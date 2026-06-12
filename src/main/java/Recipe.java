import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe {
    private String name;
    private int servings;
    private int calories;
    private List<String> ingredients = new ArrayList<>();
    private String procedure;

    public Recipe(String name, int servings, int calories, List<String> ingredients, String procedure) {
        this.name = name;
        this.servings = servings;
        this.calories = calories;
        this.ingredients = ingredients;
        this.procedure = procedure;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public int getCalories() {
        return calories;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    @Override
    public String toString() {
        return name + "," + servings + "," + calories + "," + String.join("|", ingredients) + "," + procedure;
    }

    public static Recipe fromString(String data) {
        String[] parts = data.split(",", 5);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid recipe line: " + data);
        }

        String name = parts[0];
        int servings = Integer.parseInt(parts[1]);
        int calories = Integer.parseInt(parts[2]);
        List<String> ingredients = parts[3].isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(parts[3].split("\\|")));
        String procedure = parts[4];

        return new Recipe(name, servings, calories, ingredients, procedure);
    }

    public void display() {
        System.out.println("Recipe Name: " + name);
        System.out.println("Ingredients: " + String.join(", ", ingredients));
        System.out.println("Procedure: " + procedure);
        System.out.println("------------------------------------------");
    }
}
