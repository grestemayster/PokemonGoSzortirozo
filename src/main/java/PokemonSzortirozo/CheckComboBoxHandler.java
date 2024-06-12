package PokemonSzortirozo;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

public class CheckComboBoxHandler {

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

    public void initialize() {
        initializeAppraisalComboBox();
        initializeShinyChoiceBox();
        initializeShadowChoiceBox();
        initializeCostumeChoiceBox();
    }

    public void setAppraisalcomboBox(CheckComboBox<String> appraisalcomboBox) {
        this.AppraisalcomboBox = appraisalcomboBox;
    }

    public void setShinyChoiceBox(CheckComboBox<String> shinyChoiceBox) {
        this.ShinyChoiceBox = shinyChoiceBox;
    }

    public void setShadowChoiceBox(CheckComboBox<String> shadowChoiceBox) {
        this.ShadowChoiceBox = shadowChoiceBox;
    }

    public void setCostumeChoiceBox(CheckComboBox<String> costumeChoiceBox) {
        this.CostumeChoiceBox = costumeChoiceBox;
    }

    private void initializeAppraisalComboBox() {
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
            System.out.println("Appraisal checked items: " + getAppraisalSelectedItems());
        });
    }

    private void initializeShinyChoiceBox() {
        ShinyChoiceBox.getItems().addAll("Shiny, de nem csak shiny", "Csak shiny", "Nem lehet Shiny");

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
                }
            } finally {
                isShinyChoiceBoxUpdating = false;
            }
            System.out.println("Shiny checked items: " + getShinySelectedItems());
        });
    }

    private void initializeShadowChoiceBox() {
        ShadowChoiceBox.getItems().addAll("Minden forma", "Csak Sima", "Shadow,Purified", "Csak Shadow", "Csak Purified");

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
                }
            } finally {
                isShadowChoiceBoxUpdating = false;
            }
            System.out.println("Shadow checked items: " + getShadowSelectedItems());
        });
    }

    private void initializeCostumeChoiceBox() {
        CostumeChoiceBox.getItems().addAll("Minden kinézet", "Csak kinézet nélküli", "Csak kinézetes");

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
                }
            } finally {
                isCostumeChoiceBoxUpdating = false;
            }
            System.out.println("Costume checked items: " + getCostumeSelectedItems());
        });
    }

    public List<String> getAppraisalSelectedItems() {
        return AppraisalcomboBox.getCheckModel().getCheckedItems();
    }

    public List<String> getShinySelectedItems() {
        return ShinyChoiceBox.getCheckModel().getCheckedItems();
    }

    public List<String> getShadowSelectedItems() {
        return ShadowChoiceBox.getCheckModel().getCheckedItems();
    }

    public List<String> getCostumeSelectedItems() {
        return CostumeChoiceBox.getCheckModel().getCheckedItems();
    }
}
