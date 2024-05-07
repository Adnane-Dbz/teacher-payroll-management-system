package org.example.max;

public class hhh {

        // Class-level variable to hold the state of x
        private static boolean x;

        public static String mix(String a, String b, boolean x) {
            hhh.x = x;
            if (b.equals("0")) {
                System.out.println("The second string is null.");
                return a + "©" + x;
            }
            return a + "©" + b + "©" + x;
        }

        public static String[] split(String a) {
            String[] parts = a.split("©");
            if (parts.length < 3 || parts[1].equals("0")) {
                System.out.println("The second part or the state is null.");
                x = Boolean.parseBoolean(parts[parts.length - 1]);
                return new String[] { parts[0], null };
            }
            x = Boolean.parseBoolean(parts[2]);
            return new String[] { parts[0], parts[1] };
        }

        public static void main(String[] args) {
            // Example usage
            String s1 = "max1";
            String s2 = "0";
            boolean initialX = true;

            String mixed = mix(s1, s2, initialX);
            System.out.println("Mixed: " + mixed);

            String[] splitStrings = split(mixed);
            System.out.println("Split: ");
            System.out.println("s1: " + splitStrings[0]);
            System.out.println("s2: " + splitStrings[1]);
            System.out.println("State of x: " + x);


        }
    }
