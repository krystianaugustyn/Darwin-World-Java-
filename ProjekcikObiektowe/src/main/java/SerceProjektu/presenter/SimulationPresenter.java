package SerceProjektu.presenter;

import SerceProjektu.Simulation;
import SerceProjektu.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.List;

public class SimulationPresenter implements MapChangeListener {

    private WorldMap map;

    private static final double OFFSET = 30.0;

    private List<Integer> mostPopularGenotype = null;

    private Simulation simulation;
    private boolean isPaused = false;

    @FXML private Button pauseButton;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @FXML
    private void onPauseClicked() {
        if (simulation == null) return;

        if (isPaused) {
            simulation.resumeSimulation();
            pauseButton.setText("Pauza");
            isPaused = false;
        } else {
            simulation.pauseSimulation();
            pauseButton.setText("Wznów");
            isPaused = true;
        }
    }

    @FXML
    private Canvas mapCanvas;

    private Image predatorImage;
    private Image victimImage;
    private Image grassImage;
    private Image groupImage;

    @FXML
    public void initialize() {
        predatorImage = loadImage("/images/lew.png");
        victimImage = loadImage("/images/koala.png");
        grassImage = loadImage("/images/trawa.png");
        groupImage = loadImage("/images/grupa.png");

        mapCanvas.setOnMouseClicked(event -> {
            if (!isPaused) return;

            double x = event.getX();
            double y = event.getY();

            if (map instanceof AbstractWorldMap abstractMap) {
                double cellWidth = (mapCanvas.getWidth() - 30.0) / abstractMap.getWidth();
                double cellHeight = (mapCanvas.getHeight() - 30.0) / abstractMap.getHeight();

                int mapX = (int) ((x - 30.0) / cellWidth);
                int mapY = abstractMap.getHeight() - 1 - (int)((y - 30.0) / cellHeight);

                Vector2d clickedPosition = new Vector2d(mapX, mapY);

                List<Animal> animals = map.getAnimalsAt(clickedPosition);
                if (animals != null && !animals.isEmpty()) {
                    trackedAnimal = animals.get(0);
                    updateTrackedAnimalStats();
                } else {
                    trackedAnimal = null;
                    clearTrackedAnimalStats();
                }
            }
        });
    }

    private void updateTrackedAnimalStats() {
        if (trackedAnimal == null) {
            clearTrackedAnimalStats();
            return;
        }

        if (trackedAnimal.getEnergy() > 0) {
            trackedStatus.setText("Żyje");
            trackedStatus.setTextFill(Color.GREEN);
        } else {
            trackedStatus.setText("Martwy");
            trackedStatus.setTextFill(Color.RED);
        }

        trackedGenom.setText(trackedAnimal.getGenom().toString());

        int activeIdx = trackedAnimal.getActiveGeneIndex();
        trackedActiveGene.setText("Indeks: " + activeIdx + " (Wartość: " + trackedAnimal.getGenom().get(activeIdx) + ")");

        trackedEnergy.setText(String.valueOf(trackedAnimal.getEnergy()));
        trackedPlants.setText(String.valueOf(trackedAnimal.getPlantsEaten()));
        trackedChildren.setText(String.valueOf(trackedAnimal.getChildren().size()));

        trackedDescendants.setText(String.valueOf(trackedAnimal.getDescendantsCount()));

        if (trackedAnimal.getDeathDay() != null) {
            trackedAge.setText("Zmarł w dniu: " + trackedAnimal.getDeathDay() + " (Żył dni: " + trackedAnimal.getAge() + ")");
        } else {
            trackedAge.setText("Żyje dni: " + trackedAnimal.getAge());
        }
    }

    private void clearTrackedAnimalStats() {
        trackedStatus.setText("-");
        trackedStatus.setTextFill(Color.BLACK);
        trackedGenom.setText("-");
        trackedActiveGene.setText("-");
        trackedEnergy.setText("-");
        trackedPlants.setText("-");
        trackedChildren.setText("-");
        trackedDescendants.setText("-");
        trackedAge.setText("-");
    }


