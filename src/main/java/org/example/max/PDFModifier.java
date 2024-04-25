package org.example.max;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;

public class PDFModifier {

    static String name = "";
    static String last_name = "";
    static int number = 0;

    static String newhtmlFilePath = "src/main/resources/documents_word/fiche-de-paie-2.html";
    static String og_html_path = "/documents_word/fiche-de-paie-1.html";

    public static void SAVE(int month, int year) {
        // Create a new folder on the Desktop named "malloc"
        String folder_name = "fiche_de_paiex(" + year + "-" + month + "-1)";
        String userHome = System.getProperty("user.home");
        File mallocFolder = new File(userHome + "/Desktop/" + folder_name);
        if (!mallocFolder.exists()) {
            mallocFolder.mkdir();
        }
        String b;
        if (number == 0) {
            b = "/" + "fiche_de_paiex(" + name + "_" + last_name + ").pdf";
        } else {
            b = "/" + "fiche_de_paiex(" + name + "_" + last_name + "_" + "(" + number + ")" + ".pdf";
        }
        // Output PDF path
        String outputPDFPath = mallocFolder.getPath() + b;
        save_pdf(outputPDFPath);
        aplyy_changes(outputPDFPath);
    }

    public static void main(String[] args) {
        String[] replacements = new String[]{
                "maxxx", "22222222", "33333333", "44444444", "55555555",
                "66666666", "77777777", "88888888", "99999999", "10101010",
                "11111111", "12121212", "13131313", "14141414", "15151515",
                "16161616", "17171717", "18181818", "757575", "876543",
                "21212121", "22222222"
        };
        try {
            change(replacements);
            SAVE(7, 2024); // Example month and year
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void aplyy_changes(String inputFilePath) {
        float positionX1 = 354; // X position of the first line
        float positionY1 = 165; // Y starting position of the first line
        float length1 = 365;    // Length of the first line
        float lineWidth1 = 2;   // Width of the first line

        float positionX2 = 489; // X position of the second line
        float positionY2 = 160; // Y starting position of the second line
        float length2 = 377;    // Length of the second line
        float lineWidth2 = 114;   // Width of the second line

        try {
            // Load existing PDF
            PDDocument document = PDDocument.load(new File(inputFilePath));
            PDPage page = document.getPage(0); // Get the first page

            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Set line properties for the first line (black)
            contentStream.setLineWidth(lineWidth1);
            contentStream.setStrokingColor(Color.black);
            contentStream.moveTo(positionX1, positionY1);
            contentStream.lineTo(positionX1, positionY1 + length1);
            contentStream.stroke();

            // Set line properties for the second line (white)
            contentStream.setLineWidth(lineWidth2);
            contentStream.setStrokingColor(Color.white);
            contentStream.moveTo(positionX2, positionY2);
            contentStream.lineTo(positionX2, positionY2 + length2);
            contentStream.stroke();

            // Close the content stream
            contentStream.close();

            // Save modified document
            document.save(inputFilePath);
            document.close();

            System.out.println("Vertical lines added successfully to PDF.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String sourcePath, String destinationPath) throws IOException {
        InputStream inputStream = PDFModifier.class.getResourceAsStream(sourcePath);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + sourcePath);
        }

        File destinationFile = new File(destinationPath);

        // Create parent directories for destination file if they don't exist
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }

        // Perform file copy
        try (OutputStream outStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
        }
    }

    private static final int TARGET_LENGTH = "x22222222222222222222222222222222".length();

    public static String padString(String input) {
        return input.length() >= TARGET_LENGTH ? input : String.format("%-" + TARGET_LENGTH + "s", input);
    }

    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        String month = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int day = currentDate.getDayOfMonth();
        return month + "-" + day;
    }

    public static void change(String[] replacements) throws IOException {
        copyFile(og_html_path, newhtmlFilePath);
        String htmlContent = readFileAsString(newhtmlFilePath);
        number = number + 1;

        htmlContent = replaceText(htmlContent, "x22222222222222222222222222222", name = replacements[0]);
        htmlContent = replaceText(htmlContent, "x3333333333333333333333333333", last_name = replacements[1]);
        htmlContent = replaceText(htmlContent, "x44444444444444444444444", replacements[2]);
        htmlContent = replaceText(htmlContent, "x555", replacements[3]);
        htmlContent = replaceText(htmlContent, "vvvvvv", replacements[4]);
        htmlContent = replaceText(htmlContent, "kkkkkkkk", replacements[5]);
        htmlContent = replaceText(htmlContent, "pppppppppppppppp", replacements[6]);
        htmlContent = replaceText(htmlContent, "x191919191919", replacements[7]);
        htmlContent = replaceText(htmlContent, "x999999999999", replacements[8]);
        htmlContent = replaceText(htmlContent, "x101010101010", replacements[9]);
        htmlContent = replaceText(htmlContent, "x121212121212", replacements[11]);
        htmlContent = replaceText(htmlContent, "x131313131313", replacements[12]);
        htmlContent = replaceText(htmlContent, "x141414141414", replacements[13]);
        htmlContent = replaceText(htmlContent, "x151515151515", replacements[14]);
        htmlContent = replaceText(htmlContent, "9x171717171717", replacements[16]);
        htmlContent = replaceText(htmlContent, "x212121212121", replacements[17]);
        htmlContent = replaceText(htmlContent, "x313131313131", replacements[18]);
        htmlContent = replaceText(htmlContent, "x242424242424", replacements[19]);
        htmlContent = replaceText(htmlContent, "x252525252525", replacements[20]);
        htmlContent = replaceText(htmlContent, "x262626262626", replacements[21]);
        htmlContent = replaceText(htmlContent, "x181818181818", replacements[10]);
        htmlContent = replaceText(htmlContent, "x161616161616", replacements[15]);
        htmlContent = replaceText(htmlContent, "x232323232323", getCurrentDate());

        // Write modified HTML content back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newhtmlFilePath))) {
            writer.write(htmlContent);
        }
    }

    public static String readFileAsString(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
    }

    public static String replaceText(String originalText, String target, String replacement) {
        if (replacement == null) {
            replacement = ""; // Replace with empty string if replacement is null
        }
        return originalText.replace(target, replacement);
    }

    public static byte[] convertHtmlToPdfBytes(String htmlString) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlString, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new IOException("Error converting HTML to PDF", e);
        } finally {
            os.close();
        }
        return os.toByteArray();
    }

    public static void save_pdf(String end_path) {
        try {
            // Read HTML content from file
            String htmlString = new String(Files.readAllBytes(Paths.get(newhtmlFilePath)), StandardCharsets.UTF_8);

            byte[] pdfBytes = convertHtmlToPdfBytes(htmlString);

            // Save the PDF bytes to a file
            try (FileOutputStream fos = new FileOutputStream(end_path)) {
                fos.write(pdfBytes);
            }

            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
