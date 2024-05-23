package PokemonSzortirozo;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.controlsfx.control.CheckComboBox;
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
    private ChoiceBox<String> ShadowChoiceBox;

    @FXML
    private ChoiceBox<String> CostumeChoiceBox;


    @FXML
    public void initialize() {

        initializeChoiceBox();

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

            ShadowChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            CostumeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeChoiceBox() {
        // Töltsük fel a ChoiceBox-ot a lehetőségekkel
        ShinyChoiceBox.getItems().addAll("Shiny, de nem csak shiny", "Csak shiny", "Nem lehet Shiny");
        // Alapértelmezett választás
        ShinyChoiceBox.setValue("Shiny, de nem csak shiny");


        ShadowChoiceBox.getItems().addAll("Minden forma","Csak Sima","Shadow,Purified","Csak Shadow","Csak Purified");
        ShadowChoiceBox.setValue("Minden forma");

        CostumeChoiceBox.getItems().addAll("Minden kinézet","Csak kinézet nélküli","Csak kinézetes");
        CostumeChoiceBox.setValue("Minden kinézet");



    }

    private void updateSelectedItems() {
        selectedItems.clear();
        selectedItems.addAll(comboBox.getCheckModel().getCheckedItems());

        String Shinychoice = ShinyChoiceBox.getSelectionModel().getSelectedItem();
        if (Shinychoice != null) {
            switch (Shinychoice) {
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

        String Shadowchoice = ShadowChoiceBox.getSelectionModel().getSelectedItem();
        if(Shadowchoice != null) {
            switch (Shadowchoice) {
                case "Csak Sima":
                    selectedItems.add("!shadow&!purified");
                    break;
                case "Shadow,Purified":
                    selectedItems.add("shadow,purified");
                    break;
                case "Csak Shadow":
                    selectedItems.add("shadow");
                    break;
                case "Csak Purified":
                    selectedItems.add("purified");
                    break;
                case "Minden forma":
                    break;

            }
        }

        String Costumechoice = CostumeChoiceBox.getSelectionModel().getSelectedItem();
        if(Costumechoice != null) {
            switch (Costumechoice) {
                case "Csak kinézet nélküli":
                    selectedItems.add("!costumed");
                    break;
                case "Csak kinézetes":
                    selectedItems.add("costumed");
                    break;
                case "Minden kinézet":
                    break;

            }
        }





    }

    @FXML
    private void saveToTxt() {
        if (!selectedItems.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                StringBuilder sb = new StringBuilder();
                for (String item : selectedItems) {
                    sb.append(item).append(",");
                }
                sb.setLength(sb.length() - 1); // Remove the last ','
                writer.write(sb.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
