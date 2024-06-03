#!/bin/bash
#
# سكريبت لتوزيع كوميتات مشروع Maven + JavaFX على فترة زمنية سابقة
# يرتب الملفات منطقياً (pom.xml -> models -> repository -> service -> controller -> FXML views -> tests)
# ويوزعها على تواريخ محددة مسبقاً بفجوات أيام فارغة بين الكوميتات.
#
# طريقة الاستخدام:
#   1) ضع هذا السكريبت في المجلد الأب لمشروعك (بجانب مجلد المشروع نفسه)
#   2) عدّل المتغيرات في قسم الإعدادات أدناه
#   3) نفّذ: bash spread_commits.sh
#
# ملاحظة: السكريبت يأخذ ملفات مشروعك الحقيقية (pom.xml, src/main/java, src/main/resources/*.fxml...)
# ويقسمها إلى مجموعات، ثم يعمل commit لكل مجموعة بتاريخ مزيّف ضمن الفترة المحددة.

# ملاحظة: لا نستخدم "set -e" هنا عمداً — فشل عملية واحدة (مثل commit فارغ)
# يجب ألا يوقف بقية السكريبت، بل يجب تخطيه والمتابعة.

# ================= الإعدادات =================
PROJECT_DIR="."             # مسار مشروعك
START_DATE="2024-02-25"           # تاريخ أول كوميت
NUM_DAYS=100                       # 25/2/2024 -> 12/5/2024 (78 يوم بالضبط)
MIN_GAP_DAYS=2                    # أقل عدد أيام فارغة بين كوميت وآخر
MAX_GAP_DAYS=5                    # أكثر عدد أيام فارغة بين كوميت وآخر
# ملاحظة: كوميت واحد بالضبط في كل يوم نشط (لا يوجد MIN/MAX_COMMITS_PER_DAY بعد الآن)
GIT_REMOTE_URL=""                 # اتركه فارغاً، الريبو موجود مسبقاً
BRANCH_NAME="main"
# ===============================================

if [ ! -d "$PROJECT_DIR" ]; then
  echo "خطأ: المجلد $PROJECT_DIR غير موجود. عدّل المتغير PROJECT_DIR."
  exit 1
fi

cd "$PROJECT_DIR"

# تهيئة git إذا لم يكن مهيأ
if [ ! -d ".git" ]; then
  git init
  git checkout -b "$BRANCH_NAME"
fi

# إعداد remote إذا تم توفيره
if [ -n "$GIT_REMOTE_URL" ]; then
  if ! git remote | grep -q origin; then
    git remote add origin "$GIT_REMOTE_URL"
  fi
fi

# ===== فرض هوية صحيحة (مؤلف + مُرسِل) للكوميتات =====
# مهم جداً: بعض بيئات bash على ويندوز/WSL تحتوي متغيرات بيئة قديمة
# مثل GIT_AUTHOR_EMAIL="root@..." موروثة من النظام، وهذه تتجاوز إعدادات
# git config عند تحديد "المؤلف" تحديداً (حتى لو كان user.email مضبوطاً بشكل صحيح).
# لذلك نفرض القيم الصحيحة صراحةً هنا لكل عمليات commit في هذا السكريبت.
CONFIGURED_NAME=$(git config user.name)
CONFIGURED_EMAIL=$(git config user.email)

if [ -z "$CONFIGURED_NAME" ] || [ -z "$CONFIGURED_EMAIL" ]; then
  echo "خطأ: لم يتم ضبط git config user.name / user.email بعد."
  echo "نفّذ أولاً: git config --global user.name \"اسمك\" && git config --global user.email \"بريدك\""
  exit 1
fi

export GIT_AUTHOR_NAME="$CONFIGURED_NAME"
export GIT_AUTHOR_EMAIL="$CONFIGURED_EMAIL"
export GIT_COMMITTER_NAME="$CONFIGURED_NAME"
export GIT_COMMITTER_EMAIL="$CONFIGURED_EMAIL"

echo "سيتم استخدام الهوية: $CONFIGURED_NAME <$CONFIGURED_EMAIL> لكل الكوميتات"

# ===== 1. تجميع كل الملفات غير المتتبعة/المعدّلة =====
# نستخدم أوامر git تُرجع مسارات نظيفة مباشرة (بدون تحليل نص يدوي)
# لتجنب مشاكل الملفات المُعاد تسميتها (rename) أو الأسماء التي تحتوي مسافات.
mapfile -t MODIFIED_FILES < <(git diff --name-only)
mapfile -t UNTRACKED_FILES < <(git ls-files --others --exclude-standard)
mapfile -t ALL_FILES < <(printf '%s\n' "${MODIFIED_FILES[@]}" "${UNTRACKED_FILES[@]}" | sed '/^$/d' | sort -u)

