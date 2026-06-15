import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class RecipeManager {
    private final Scanner scanner;
    private final RecipeFileHandler fileHandler;
    private List<Recipe> recipes;


    public RecipeManager(String fileName) throws IOException {
        this.scanner = null;
        this.fileHandler = new RecipeFileHandler(fileName);
        this.recipes = fileHandler.loadRecipes();
    }

    public RecipeManager(Scanner scanner, String fileName) throws IOException {
        this.scanner = scanner;
        this.fileHandler = new RecipeFileHandler(fileName);
        this.recipes = fileHandler.loadRecipes();
    }


    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }

    public void addRecipe(Recipe recipe) throws IOException {
        validateRecipe(recipe);
        recipes.add(recipe);
        fileHandler.saveRecipes(recipes);
    }

    public boolean updateRecipe(String oldName, Recipe updatedRecipe) throws IOException {
        validateRecipe(updatedRecipe);

        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getName().equalsIgnoreCase(oldName)) {
                recipes.set(i, updatedRecipe);
                fileHandler.saveRecipes(recipes);
                return true;
            }
        }
        return false;
    }

    public boolean deleteRecipe(String recipeName) throws IOException {
        boolean removed = recipes.removeIf(r -> r.getName().equalsIgnoreCase(recipeName));
        if (removed) {
            fileHandler.saveRecipes(recipes);
        }
        return removed;
    }

    public List<Recipe> searchByServings(int servings) {
        return recipes.stream()
                .filter(r -> r.getServings() == servings)
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByIngredient(String ingredient) {
        String query = ingredient.toLowerCase().trim();
        return recipes.stream()
                .filter(r -> r.getIngredients().stream()
                        .anyMatch(i -> i.toLowerCase().contains(query)))
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByCaloriesLessThan(int value) {
        return recipes.stream()
                .filter(r -> r.getCalories() < value)
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByCaloriesGreaterThan(int value) {
        return recipes.stream()
                .filter(r -> r.getCalories() > value)
                .collect(Collectors.toList());
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe name cannot be empty.");
        }
        if (!(recipe.getServings() == 2 || recipe.getServings() == 3 || recipe.getServings() == 4
                || recipe.getServings() == 6 || recipe.getServings() == 8)) {
            throw new IllegalArgumentException("Servings must be 2, 3, 4, 6, or 8.");
        }
        if (recipe.getCalories() < 0) {
            throw new IllegalArgumentException("Calories cannot be negative.");
        }
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            throw new IllegalArgumentException("At least one ingredient is required.");
        }
        if (recipe.getProcedure() == null || recipe.getProcedure().trim().isEmpty()) {
            throw new IllegalArgumentException("Procedure cannot be empty.");
        }
    }


    public void run() throws IOException {
        if (scanner == null) {
            throw new IllegalStateException("Console scanner is not available. Use RecipeManager(scanner, fileName).");
        }

        while (true) {
            try {
                System.out.println("Choose option: 1-Add, 2-Edit, 3-Delete, 4-Search, 5-Exit");
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addRecipeFromConsole();
                    case 2 -> editRecipeFromConsole();
                    case 3 -> deleteRecipeFromConsole();
                    case 4 -> searchRecipeFromConsole();
                    case 5 -> {
                        System.out.println("Exiting Recipe Manager.");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (IOException e) {
                System.out.println("Error during file operation: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addRecipeFromConsole() throws IOException {
        System.out.print("Enter recipe name: ");
        String name = scanner.nextLine();
        System.out.print("Enter servings (2, 3, 4, 6, or 8): ");
        int servings = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter calories: ");
        int calories = Integer.parseInt(scanner.nextLine());
        List<String> ingredients = getIngredientsFromUser();
        System.out.print("Enter procedure: ");
        String procedure = scanner.nextLine();

        Recipe recipe = new Recipe(name, servings, calories, ingredients, procedure);
        addRecipe(recipe);
        System.out.println("Recipe added successfully!");
    }

    private List<String> getIngredientsFromUser() {
        List<String> ingredients = new ArrayList<>();
        System.out.println("Enter ingredients (type 'done' to finish):");
        while (true) {
            String ingredient = scanner.nextLine();
            if (ingredient.equalsIgnoreCase("done")) {
                break;
            }
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private void editRecipeFromConsole() throws IOException {
        System.out.print("Enter recipe name to edit: ");
        String nameToEdit = scanner.nextLine();

        for (Recipe recipe : recipes) {
            if (recipe.getName().equalsIgnoreCase(nameToEdit)) {
                editRecipeDetails(recipe);
                fileHandler.saveRecipes(recipes);
                System.out.println("Recipe updated successfully.");
                return;
            }
        }
        System.out.println("Recipe not found.");
    }

    private void editRecipeDetails(Recipe recipe) {
        System.out.println("What would you like to edit? (name/servings/calories/ingredients/recipe)");
        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "name" -> {
                System.out.print("Enter new name: ");
                recipe.setName(scanner.nextLine());
            }
            case "servings" -> {
                System.out.print("Enter new servings: ");
                recipe.setServings(Integer.parseInt(scanner.nextLine()));
            }
            case "calories" -> {
                System.out.print("Enter new calories: ");
                recipe.setCalories(Integer.parseInt(scanner.nextLine()));
            }
            case "ingredients" -> recipe.setIngredients(getIngredientsFromUser());
            case "recipe" -> {
                System.out.print("Enter new procedure: ");
                recipe.setProcedure(scanner.nextLine());
            }
            default -> System.out.println("Invalid edit option.");
        }
    }

    private void deleteRecipeFromConsole() throws IOException {
        System.out.print("Enter recipe name to delete: ");
        String nameToDelete = scanner.nextLine();
        boolean removed = deleteRecipe(nameToDelete);
        System.out.println(removed ? "Recipe deleted." : "Recipe not found.");
    }

    private void searchRecipeFromConsole() {
        System.out.println("Search by: 1-Servings, 2-Ingredient, 3-Calorie Range");
        int choice = Integer.parseInt(scanner.nextLine());
        boolean found = false;
        List<Recipe> results = new ArrayList<>();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter serving size: ");
                int size = Integer.parseInt(scanner.nextLine());
                results = searchByServings(size);
            }
            case 2 -> {
                System.out.print("Enter ingredient: ");
                String ingredient = scanner.nextLine();
                results = searchByIngredient(ingredient);
            }
            case 3 -> {
                System.out.print("Enter calorie range (less/greater number): ");
                String[] parts = scanner.nextLine().split(" ");
                if (parts.length != 2) {
                    System.out.println("Invalid input format. Please use: less 300 or greater 600");
                    return;
                }
                int value = Integer.parseInt(parts[1]);
                if (parts[0].equalsIgnoreCase("less")) {
                    results = searchByCaloriesLessThan(value);
                } else if (parts[0].equalsIgnoreCase("greater")) {
                    results = searchByCaloriesGreaterThan(value);
                }
            }
            default -> System.out.println("Invalid search option.");
        }

        for (Recipe r : results) {
            r.display();
            found = true;
        }

        if (!found) {
            System.out.println("No matching recipes found.");
        }
    }
}
