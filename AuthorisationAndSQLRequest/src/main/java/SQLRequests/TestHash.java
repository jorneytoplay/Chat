package SQLRequests;

import java.util.Scanner;

public class TestHash {
    public static void main(String[] args) {
        String password;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your password: ");
        password = scanner.nextLine();
        System.out.println("Your hashpass : " + password.hashCode());
        System.out.println("Your hashpass with salt : " + password.hashCode() * 12);
    }
}
