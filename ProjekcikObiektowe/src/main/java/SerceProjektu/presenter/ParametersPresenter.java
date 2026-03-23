package SerceProjektu.presenter;

import SerceProjektu.model.GrassField;
import SerceProjektu.model.StartValues;
import SerceProjektu.Simulation;
import SerceProjektu.model.util.ConfigurationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParametersPresenter {

    @FXML private TextField mapWidth;
    @FXML private TextField mapHeight;
    @FXML private TextField startEnergy;
    @FXML private TextField grassEnergy;
    @FXML private TextField dailyCost;
    @FXML private TextField animalsCount;
    @FXML private TextField grassPerDay;
    @FXML private TextField startGrass;
    @FXML private TextField energyNeededToMultiplicate;
    @FXML private TextField energyLostToMultiplicate;
    @FXML private TextField minMutation;
    @FXML private TextField maxMutation;
    @FXML private TextField genLength;
    @FXML private CheckBox mapVariant;
    @FXML private TextField minGrassEnergy;
    @FXML private TextField maxGrassEnergy;
    @FXML private TextField minEnergyToKill;

    @FXML private ComboBox<String> presetComboBox;
    @FXML private TextField newPresetName;
    @FXML private Button savePresetButton;

    private final ConfigurationManager configManager = new ConfigurationManager();
    private Map<String, StartValues> presets = new HashMap<>();

    @FXML
    public void initialize() {
        presets = configManager.loadPresets();
        updateComboBox();
    }

    private void updateComboBox() {
        presetComboBox.setItems(FXCollections.observableArrayList(presets.keySet()));
    }

    @FXML
    private void onPresetSelected() {
        String selectedName = presetComboBox.getValue();
        if (selectedName != null && presets.containsKey(selectedName)) {
            fillForm(presets.get(selectedName));
        }
    }

    @FXML
    private void onSavePresetClicked() {
        String name = newPresetName.getText();
        if (name == null || name.isBlank()) {
            System.out.println("Nazwa presetu nie może być pusta!");
            return;
        }

        try {
            StartValues currentValues = getValuesFromForm();
            presets.put(name, currentValues);
            configManager.savePresets(presets);
            updateComboBox();
            presetComboBox.setValue(name);
            newPresetName.clear();
        } catch (NumberFormatException e) {
            System.out.println("Błąd formatu danych: " + e.getMessage());
        }
    }

    private void fillForm(StartValues v) {
        mapWidth.setText(String.valueOf(v.width()));
        mapHeight.setText(String.valueOf(v.height()));
        mapVariant.setSelected(v.variant());
        startGrass.setText(String.valueOf(v.startGrassPoints()));
        grassEnergy.setText(String.valueOf(v.grassEnergy()));
        minGrassEnergy.setText(String.valueOf(v.minGrassEnergy()));
        maxGrassEnergy.setText(String.valueOf(v.maxGrassEnergy()));
        grassPerDay.setText(String.valueOf(v.newGrassPerDay()));
        animalsCount.setText(String.valueOf(v.startAnimals()));
        startEnergy.setText(String.valueOf(v.startEnergyAnimals()));
        dailyCost.setText(String.valueOf(v.energyLostPerDay()));
        energyNeededToMultiplicate.setText(String.valueOf(v.energyForMultiplication()));
        energyLostToMultiplicate.setText(String.valueOf(v.energyLostForMultiplication()));
        minMutation.setText(String.valueOf(v.minMutation()));
        maxMutation.setText(String.valueOf(v.maxMutation()));
        genLength.setText(String.valueOf(v.genomLength()));
        minEnergyToKill.setText(String.valueOf(v.minEnergyToKill()));
    }

    private StartValues getValuesFromForm() {
        return new StartValues(
                Integer.parseInt(mapWidth.getText()),
                Integer.parseInt(mapHeight.getText()),
                mapVariant.isSelected(),
                Integer.parseInt(startGrass.getText()),
                Integer.parseInt(grassEnergy.getText()),
                Integer.parseInt(minGrassEnergy.getText()),
                Integer.parseInt(maxGrassEnergy.getText()),
                Integer.parseInt(grassPerDay.getText()),
                Integer.parseInt(animalsCount.getText()),
                Integer.parseInt(startEnergy.getText()),
                Integer.parseInt(dailyCost.getText()),
                Integer.parseInt(energyNeededToMultiplicate.getText()),
                Integer.parseInt(energyLostToMultiplicate.getText()),
                Integer.parseInt(minMutation.getText()),
                Integer.parseInt(maxMutation.getText()),
                Integer.parseInt(genLength.getText()),
                Integer.parseInt(minEnergyToKill.getText())
        );
    }

    @FXML
    private void onSimulationStartClicked() throws IOException {
        StartValues values = getValuesFromForm();

        GrassField map = new GrassField(values);

        SerceProjektu.model.util.CsvStatisticsLogger statsLogger = new SerceProjektu.model.util.CsvStatisticsLogger();
        map.addObserver(statsLogger);

        Simulation simulation = new Simulation(map, values);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();

        SimulationPresenter presenter = loader.getController();
        presenter.setMap(map);
        presenter.setSimulation(simulation);
        map.addObserver(presenter);

        Stage stage = new Stage();
        stage.setTitle("Darwin World Simulation");
        stage.setScene(new Scene(viewRoot));
        stage.show();

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();
    }

    public boolean isCheckBox() {
        return mapVariant.isSelected();
    }


}