if [ ${#ALL_FILES[@]} -eq 0 ]; then
  echo "لا توجد ملفات جديدة لعمل commit لها. تأكد أن ملفات المشروع موجودة في $PROJECT_DIR"
  exit 1
fi

TOTAL_FILES=${#ALL_FILES[@]}
echo "عدد الملفات المكتشفة: $TOTAL_FILES"

# ===== 1.5. ترتيب الملفات حسب تسلسل تطور منطقي (Maven + JavaFX) =====
# كل نمط يمثل مرحلة في تطور المشروع. الملفات تُرتب حسب أول نمط تطابقه.
PRIORITY_PATTERNS=(
  "(^|/)pom\.xml$"
  "(^|/)mvnw(\.cmd)?$|(^|/)\.mvn/"
  "(^|/)\.gitignore$"
  "(^|/)module-info\.java$"
  "(^|/)(model|models|entity|entities)/.*\.java$"
  "(^|/)(repository|repositories|dao)/.*\.java$"
  "(^|/)(service|services)/.*\.java$"
  "(^|/)(controller|controllers)/.*\.java$"
  "(^|/)(Main|App|Application)\.java$"
  "(^|/)src/main/resources/.*\.fxml$"
  "(^|/)src/main/resources/.*\.(css|properties)$"
  "(^|/)src/main/resources/"
  "(^|/)src/test/"
  "(^|/)src/main/java/.*\.java$"
  "(README|readme|docs/)"
)

CATEGORY_MESSAGES=(
  "Initial Maven project setup (pom.xml)"
  "Add Maven wrapper"
  "Add .gitignore"
  "Add module configuration"
  "Add domain models"
  "Add repositories/DAO layer"
  "Add service layer"
  "Add controllers"
  "Add application entry point"
  "Add FXML views"
  "Add resources (styles/config)"
  "Add additional resources"
  "Add tests"
  "Add core Java classes"
  "Update documentation"
)
FALLBACK_MESSAGE="Add project files"

# رسائل عامة تُستخدم بين الحين والآخر لإضفاء طابع واقعي (مثل تصحيح خطأ بسيط)
MISC_MESSAGES=("Fix bug" "Refactor code" "Improve validation" "Fix typo" "Update dependencies" "Minor cleanup")

get_priority() {
  local f="$1"
  local i=0
  for pat in "${PRIORITY_PATTERNS[@]}"; do
    if echo "$f" | grep -qE "$pat"; then
      echo "$i"
      return
    fi
    i=$((i+1))
  done
  echo "${#PRIORITY_PATTERNS[@]}"
}

SCORED_FILES=()
for f in "${ALL_FILES[@]}"; do
  p=$(get_priority "$f")
  SCORED_FILES+=("$p|$f")
done

mapfile -t ALL_FILES < <(printf '%s\n' "${SCORED_FILES[@]}" | sort -t'|' -k1,1n -k2,2 | cut -d'|' -f2-)
mapfile -t FILE_PRIORITIES < <(printf '%s\n' "${SCORED_FILES[@]}" | sort -t'|' -k1,1n -k2,2 | cut -d'|' -f1)

# ===== 2. بناء قائمة الأيام النشطة بفجوة ثابتة (2-5 أيام فارغة) بين كل كوميت والذي يليه =====
declare -a DAILY_COMMIT_COUNTS
for ((d=0; d<NUM_DAYS; d++)); do
  DAILY_COMMIT_COUNTS[d]=0
done

TOTAL_COMMITS=0
d=0
while [ "$d" -lt "$NUM_DAYS" ]; do
  DAILY_COMMIT_COUNTS[d]=1
  TOTAL_COMMITS=$((TOTAL_COMMITS + 1))
  empty_days=$(( (RANDOM % (MAX_GAP_DAYS - MIN_GAP_DAYS + 1)) + MIN_GAP_DAYS ))
  d=$((d + empty_days + 1))
done

# نضمن أن آخر يوم في المدى الزمني يحتوي كوميتاً أيضاً، حتى تصل التواريخ فعلياً لنهاية الفترة
LAST_DAY=$((NUM_DAYS - 1))
if [ "${DAILY_COMMIT_COUNTS[$LAST_DAY]}" -eq 0 ]; then
  DAILY_COMMIT_COUNTS[$LAST_DAY]=1
  TOTAL_COMMITS=$((TOTAL_COMMITS + 1))
fi

if [ "$TOTAL_COMMITS" -eq 0 ]; then
  echo "لم يتم تحديد أي كوميتات. أعد المحاولة."
  exit 1
fi

echo "إجمالي عدد الكوميتات التي ستُنشأ: $TOTAL_COMMITS"

# ===== 3. توزيع دقيق للملفات على كل الكوميتات (بدون تقريب زائد يستهلك الملفات مبكراً) =====
# base = العدد الأساسي لكل كوميت، والباقي (remainder) يُوزَّع بالتساوي عبر كل الكوميتات
# بدل تكديسه في البداية، حتى لا تنفد الملفات قبل الوصول لنهاية الفترة الزمنية.
BASE_FILES_PER_COMMIT=$(( TOTAL_FILES / TOTAL_COMMITS ))
REMAINDER=$(( TOTAL_FILES % TOTAL_COMMITS ))

declare -a BATCH_SIZES
for ((i=0; i<TOTAL_COMMITS; i++)); do
  BATCH_SIZES[i]=$BASE_FILES_PER_COMMIT
done

if [ "$REMAINDER" -gt 0 ]; then
  step=$(( TOTAL_COMMITS / REMAINDER ))
  [ "$step" -lt 1 ] && step=1
  r=0
  i=0
  while [ "$r" -lt "$REMAINDER" ] && [ "$i" -lt "$TOTAL_COMMITS" ]; do
    BATCH_SIZES[i]=$(( BATCH_SIZES[i] + 1 ))
    r=$((r + 1))
    i=$((i + step))
  done
fi

index=0
commit_num=0
planned_idx=0

for ((d=0; d<NUM_DAYS; d++)); do
  day_commits=${DAILY_COMMIT_COUNTS[d]}
  if [ "$day_commits" -eq 0 ]; then
    continue
  fi

  current_date=$(date -d "$START_DATE +$d day" +%Y-%m-%d 2>/dev/null || date -j -v+"$d"d -f "%Y-%m-%d" "$START_DATE" +%Y-%m-%d)

  for ((c=0; c<day_commits; c++)); do
    if [ "$index" -ge "$TOTAL_FILES" ]; then
      break 2
    fi

    this_batch_size=${BATCH_SIZES[$planned_idx]:-1}
    planned_idx=$((planned_idx + 1))

    if [ "$this_batch_size" -lt 1 ]; then
      continue
    fi

    # اختيار مجموعة ملفات لهذا الكوميت (بالترتيب المنطقي بعد الفرز)
    batch=("${ALL_FILES[@]:index:this_batch_size}")
    batch_priority="${FILE_PRIORITIES[$index]}"
    index=$((index + this_batch_size))

    if [ ${#batch[@]} -eq 0 ]; then
      continue
    fi

    git add -- "${batch[@]}" 2>/dev/null || true

    # إذا لم يتم تسجيل أي تغيير فعلي (staged) لأي سبب، تخطَّ هذه الدفعة بدل الفشل
    if git diff --cached --quiet; then
      echo "تخطي دفعة فارغة (${#batch[@]} مسار لم يُسجَّل منها شيء فعلي)"
      continue
    fi

    # توليد وقت عشوائي خلال ساعات العمل (9 صباحاً - 11 مساءً)
    hour=$(( (RANDOM % 14) + 9 ))
    minute=$((RANDOM % 60))
    second=$((RANDOM % 60))
    commit_datetime=$(printf "%sT%02d:%02d:%02d" "$current_date" "$hour" "$minute" "$second")

    # رسالة الكوميت تُبنى حسب فئة الملفات (models/views/frontend..)، مع رسائل عامة عشوائية أحياناً لواقعية أكثر
    if [ -n "$batch_priority" ] && [ "$batch_priority" -lt "${#CATEGORY_MESSAGES[@]}" ]; then
      commit_msg="${CATEGORY_MESSAGES[$batch_priority]}"
    else
      commit_msg="$FALLBACK_MESSAGE"
    fi

    misc_roll=$((RANDOM % 100))
    if [ "$misc_roll" -lt 15 ] && [ "$commit_num" -gt 0 ]; then
      misc_index=$((RANDOM % ${#MISC_MESSAGES[@]}))
      commit_msg="${MISC_MESSAGES[$misc_index]}"
    fi

    if GIT_AUTHOR_DATE="$commit_datetime" GIT_COMMITTER_DATE="$commit_datetime" \
      git commit -m "$commit_msg" --quiet; then
      commit_num=$((commit_num + 1))
      echo "[$commit_num/$TOTAL_COMMITS] تم الكوميت بتاريخ $commit_datetime - $commit_msg (${#batch[@]} ملف)"
    else
      echo "تحذير: فشل commit لهذه الدفعة، تم تخطيها والمتابعة"
    fi
  done
done

# إضافة أي ملفات متبقية في كوميت أخير (أو عدة كوميتات إذا تبقى الكثير)
if [ "$index" -lt "$TOTAL_FILES" ]; then
  remaining=("${ALL_FILES[@]:index}")
  git add -- "${remaining[@]}" 2>/dev/null || true
  if git diff --cached --quiet; then
    echo "لا توجد ملفات متبقية فعلية لعمل commit لها."
  else
    last_date=$(date -d "$START_DATE +$((NUM_DAYS-1)) day" +%Y-%m-%d 2>/dev/null || date -j -v+"$((NUM_DAYS-1))"d -f "%Y-%m-%d" "$START_DATE" +%Y-%m-%d)
    commit_datetime="${last_date}T20:00:00"
    if GIT_AUTHOR_DATE="$commit_datetime" GIT_COMMITTER_DATE="$commit_datetime" \
      git commit -m "Final touches" --quiet; then
      echo "تم كوميت الملفات المتبقية بتاريخ $commit_datetime"
    else
      echo "تحذير: فشل كوميت الملفات المتبقية. تحقق من: git status"
    fi
  fi
fi

echo ""
echo "انتهى! تم إنشاء $((commit_num)) كوميت (أو أكثر بسبب الكوميت الأخير)."
echo "للتحقق: git log --pretty=format:'%h %ad %s' --date=short"
echo ""
echo "لرفع المشروع إلى GitHub:"
echo "  git push -u origin $BRANCH_NAME"
