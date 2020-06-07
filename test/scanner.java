import java.util.Scanner;

class scanner {
    public static void main(String args[]) {

        while (true) {
            try {
                System.out.println("Enter username:");
                
                Scanner in = new Scanner(System.in);
    
                String s = in.nextLine();

                if (s.equals("achen134")) {
                    System.out.println("Welcome back, " + s);
                    break;
                } else {
                    System.out.println("This username does not exist, please try again.");
                }

                
            } catch (Exception e) {
                System.out.println("There is an error with input");
            }

        }

    }

}