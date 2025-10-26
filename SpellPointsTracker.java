import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SpellPointsTracker extends Application {

    private int currentPoints = 0;

    // Spell point cost by spell level
    private final Map<Integer, Integer> spellCost = Map.ofEntries(
            Map.entry(1, 2), Map.entry(2, 3), Map.entry(3, 5), Map.entry(4, 6),
            Map.entry(5, 7), Map.entry(6, 9), Map.entry(7, 10), Map.entry(8, 11), Map.entry(9, 13)
    );

    // Spell points per caster level
    private final Map<Integer, Integer> casterLevel = new HashMap<>() {{
        put(1, 4); put(2, 6); put(3, 14); put(4, 17); put(5, 27);
        put(6, 32); put(7, 38); put(8, 44); put(9, 57); put(10, 64);
        put(11, 73); put(12, 73); put(13, 83); put(14, 83); put(15, 94);
        put(16, 94); put(17, 107); put(18, 114); put(19, 123); put(20, 133);
    }};

    private Label spellPointsLabel;
    private Label errorLabel;
    private ComboBox<Integer> levelComboBox;
    private TextField spellLevelInput;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spell Points Tracker");

        // --- UI Elements ---
        spellPointsLabel = new Label("Spell Points: " + currentPoints);
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Caster level dropdown
        Label casterLabel = new Label("Caster Level:");
        levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll(casterLevel.keySet());
        levelComboBox.setValue(1);
        levelComboBox.setOnAction(e -> updateCasterLevel());

        HBox casterBox = new HBox(10, casterLabel, levelComboBox);
        casterBox.setPadding(new Insets(10));

        // Modify spell points section
        Label modifyLabel = new Label("Modify Spell Points by Spell Level:");
        spellLevelInput = new TextField();
        spellLevelInput.setPrefWidth(50);
        Button modifyButton = new Button("Modify");
        modifyButton.setOnAction(e -> modifyBySpellLevel());

        HBox modifyBox = new HBox(10, modifyLabel, spellLevelInput, modifyButton);
        modifyBox.setPadding(new Insets(10));

        // Reset button
        Button resetButton = new Button("Reset Spell Points");
        resetButton.setOnAction(e -> resetSpellPoints());

        VBox layout = new VBox(15, casterBox, spellPointsLabel, modifyBox, resetButton, errorLabel);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 400, 220);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCasterLevel(); // Set initial points
    }

    // --- Logic ---

    private void updateCasterLevel() {
        int level = levelComboBox.getValue();
        currentPoints = casterLevel.getOrDefault(level, 0);
        spellPointsLabel.setText("Spell Points: " + currentPoints);
        errorLabel.setText("");
    }

    private void modifyBySpellLevel() {
        try {
            int spellLevel = Integer.parseInt(spellLevelInput.getText());
            Integer cost = spellCost.get(spellLevel);

            if (cost == null) {
                errorLabel.setText("Invalid spell level entered.");
                return;
            }

            currentPoints -= cost;
            if (currentPoints < 0) currentPoints = 0;

            spellPointsLabel.setText("Spell Points: " + currentPoints);
            errorLabel.setText("");

        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid number.");
        }
    }

    private void resetSpellPoints() {
        currentPoints = 0;
        spellPointsLabel.setText("Spell Points: " + currentPoints);
        errorLabel.setText("");
    }

    public static void main(String[] args) {
        launch();
    }
}
