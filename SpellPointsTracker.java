import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellPointsTracker extends Application {

    private int currentPoints = 0;
    private int currentCasterLevel = 1;
    private int maxSpellSlot = 1;
    private Set<Integer> usedHighLevelSlots = new HashSet<>();

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

    // Maximum spell slot level per caster level
    private static final Map<Integer, Integer> MAX_SPELL_SLOT_BY_LEVEL = Map.ofEntries(
            Map.entry(1, 1), Map.entry(2, 1), Map.entry(3, 2), Map.entry(4, 2), Map.entry(5, 3),
            Map.entry(6, 3), Map.entry(7, 4), Map.entry(8, 4), Map.entry(9, 5), Map.entry(10, 5),
            Map.entry(11, 6), Map.entry(12, 6), Map.entry(13, 7), Map.entry(14, 7), Map.entry(15, 8),
            Map.entry(16, 8), Map.entry(17, 9), Map.entry(18, 9), Map.entry(19, 9), Map.entry(20, 9)
    );

    private Label spellPointsLabel;
    private Label errorLabel;
    private Label highLevelSlotsLabel;
    private ComboBox<Integer> levelComboBox;
    private TextField spellLevelInput;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spell Points Tracker");

        // --- UI Elements ---
        spellPointsLabel = new Label("Spell Points: " + currentPoints);
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        highLevelSlotsLabel = new Label("Used high-level slots: none");

        // Caster level dropdown
        Label casterLabel = new Label("Caster Level:");
        levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll(casterLevel.keySet());
        levelComboBox.setValue(1);
        levelComboBox.setOnAction(e -> updateCasterLevel());

        HBox casterBox = new HBox(10, casterLabel, levelComboBox, spellPointsLabel);
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

        VBox layout = new VBox(15, casterBox, modifyBox, resetButton, highLevelSlotsLabel, errorLabel);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 400, 220);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCasterLevel(); // Set initial points
    }

    // --- Logic ---

    private void updateCasterLevel() {
        currentCasterLevel = levelComboBox.getValue();
        currentPoints = casterLevel.getOrDefault(currentCasterLevel, 0);
        maxSpellSlot = MAX_SPELL_SLOT_BY_LEVEL.getOrDefault(currentCasterLevel, 1);
        spellPointsLabel.setText("Spell Points: " + currentPoints + " (Max slot: " + maxSpellSlot + ")");
        errorLabel.setText("");
    }

    private void modifyBySpellLevel() {
        try {
            int spellLevel = Integer.parseInt(spellLevelInput.getText().trim());
            Integer cost = spellCost.get(spellLevel);

            // Validate spell level exists in cost table
            if (cost == null) {
                errorLabel.setText("Invalid spell level entered.");
                return;
            }

            // Enforce maximum spell slot restriction
            if (spellLevel > maxSpellSlot) {
                errorLabel.setText("You cannot create a spell slot higher than level " + maxSpellSlot + ".");
                return;
            }

            // Restrict one slot per level (6–9)
            if (spellLevel >= 6 && usedHighLevelSlots.contains(spellLevel)) {
                errorLabel.setText("You have already used your level " + spellLevel + " slot. Wait for a long rest.");
                return;
            }

            // Ensure there are enough spell points before deducting
            if (cost > currentPoints) {
                errorLabel.setText("Not enough spell points. Needed: " + cost + ", available: " + currentPoints + ".");
                return;
            }

            // All checks passed — deduct cost
            currentPoints -= cost;
            //if (currentPoints < 0) currentPoints = 0; // defensive, should never hit because of check above

            spellPointsLabel.setText("Spell Points: " + currentPoints + " (Max slot: " + maxSpellSlot + ")");
            errorLabel.setText(""); // clear previous errors

            // Track high-level slot usage
            if (spellLevel >= 6) {
                usedHighLevelSlots.add(spellLevel);
            }

            updateHighLevelSlotsLabel();

        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid number.");
        }
    }

    private void updateHighLevelSlotsLabel() {
        if (usedHighLevelSlots.isEmpty()) {
            highLevelSlotsLabel.setText("Used high-level slots: none");
        } else {
            String used = usedHighLevelSlots.stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            highLevelSlotsLabel.setText("Used high-level slots: " + used);
        }
    }

    private void resetSpellPoints() {
        usedHighLevelSlots.clear();
        updateCasterLevel();
        updateHighLevelSlotsLabel();
    }

    public static void main(String[] args) {
        launch();
    }
}
