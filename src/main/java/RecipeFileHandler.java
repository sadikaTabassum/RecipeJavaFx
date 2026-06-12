import java.io.*;
import java.util.ArrayList;
import java.util.List;

class RecipeFileHandler {
    private final String fileName;

    public RecipeFileHandler(String fileName) {
        this.fileName = fileName;
    }

    public List<Recipe> loadRecipes() throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    recipes.add(Recipe.fromString(line));
                }
            }
        }
        return recipes;
    }

    public void saveRecipes(List<Recipe> recipes) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Recipe recipe : recipes) {
                bw.write(recipe.toString());
                bw.newLine();
            }
        }
    }

    public void addRecipe(Recipe recipe) throws IOException {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(recipe.toString());
            bw.newLine();
        }
    }
}