    private Image loadImage(String path) {
        try {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                System.out.println("Błąd: Nie znaleziono obrazka: " + path);
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private Label infoLabel;

    @FXML private Label statAnimalCount;
    @FXML private Label statGrassCount;
    @FXML private Label statFreeFields;
    @FXML private Label statGenotype;
    @FXML private Label statAvgEnergy;
    @FXML private Label statAvgLifespan;
    @FXML private Label statAvgChildren;

    public void setMap(WorldMap map) {
        this.map = map;
    }

    @Override
    public void mapChanged(WorldMap worldmap, String message) {
        Platform.runLater(() -> {
            drawMap();
            infoLabel.setText(message);
            updateStatistics();
            updateTrackedAnimalStats();
        });
    }

    private void updateStatistics() {
        if (map instanceof GrassField grassField) {
            Statistics stats = grassField.getStatistics();

            statAnimalCount.setText(String.valueOf(stats.animalCount()));
            statGrassCount.setText(String.valueOf(stats.grassCount()));
            statFreeFields.setText(String.valueOf(stats.freeFieldsCount()));
            statGenotype.setText(stats.mostPopularGenotype().toString());
            statAvgEnergy.setText(String.format("%.2f", stats.avgEnergy()));
            statAvgLifespan.setText(String.format("%.2f", stats.avgLifespan()));
            statAvgChildren.setText(String.format("%.2f", stats.avgChildren()));

            this.mostPopularGenotype = stats.mostPopularGenotype();
        }
    }

    private void drawMap() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        if (map instanceof AbstractWorldMap abstractMap) {

            double drawWidth = mapCanvas.getWidth() - OFFSET;
            double drawHeight = mapCanvas.getHeight() - OFFSET;

            double cellWidth = drawWidth / abstractMap.getWidth();
            double cellHeight = drawHeight / abstractMap.getHeight();

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.setFont(new Font(10));

            gc.setFill(Color.BLACK);

            for (int i = 0; i < abstractMap.getWidth(); i++) {
                gc.fillText("" + i, OFFSET + (i * cellWidth) + (cellWidth / 3), OFFSET - 12);
            }

            for (int i = 0; i < abstractMap.getHeight(); i++) {
                double yPos = OFFSET + ((abstractMap.getHeight() - 1 - i) * cellHeight) + (cellHeight / 1.5);
                gc.fillText("" + i, 5, yPos);
            }

            for (int x = 0; x < abstractMap.getWidth(); x++) {
                for (int y = 0; y < abstractMap.getHeight(); y++) {
                    Vector2d position = new Vector2d(x, y);

                    double drawX = OFFSET + x * cellWidth;
                    double drawY = OFFSET + (abstractMap.getHeight() - 1 - y) * cellHeight;

                    if (map instanceof GrassField grassField && grassField.isPreferredField(position)) {
                        gc.setFill(Color.DARKSEAGREEN);
                    } else {
                        gc.setFill(Color.LIGHTGOLDENRODYELLOW);
                    }

                    gc.fillRect(drawX, drawY, cellWidth, cellHeight);

                    if (map instanceof GrassField grassField && grassField.isGrassAt(position)) {
                        if (grassImage != null) {

                            gc.drawImage(grassImage, drawX, drawY, cellWidth, cellHeight);
                        } else {

                            gc.setFill(Color.GREEN);
                            gc.fillRect(drawX, drawY, cellWidth, cellHeight);
                        }
                    }

                    List<Animal> animals = map.getAnimalsAt(position);

                    if (animals != null && !animals.isEmpty()) {
                        Image imgToDraw = null;
                        Animal topAnimal = animals.get(0);

                        int startEnergy = simulation.getStartValues().startEnergyAnimals();

                        double energyRatio = (double) topAnimal.getEnergy() / (2 * startEnergy);

                        double barHeight = 5.0;
                        double barWidth = cellWidth;
                        double currentBarWidth = Math.min(barWidth, barWidth * energyRatio);

                        gc.setFill(Color.GREY);
                        gc.fillRect(drawX, drawY + cellHeight - barHeight, barWidth, barHeight);

                        gc.setFill(getEnergyColor(energyRatio));
                        gc.fillRect(drawX, drawY + cellHeight - barHeight, currentBarWidth, barHeight);

                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(0.5);
                        gc.strokeRect(drawX, drawY + cellHeight - barHeight, barWidth, barHeight);

                        if (animals.size() > 1) {
                            imgToDraw = groupImage;
                        } else {

                            if (topAnimal.getAnimalType() == AnimalType.Predator) {
                                imgToDraw = predatorImage;
                            } else {
                                imgToDraw = victimImage;
                            }
                        }

                        if (mostPopularGenotype != null && topAnimal.getGenom().equals(mostPopularGenotype)) {
                            gc.setStroke(Color.BLUE);
                            gc.setLineWidth(3);

                            gc.strokeRect(drawX, drawY, cellWidth, cellHeight);
                        }


                        if (imgToDraw != null) {
                            gc.drawImage(imgToDraw, drawX, drawY, cellWidth, cellHeight);
                        } else {

                            if (animals.size() > 1) {
                                gc.setFill(Color.PURPLE);
                            } else if (topAnimal.getAnimalType() == AnimalType.Predator) {
                                gc.setFill(Color.CRIMSON);
                            } else {
                                gc.setFill(Color.BLUE);
                            }
                            gc.fillOval(drawX, drawY, cellWidth, cellHeight);
                        }

                        if (trackedAnimal != null && animals.contains(trackedAnimal)) {
                            gc.setStroke(Color.RED);
                            gc.setLineWidth(3);
                            gc.strokeRect(drawX, drawY, cellWidth, cellHeight);
                        }
                    }

                }
            }
        }
    }

    @FXML private Label trackedStatus;
    @FXML private Label trackedGenom;
    @FXML private Label trackedActiveGene;
    @FXML private Label trackedEnergy;
    @FXML private Label trackedPlants;
    @FXML private Label trackedChildren;
    @FXML private Label trackedDescendants;
    @FXML private Label trackedAge;

    private Animal trackedAnimal;

    private Color getEnergyColor(double percentage) {
        double hue = Math.min(1.0, Math.max(0.0, percentage)) * 120.0;
        return Color.hsb(hue, 1.0, 1.0);
    }
}