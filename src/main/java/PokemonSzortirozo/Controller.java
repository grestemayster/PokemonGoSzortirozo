package PokemonSzortirozo;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    private List<String> selectedItems = new ArrayList<>();
    private boolean isAppraisalComboBoxUpdating = false;

    @FXML
    private CheckComboBox<String> AppraisalcomboBox;

    @FXML
    private ChoiceBox<String> ShinyChoiceBox;

    @FXML
    private ChoiceBox<String> ShadowChoiceBox;

    @FXML
    private ChoiceBox<String> CostumeChoiceBox;

    @FXML
    private ComboBox<String> PokemonComboBox;

    private TextField searchField;
    private ListView<String> listView;
    private List<String> allPokemons;
    private ObservableList<String> filteredPokemons;
    private ObservableList<String> selectedPokemons = FXCollections.observableArrayList();
    private Popup popup;

    @FXML
    public void initialize() {
        initializeChoiceBox();

        try {
            allPokemons = Files.readAllLines(Paths.get("data.csv"));
            initializePokemonComboBox();

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

            selectedPokemons.addListener((ListChangeListener<String>) c -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeChoiceBox() {
        ShinyChoiceBox.getItems().addAll("Shiny, de nem csak shiny", "Csak shiny", "Nem lehet Shiny");
        ShinyChoiceBox.setValue("Shiny, de nem csak shiny");

        ShadowChoiceBox.getItems().addAll("Minden forma", "Csak Sima", "Shadow,Purified", "Csak Shadow", "Csak Purified");
        ShadowChoiceBox.setValue("Minden forma");

        CostumeChoiceBox.getItems().addAll("Minden kinézet", "Csak kinézet nélküli", "Csak kinézetes");
        CostumeChoiceBox.setValue("Minden kinézet");

        AppraisalcomboBox.getItems().addAll("Minden értékelés", "0*", "1*", "2*", "3*", "4*");

        AppraisalcomboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (isAppraisalComboBoxUpdating) return;

            isAppraisalComboBoxUpdating = true;
            try {
                while (c.next()) {
                    if (c.wasAdded()) {
                        if (c.getAddedSubList().contains("Minden értékelés")) {
                            AppraisalcomboBox.getCheckModel().checkAll();
                        } else if (AppraisalcomboBox.getCheckModel().getCheckedItems().size() == AppraisalcomboBox.getItems().size() - 1) {
                            AppraisalcomboBox.getCheckModel().check("Minden értékelés");
                        }
                    } else if (c.wasRemoved()) {
                        if (c.getRemoved().contains("Minden értékelés")) {
                            AppraisalcomboBox.getCheckModel().clearChecks();
                        } else if (AppraisalcomboBox.getCheckModel().isChecked("Minden értékelés")) {
                            AppraisalcomboBox.getCheckModel().clearCheck("Minden értékelés");
                        }
                    }
                }
            } finally {
                isAppraisalComboBoxUpdating = false;
            }
        });
    }

    private void initializePokemonComboBox() {
        searchField = new TextField();
        searchField.setPromptText("Keresés...");

        listView = new ListView<>();
        filteredPokemons = FXCollections.observableArrayList(allPokemons);
        listView.setItems(filteredPokemons);

        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    private final CheckBox checkBox = new CheckBox();

                    {
                        checkBox.setOnAction(event -> {
                            if (checkBox.isSelected()) {
                                selectedPokemons.add(getItem());
                            } else {
                                selectedPokemons.remove(getItem());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            checkBox.setText(item);
                            checkBox.setSelected(selectedPokemons.contains(item));
                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });

        searchField.setOnKeyReleased(this::filterList);

        VBox vbox = new VBox(searchField, listView);
        vbox.setPrefSize(247, 200);

        popup = new Popup();
        popup.getContent().add(vbox);

        PokemonComboBox.setOnMouseClicked(event -> {
            if (!popup.isShowing()) {
                popup.show(PokemonComboBox.getScene().getWindow(),
                        PokemonComboBox.localToScreen(PokemonComboBox.getBoundsInLocal()).getMinX(),
                        PokemonComboBox.localToScreen(PokemonComboBox.getBoundsInLocal()).getMaxY());
            }
        });

        PokemonComboBox.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (!isNowShowing && popup.isShowing()) {
                popup.hide();
            }
        });

        PokemonComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popup.hide();
            }
        });

        PokemonComboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popup.hide();
            }
        });

        PokemonComboBox.setOnAction(event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });

        popup.setOnAutoHide(event -> PokemonComboBox.hide());
    }

    private void filterList(KeyEvent event) {
        String filter = searchField.getText();
        if (filter == null || filter.isEmpty()) {
            filteredPokemons.setAll(allPokemons);
        } else {
            filteredPokemons.setAll(allPokemons.stream()
                    .filter(pokemon -> pokemon.toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }

    private void updateSelectedItems() {
        selectedItems.clear();
        selectedItems.addAll(selectedPokemons);

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
        selectedItems.remove("Minden értékelés");
    }

    @FXML
    private void saveToTxt() {
        if (!selectedItems.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                StringBuilder sb = new StringBuilder();
                boolean firstItem = true;
                for (String item : selectedItems) {
                    if (item.startsWith("&")) {
                        sb.append(item);
                    } else {
                        if (!firstItem) {
                            sb.append(",");
                        }
                        sb.append(item);
                    }
                    firstItem = false;
                }
                writer.write(sb.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
