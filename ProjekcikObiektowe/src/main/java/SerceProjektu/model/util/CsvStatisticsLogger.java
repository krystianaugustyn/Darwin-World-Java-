package SerceProjektu.model.util;

import SerceProjektu.model.GrassField;
import SerceProjektu.model.MapChangeListener;
import SerceProjektu.model.Statistics;
import SerceProjektu.model.WorldMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvStatisticsLogger implements MapChangeListener {

    private final String fileName;
    private static final String SEPARATOR = ";";

    public CsvStatisticsLogger() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        this.fileName = "stats_" + timeStamp + ".csv";
        createFileWithHeader();
    }

    private void createFileWithHeader() {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Dzien" + SEPARATOR +
                    "Zwierzeta" + SEPARATOR +
                    "Rosliny" + SEPARATOR +
                    "Wolne_Pola" + SEPARATOR +
                    "Srednia_Energia" + SEPARATOR +
                    "Srednia_Dlugosc_Zycia" + SEPARATOR +
                    "Srednia_Liczba_Dzieci");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Nie udalo sie utworzyc pliku statystyk: " + e.getMessage());
        }
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        if (message.startsWith("Dzień symulacji")) {
            if (worldMap instanceof GrassField grassField) {
                writeStats(grassField.getStatistics(), message);
            }
        }
    }

    private void writeStats(Statistics stats, String message) {
        String dayNumber = message.replaceAll("\\D+", "");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            String line = String.format("%s%s%d%s%d%s%d%s%.2f%s%.2f%s%.2f",
                    dayNumber, SEPARATOR,
                    stats.animalCount(), SEPARATOR,
                    stats.grassCount(), SEPARATOR,
                    stats.freeFieldsCount(), SEPARATOR,
                    stats.avgEnergy(), SEPARATOR,
                    stats.avgLifespan(), SEPARATOR,
                    stats.avgChildren()
            );

            line = line.replace(".", ",");

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Blad zapisu statystyk: " + e.getMessage());
        }
    }
}