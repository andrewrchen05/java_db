import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class bufreader {
    public static void main(String args[]) {

        while (true) {
            try {
                String username = JOptionPane.showInputDialog("Enter username: ");
                Integer age = Integer.parseInt(JOptionPane.showInputDialog("Enter age: "));

                if (age < 18) {
                    System.out.println("You cannot be less than 18 years old.");
                    
                } else {
                    System.out.println("Welcome " + username + "!");
                }
            } catch (Exception e) {
                System.out.println("There is an error with input");
            }
        }
    }
}