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
    private boolean isAppraisalComboBoxUpdating = false;
    private boolean isShinyChoiceBoxUpdating = false;
    private boolean isShadowChoiceBoxUpdating = false;
    private boolean isCostumeChoiceBoxUpdating = false;

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

    private TextField searchField;
    private ListView<String> listView;
    private List<String> allPokemons;
    private ObservableList<String> filteredPokemons;
    private ObservableList<String> selectedPokemons = FXCollections.observableArrayList();
    private Popup popup;

    @FXML
    public void initialize() {
        initializeCheckComboBoxes();

        try {
            allPokemons = Files.readAllLines(Paths.get("data.csv"));
            initializePokemonComboBox();

            ShinyChoiceBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
                if (isShinyChoiceBoxUpdating) return;

                isShinyChoiceBoxUpdating = true;
                try {
                    while (c.next()) {
                        if (c.wasAdded() || c.wasRemoved()) {
                            if (ShinyChoiceBox.getCheckModel().getCheckedItems().size() > 1) {
                                String lastAdded = c.getAddedSubList().get(c.getAddedSubList().size() - 1);
                                ShinyChoiceBox.getCheckModel().clearChecks();
                                ShinyChoiceBox.getCheckModel().check(lastAdded);
                            }
                        }
                        updateSelectedItems();
                    }
                } finally {
                    isShinyChoiceBoxUpdating = false;
                }
            });

            ShadowChoiceBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
                if (isShadowChoiceBoxUpdating) return;

                isShadowChoiceBoxUpdating = true;
                try {
                    while (c.next()) {
                        if (c.wasAdded() || c.wasRemoved()) {
                            if (ShadowChoiceBox.getCheckModel().getCheckedItems().size() > 1) {
                                String lastAdded = c.getAddedSubList().get(c.getAddedSubList().size() - 1);
                                ShadowChoiceBox.getCheckModel().clearChecks();
                                ShadowChoiceBox.getCheckModel().check(lastAdded);
                            }
                        }
                        updateSelectedItems();
                    }
                } finally {
                    isShadowChoiceBoxUpdating = false;
                }
            });

            CostumeChoiceBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
                if (isCostumeChoiceBoxUpdating) return;

                isCostumeChoiceBoxUpdating = true;
                try {
                    while (c.next()) {
                        if (c.wasAdded() || c.wasRemoved()) {
                            if (CostumeChoiceBox.getCheckModel().getCheckedItems().size() > 1) {
                                String lastAdded = c.getAddedSubList().get(c.getAddedSubList().size() - 1);
                                CostumeChoiceBox.getCheckModel().clearChecks();
                                CostumeChoiceBox.getCheckModel().check(lastAdded);
                            }
                        }
                        updateSelectedItems();
                    }
                } finally {
                    isCostumeChoiceBoxUpdating = false;
                }
            });

            selectedPokemons.addListener((ListChangeListener<String>) c -> {
                updateSelectedItems();
                System.out.println("Checked items: " + selectedItems);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeCheckComboBoxes() {
        ShinyChoiceBox.getItems().addAll("Shiny, de nem csak shiny", "Csak shiny", "Nem lehet Shiny");

        ShadowChoiceBox.getItems().addAll("Minden forma", "Csak Sima", "Shadow,Purified", "Csak Shadow", "Csak Purified");

        CostumeChoiceBox.getItems().addAll("Minden kinézet", "Csak kinézet nélküli", "Csak kinézetes");

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
                    updateSelectedItems();
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

        List<String> shinyChoices = ShinyChoiceBox.getCheckModel().getCheckedItems();
        if (shinyChoices.contains("Csak shiny")) {
            selectedItems.add("shiny");
        } else if (shinyChoices.contains("Nem lehet Shiny")) {
            selectedItems.add("!shiny");
        }

        List<String> shadowChoices = ShadowChoiceBox.getCheckModel().getCheckedItems();
        if (shadowChoices.contains("Csak Sima")) {
            selectedItems.add("!shadow&!purified");
        } else if (shadowChoices.contains("Shadow,Purified")) {
            selectedItems.add("shadow,purified");
        } else if (shadowChoices.contains("Csak Shadow")) {
            selectedItems.add("shadow");
        } else if (shadowChoices.contains("Csak Purified")) {
            selectedItems.add("purified");
        }

        List<String> costumeChoices = CostumeChoiceBox.getCheckModel().getCheckedItems();
        if (costumeChoices.contains("Csak kinézet nélküli")) {
            selectedItems.add("!costumed");
        } else if (costumeChoices.contains("Csak kinézetes")) {
            selectedItems.add("costumed");
        }

        selectedItems.addAll(AppraisalcomboBox.getCheckModel().getCheckedItems());
        selectedItems.remove("Minden értékelés");

        // Update the TextArea with the selected items
        selectedItemsTextArea.setText(String.join(", ", selectedItems));
        selectedItemsTextArea.setScrollTop(Double.MAX_VALUE);  // Scroll to the bottom
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
