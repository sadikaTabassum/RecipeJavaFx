import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String fileName = "recipes.txt";
        try {
            RecipeManager manager = new RecipeManager(scanner, fileName);
            manager.run();
        } catch (IOException e) {
            System.err.println("Error initializing or running the Recipe Manager: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
