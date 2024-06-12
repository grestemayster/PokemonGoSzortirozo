package PokemonSzortirozo;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.controlsfx.control.CheckComboBox;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    private List<String> selectedItems = new ArrayList<>();

    @FXML
    private CheckComboBox<String> AppraisalcomboBox;
    @FXML
    private CheckComboBox<String> ShinyChoiceBox;
    @FXML
    private CheckComboBox<String> ShadowChoiceBox;
    @FXML
    private CheckComboBox<String> CostumeChoiceBox;

    @FXML
    private ComboBox<String> PokemonComboBox;

    @FXML
    private TextArea selectedItemsTextArea;

    @FXML
    private TextArea addPokemonTextArea;

    @FXML
    private VBox addPokemonVBox;

    private TextField searchField;
    private ListView<String> listView;
    private List<String> allPokemons;
    private ObservableList<String> filteredPokemons;
    private ObservableList<String> selectedPokemons = FXCollections.observableArrayList();
    private Popup popup;

    private CheckComboBoxHandler checkComboBoxHandler;

    @FXML
    public void initialize() {
        checkComboBoxHandler = new CheckComboBoxHandler();
        checkComboBoxHandler.setAppraisalcomboBox(this.AppraisalcomboBox);
        checkComboBoxHandler.setShinyChoiceBox(this.ShinyChoiceBox);
        checkComboBoxHandler.setShadowChoiceBox(this.ShadowChoiceBox);
        checkComboBoxHandler.setCostumeChoiceBox(this.CostumeChoiceBox);
        checkComboBoxHandler.initialize();

        try {
            allPokemons = Files.readAllLines(Paths.get("data.csv"));
            initializePokemonComboBox();

            selectedPokemons.addListener((ListChangeListener<String>) c -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePokemonComboBox() {
        searchField = new TextField();
        searchField.setPromptText("Keresés...");

        listView = new ListView<>();
        filteredPokemons = FXCollections.observableArrayList(allPokemons);
        listView.setItems(filteredPokemons);

        listView.setCellFactory(param -> new ListCell<String>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    if (checkBox.isSelected()) {
                        selectedPokemons.add(getItem());
                    } else {
                        selectedPokemons.remove(getItem());
                    }
                });

                setOnMouseClicked(event -> {
                    if (checkBox.isSelected()) {
                        checkBox.setSelected(false);
                        selectedPokemons.remove(getItem());
                    } else {
                        checkBox.setSelected(true);
                        selectedPokemons.add(getItem());
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
        });

        searchField.setOnKeyReleased(this::filterList);

        VBox vbox = new VBox(searchField, listView);
        vbox.setPrefSize(247, 200);

        popup = new Popup();
        popup.getContent().add(vbox);

        PokemonComboBox.setOnMouseClicked(event -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
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

        List<String> shinyChoices = checkComboBoxHandler.getShinySelectedItems();
        if (shinyChoices.contains("Csak shiny")) {
            selectedItems.add("shiny");
        } else if (shinyChoices.contains("Nem lehet Shiny")) {
            selectedItems.add("!shiny");
        }

        List<String> shadowChoices = checkComboBoxHandler.getShadowSelectedItems();
        if (shadowChoices.contains("Csak Sima")) {
            selectedItems.add("!shadow&!purified");
        } else if (shadowChoices.contains("Shadow,Purified")) {
            selectedItems.add("shadow,purified");
        } else if (shadowChoices.contains("Csak Shadow")) {
            selectedItems.add("shadow");
        } else if (shadowChoices.contains("Csak Purified")) {
            selectedItems.add("purified");
        }

        List<String> costumeChoices = checkComboBoxHandler.getCostumeSelectedItems();
        if (costumeChoices.contains("Csak kinézet nélküli")) {
            selectedItems.add("!costumed");
        } else if (costumeChoices.contains("Csak kinézetes")) {
            selectedItems.add("costumed");
        }

        selectedItems.addAll(checkComboBoxHandler.getAppraisalSelectedItems());
        selectedItems.remove("Minden értékelés");

        selectedItemsTextArea.setText(String.join(", ", selectedItems));
        System.out.println("Selected items updated: " + selectedItems);
    }

    @FXML
    private void showSelectedItems() {
        selectedItemsTextArea.setVisible(!selectedItemsTextArea.isVisible());
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
                        firstItem = false;
                    }
                }
                writer.write(sb.toString());
                writer.newLine();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Mentés");
                alert.setHeaderText(null);
                alert.setContentText("Lementve!");
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addPokemon() {
        String newPokemon = addPokemonTextArea.getText().trim();
        if (!newPokemon.isEmpty()) {
            try {
                Files.write(Paths.get("data.csv"), Collections.singletonList(newPokemon), StandardOpenOption.APPEND);

                allPokemons.add(newPokemon);
                filteredPokemons.add(newPokemon);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hozzáadás");
                alert.setHeaderText(null);
                alert.setContentText("Pokemon sikeresen hozzáadva!");
                alert.showAndWait();

                addPokemonTextArea.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void toggleAddPokemonMenu() {
        addPokemonVBox.setVisible(!addPokemonVBox.isVisible());
    }
}
