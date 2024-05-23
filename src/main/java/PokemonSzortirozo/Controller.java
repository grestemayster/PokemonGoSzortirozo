package PokemonSzortirozo;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    private boolean isAppraisalComboBoxUpdating = false; // Logikai zár

    @FXML
    private CheckComboBox<String> comboBox;

    @FXML
    private CheckComboBox<String> AppraisalcomboBox;

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

            // FilteredList létrehozása az ObservableList becsomagolásával
            FilteredList<String> filteredItems = new FilteredList<>(FXCollections.observableArrayList(lines), p -> true);

            // Listener hozzáadása a comboBox-hoz
            comboBox.getCheckModel().getCheckedItems().addListener((InvalidationListener) change -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

            // Listenerek hozzáadása a ChoiceBox-okhoz
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
        ShinyChoiceBox.setValue("Shiny, de nem csak shiny");

        ShadowChoiceBox.getItems().addAll("Minden forma", "Csak Sima", "Shadow,Purified", "Csak Shadow", "Csak Purified");
        ShadowChoiceBox.setValue("Minden forma");

        CostumeChoiceBox.getItems().addAll("Minden kinézet", "Csak kinézet nélküli", "Csak kinézetes");
        CostumeChoiceBox.setValue("Minden kinézet");

        AppraisalcomboBox.getItems().addAll("Minden értékelés", "0*", "1*", "2*", "3*", "4*");

        AppraisalcomboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (isAppraisalComboBoxUpdating) return; // Ha éppen frissítjük, akkor kilépünk a metódusból

            isAppraisalComboBoxUpdating = true; // Beállítjuk a logikai zárat
            try {
                while (c.next()) {
                    if (c.wasAdded() && c.getAddedSubList().contains("Minden értékelés")) {
                        // "Minden értékelés" kiválasztása esetén minden más is kiválasztásra kerül
                        AppraisalcomboBox.getCheckModel().checkAll();
                    } else if (c.wasRemoved() && c.getRemoved().contains("Minden értékelés")) {
                        // "Minden értékelés" kiválasztásának megszüntetése esetén minden más is törlésre kerül
                        AppraisalcomboBox.getCheckModel().clearChecks();
                    } else if (!AppraisalcomboBox.getCheckModel().isChecked(0) && AppraisalcomboBox.getCheckModel().getCheckedItems().size() == AppraisalcomboBox.getItems().size() - 1) {
                        // Ha minden elem kiválasztásra került, "Minden értékelés" is kiválasztásra kerül
                        AppraisalcomboBox.getCheckModel().check("Minden értékelés");
                    }
                }
            } finally {
                isAppraisalComboBoxUpdating = false; // Visszaállítjuk a logikai zárat
            }
        });
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
                    // Alapértelmezett eset, nem csinál semmit
                    break;
            }
        }

        String Shadowchoice = ShadowChoiceBox.getSelectionModel().getSelectedItem();
        if (Shadowchoice != null) {
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
        if (Costumechoice != null) {
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

        selectedItems.addAll(AppraisalcomboBox.getCheckModel().getCheckedItems());
        selectedItems.remove("Minden értékelés"); // "Minden értékelés" opció eltávolítása a listából, ha kiválasztották
    }

    @FXML
    private void saveToTxt() {
        if (!selectedItems.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                StringBuilder sb = new StringBuilder();
                for (String item : selectedItems) {
                    sb.append(item).append(",");
                }
                sb.setLength(sb.length() - 1); // Utolsó ',' eltávolítása
                writer.write(sb.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
