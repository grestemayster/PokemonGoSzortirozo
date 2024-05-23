package PokemonSzortirozo;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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

    private List<String> selectedItems = new ArrayList<>();

    @FXML
    private CheckComboBox<String> comboBox;

    @FXML
    private ChoiceBox<String> ShinyChoiceBox;

    @FXML
    private CheckBox FourStarCheckBox;

    @FXML
    private CheckBox ShadowCheckBox;


    @FXML
    private CheckBox EventCheckBox;


    @FXML
    public void initialize() {
        // Töltsük fel a ChoiceBox-ot a lehetőségekkel
        ShinyChoiceBox.getItems().addAll("Shiny, de nem csak shiny", "Csak shiny", "Nem lehet Shiny");
        // Alapértelmezett választás
        ShinyChoiceBox.setValue("Shiny, de nem csak shiny");
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
            ShinyChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            FourStarCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            ShadowCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            EventCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
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

        String choice = ShinyChoiceBox.getSelectionModel().getSelectedItem();
        if (choice != null) {
            switch (choice) {
                case "Csak shiny":
                    selectedItems.add("shiny");
                    break;
                case "Nem lehet Shiny":
                    selectedItems.add("!shiny");
                    break;
                default:
                    // "Alap" case, do nothing
                    break;
            }
        }


        if (FourStarCheckBox.isSelected()) {
            selectedItems.add("4*");
        }
        if (ShadowCheckBox.isSelected()) {
            selectedItems.add("Shadow");
        } else {
            selectedItems.add("!Shadow");
        }
        if (EventCheckBox.isSelected()) {
            selectedItems.add("Costumed");
        } else {
            selectedItems.add("!Costumed");
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
