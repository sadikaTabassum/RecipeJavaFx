import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeApp extends Application {
    private RecipeManager manager;
    private final ObservableList<Recipe> recipeData = FXCollections.observableArrayList();

    private TableView<Recipe> tableView;
    private TextField nameField;
    private ComboBox<Integer> servingsBox;
    private TextField caloriesField;
    private TextArea ingredientsArea;
    private TextArea procedureArea;
    private ComboBox<String> searchTypeBox;
    private TextField searchField;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        try {
            manager = new RecipeManager("recipes.txt");
            recipeData.setAll(manager.getAllRecipes());
        } catch (IOException e) {
            showError("File Error", "Could not load recipes.txt", e.getMessage());
            return;
        }

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(createHeader());
        root.setCenter(createTableSection());
        root.setRight(createFormSection());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1150, 680);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Recipe Manager - Purple Pink JavaFX Frontend");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
        Label title = new Label(" Recipe Manager");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Manage your recipe");
        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(5, title, subtitle);
        header.getStyleClass().add("header-card");
        return header;
    }

    private VBox createTableSection() {
        tableView = new TableView<>();
        tableView.getStyleClass().add("recipe-table");
        tableView.setItems(recipeData);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setPlaceholder(new Label("No recipes found. Add a recipe from the form on the right 💜"));

        TableColumn<Recipe, String> nameColumn = new TableColumn<>("Recipe Name");
        // Important fix: use a lambda instead of PropertyValueFactory.
        // This prevents blank recipe names in some JavaFX/default-package setups.
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setMinWidth(150);

        TableColumn<Recipe, Number> servingsColumn = new TableColumn<>("Servings");
        servingsColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getServings()));
        servingsColumn.setMaxWidth(95);

        TableColumn<Recipe, Number> caloriesColumn = new TableColumn<>("Calories");
        caloriesColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCalories()));
        caloriesColumn.setMaxWidth(100);

        TableColumn<Recipe, String> ingredientsColumn = new TableColumn<>("Ingredients");
        ingredientsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.join(", ", data.getValue().getIngredients())));
        ingredientsColumn.setMinWidth(220);

        TableColumn<Recipe, String> procedureColumn = new TableColumn<>("Procedure");
        procedureColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProcedure()));
        procedureColumn.setMinWidth(240);

        tableView.getColumns().addAll(nameColumn, servingsColumn, caloriesColumn, ingredientsColumn, procedureColumn);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldRecipe, selectedRecipe) -> {
            if (selectedRecipe != null) {
                fillForm(selectedRecipe);
            }
        });

        HBox searchBox = createSearchSection();
        VBox tableCard = new VBox(12, searchBox, tableView);
        tableCard.getStyleClass().add("content-card");
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableCard;
    }

    private HBox createSearchSection() {
        Label searchLabel = new Label("Search By");
        searchLabel.getStyleClass().add("field-label");

        searchTypeBox = new ComboBox<>();
        searchTypeBox.setItems(FXCollections.observableArrayList(
                "All", "Servings", "Ingredient", "Calories Less Than", "Calories Greater Than"
        ));
        searchTypeBox.setValue("All");
        searchTypeBox.setPrefWidth(190);

        searchField = new TextField();
        searchField.setPromptText("Search value...");
        searchField.setPrefWidth(230);

        Button searchButton = new Button(" Search");
        searchButton.getStyleClass().addAll("button", "primary-button");
        searchButton.setOnAction(e -> searchRecipes());

        Button showAllButton = new Button(" Show All");
        showAllButton.getStyleClass().addAll("button", "secondary-button");
        showAllButton.setOnAction(e -> refreshTable());

        HBox searchBox = new HBox(10, searchLabel, searchTypeBox, searchField, searchButton, showAllButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        return searchBox;
    }

    private VBox createFormSection() {
        nameField = new TextField();
        nameField.setPromptText("Example: Chicken Soup");

        servingsBox = new ComboBox<>();
        servingsBox.setItems(FXCollections.observableArrayList(2, 3, 4, 6, 8));
        servingsBox.setPromptText("Select servings");
        servingsBox.setMaxWidth(Double.MAX_VALUE);

        caloriesField = new TextField();
        caloriesField.setPromptText("Example: 350");

        ingredientsArea = new TextArea();
        ingredientsArea.setPromptText("Write one ingredient per line\nExample:\nChicken\nSalt\nPepper");
        ingredientsArea.setPrefRowCount(5);

        procedureArea = new TextArea();
        procedureArea.setPromptText("Write cooking procedure here...");
        procedureArea.setPrefRowCount(6);

        Button addButton = new Button(" Add Recipe");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.getStyleClass().addAll("button", "primary-button");
        addButton.setOnAction(e -> addRecipe());

        Button updateButton = new Button(" Update Selected");
        updateButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.getStyleClass().addAll("button", "secondary-button");
        updateButton.setOnAction(e -> updateRecipe());

        Button deleteButton = new Button(" Delete Selected");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.getStyleClass().addAll("button", "danger-button");
        deleteButton.setOnAction(e -> deleteRecipe());

        Button clearButton = new Button(" Clear Form");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.getStyleClass().addAll("button", "ghost-button");
        clearButton.setOnAction(e -> clearForm());

        VBox form = new VBox(9,
                sectionTitle("Recipe Form"),
                createSmallHint("Fill all fields, then click Add Recipe."),
                fieldLabel("Recipe Name"), nameField,
                fieldLabel("Servings"), servingsBox,
                fieldLabel("Calories"), caloriesField,
                fieldLabel("Ingredients"), ingredientsArea,
                fieldLabel("Procedure"), procedureArea,
                addButton, updateButton, deleteButton, clearButton
        );
        form.getStyleClass().add("form-card");
        form.setPrefWidth(360);
        return form;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private Label createSmallHint(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("small-hint");
        return label;
    }

    private HBox createStatusBar() {
        statusLabel = new Label("Ready 💜");
        statusLabel.getStyleClass().add("status-label");
        HBox statusBar = new HBox(statusLabel);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        return statusBar;
    }

    private Recipe readRecipeFromForm() {
        String name = nameField.getText().trim();
        Integer servings = servingsBox.getValue();
        int calories;

        if (servings == null) {
            throw new IllegalArgumentException("Please select servings: 2, 3, 4, 6, or 8.");
        }

        try {
            calories = Integer.parseInt(caloriesField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Calories must be a valid number.");
        }

        List<String> ingredients = Arrays.stream(ingredientsArea.getText().split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        String procedure = procedureArea.getText().trim();
        return new Recipe(name, servings, calories, ingredients, procedure);
    }

    private void addRecipe() {
        try {
            Recipe recipe = readRecipeFromForm();
            manager.addRecipe(recipe);
            refreshTable();
            clearForm();
            setStatus("Recipe added successfully ✅");
        } catch (Exception e) {
            showError("Add Failed", "Could not add recipe", e.getMessage());
        }
    }

    private void updateRecipe() {
        Recipe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a recipe from the table first.");
            return;
        }

        try {
            Recipe updatedRecipe = readRecipeFromForm();
            boolean updated = manager.updateRecipe(selected.getName(), updatedRecipe);
            if (updated) {
                refreshTable();
                clearForm();
                setStatus("Recipe updated successfully ✅");
            } else {
                showWarning("Not Found", "Selected recipe was not found.");
            }
        } catch (Exception e) {
            showError("Update Failed", "Could not update recipe", e.getMessage());
        }
    }

    private void deleteRecipe() {
        Recipe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a recipe from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Recipe");
        confirm.setHeaderText("Delete selected recipe?");
        confirm.setContentText("Recipe: " + selected.getName());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = manager.deleteRecipe(selected.getName());
                if (deleted) {
                    refreshTable();
                    clearForm();
                    setStatus("Recipe deleted successfully ");
                } else {
                    showWarning("Not Found", "Selected recipe was not found.");
                }
            } catch (IOException e) {
                showError("Delete Failed", "Could not delete recipe", e.getMessage());
            }
        }
    }

    private void searchRecipes() {
        String type = searchTypeBox.getValue();
        String value = searchField.getText().trim();

        try {
            List<Recipe> results;
            switch (type) {
                case "All" -> results = manager.getAllRecipes();
                case "Servings" -> results = manager.searchByServings(Integer.parseInt(value));
                case "Ingredient" -> results = manager.searchByIngredient(value);
                case "Calories Less Than" -> results = manager.searchByCaloriesLessThan(Integer.parseInt(value));
                case "Calories Greater Than" -> results = manager.searchByCaloriesGreaterThan(Integer.parseInt(value));
                default -> results = manager.getAllRecipes();
            }
            recipeData.setAll(results);
            setStatus("Search completed. Found " + results.size() + " recipe(s). 🔎");
        } catch (NumberFormatException e) {
            showError("Invalid Search", "Search value must be a number for this search type.", e.getMessage());
        } catch (Exception e) {
            showError("Search Failed", "Could not complete search", e.getMessage());
        }
    }

    private void refreshTable() {
        recipeData.setAll(manager.getAllRecipes());
        tableView.getSelectionModel().clearSelection();
        setStatus("Showing all recipes. Total: " + recipeData.size() + " ✨");
    }

    private void fillForm(Recipe recipe) {
        nameField.setText(recipe.getName());
        servingsBox.setValue(recipe.getServings());
        caloriesField.setText(String.valueOf(recipe.getCalories()));
        ingredientsArea.setText(String.join(System.lineSeparator(), recipe.getIngredients()));
        procedureArea.setText(recipe.getProcedure());
    }

    private void clearForm() {
        nameField.clear();
        servingsBox.setValue(null);
        caloriesField.clear();
        ingredientsArea.clear();
        procedureArea.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
