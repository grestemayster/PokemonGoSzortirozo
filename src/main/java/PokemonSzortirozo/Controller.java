package PokemonSzortirozo;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.CheckComboBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Controller {

    @FXML
    private CheckComboBox<String> comboBox;

    @FXML
    public void initialize() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("data.csv"));
            comboBox.getItems().addAll(lines);

            // Create a FilteredList wrapping the ObservableList
            FilteredList<String> filteredItems = new FilteredList<>(FXCollections.observableArrayList(lines), p -> true);

            // Add a listener to the comboBox editor
            comboBox.getCheckModel().getCheckedItems().addListener((InvalidationListener) (change) -> {
                // Do something when items are checked/unchecked
                System.out.println("Checked items: " + comboBox.getCheckModel().getCheckedItems());

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveToTxt() {
        List<String> selectedItems = comboBox.getCheckModel().getCheckedItems();
        if (!selectedItems.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                StringBuilder sb = new StringBuilder();
                for (String item : selectedItems) {
                    sb.append(item).append("&");
                }
                sb.setLength(sb.length() - 1); // Remove the last comma and space
                writer.write(sb.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
