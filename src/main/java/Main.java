import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        algorithm1();
        algorithm2();

        simpleTipAboutScannerAndStream();

    }

    private static void simpleTipAboutScannerAndStream() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("write a line please:");

        //next line return the whole line!
        String line = scanner.nextLine();

        /*above line read all input data and save it to line String
         * and when you try for second time it will read next input line
         */

        String secondTry = scanner.nextLine();

        //lets print both of them:

        System.out.println("first try to read line:" + line);
        System.out.println("second try to read line:" + secondTry);

    }

    private static void algorithm2() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("enter number: ");

        //next line return the whole line!
        String line = scanner.nextLine();

        // trim method remove space from start and end of line
        line = line.trim();
        int sum = 0;

        for (int i = 0; i < line.length(); i++) {
            //charAt return a character abn you need to convert it to number
            //ex. : getNumericValue of '1' is 1
            sum += Character.getNumericValue(line.charAt(i));
        }

        System.out.println("sum of value is:" + sum);

    }

    private static void algorithm1() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("enter number: ");
        int digits = scanner.nextInt();
        int sum = 0;
        while (digits > 0) {
            sum += digits % 10;
            digits = digits / 10;
        }

        System.out.println("sum of value is:" + sum);
    }


}
