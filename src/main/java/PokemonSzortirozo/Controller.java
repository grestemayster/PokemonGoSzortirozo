package PokemonSzortirozo;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.controlsfx.control.CheckComboBox;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private CheckComboBox<String> comboBox;

    @FXML
    private CheckBox shinyCheckBox;

    private List<String> selectedItems = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data.csv"));
            comboBox.getItems().addAll(lines);

            // Create a FilteredList wrapping the ObservableList
            FilteredList<String> filteredItems = new FilteredList<>(FXCollections.observableArrayList(lines), p -> true);

            // Add a listener to the comboBox
            comboBox.getCheckModel().getCheckedItems().addListener((InvalidationListener) (change) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            // Add a listener to the CheckBox
            shinyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSelectedItems() {
        selectedItems.clear();
        selectedItems.addAll(comboBox.getCheckModel().getCheckedItems());
        if (shinyCheckBox.isSelected()) {
            selectedItems.add("shiny");
        } else {
            selectedItems.add("!shiny");
        }
    }

    @FXML
    private void saveToTxt() {
        if (!selectedItems.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                StringBuilder sb = new StringBuilder();
                for (String item : selectedItems) {
                    sb.append(item).append("&");
                }
                sb.setLength(sb.length() - 1); // Remove the last '&'
                writer.write(sb.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
