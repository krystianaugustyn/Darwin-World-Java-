package SerceProjektu.model.util;

import SerceProjektu.model.StartValues;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {

    private static final String FILE_NAME = "darwin_presets.txt";
    private static final String SEPARATOR = ";";

    public void savePresets(Map<String, StartValues> presets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, StartValues> entry : presets.entrySet()) {
                String line = serialize(entry.getKey(), entry.getValue());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, StartValues> loadPresets() {
        Map<String, StartValues> presets = new HashMap<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return presets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parseLine(line, presets);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return presets;
    }

    private String serialize(String name, StartValues v) {
        return name + SEPARATOR +
                v.width() + SEPARATOR +
                v.height() + SEPARATOR +
                v.variant() + SEPARATOR +
                v.startGrassPoints() + SEPARATOR +
                v.grassEnergy() + SEPARATOR +
                v.minGrassEnergy() + SEPARATOR +
                v.maxGrassEnergy() + SEPARATOR +
                v.newGrassPerDay() + SEPARATOR +
                v.startAnimals() + SEPARATOR +
                v.startEnergyAnimals() + SEPARATOR +
                v.energyLostPerDay() + SEPARATOR +
                v.energyForMultiplication() + SEPARATOR +
                v.energyLostForMultiplication() + SEPARATOR +
                v.minMutation() + SEPARATOR +
                v.maxMutation() + SEPARATOR +
                v.genomLength() + SEPARATOR +
                v.minEnergyToKill();
    }

    private void parseLine(String line, Map<String, StartValues> presets) {
        try {
            String[] parts = line.split(SEPARATOR);
            if (parts.length < 18) return;

            String name = parts[0];
            StartValues values = new StartValues(
                    Integer.parseInt(parts[1]),  // width
                    Integer.parseInt(parts[2]),  // height
                    Boolean.parseBoolean(parts[3]), // variant
                    Integer.parseInt(parts[4]),  // startGrassPoints
                    Integer.parseInt(parts[5]),  // grassEnergy
                    Integer.parseInt(parts[6]),  // minGrassEnergy
                    Integer.parseInt(parts[7]),  // maxGrassEnergy
                    Integer.parseInt(parts[8]),  // newGrassPerDay
                    Integer.parseInt(parts[9]),  // startAnimals
                    Integer.parseInt(parts[10]), // startEnergyAnimals
                    Integer.parseInt(parts[11]), // energyLostPerDay
                    Integer.parseInt(parts[12]), // energyForMultiplication
                    Integer.parseInt(parts[13]), // energyLostForMultiplication
                    Integer.parseInt(parts[14]), // minMutation
                    Integer.parseInt(parts[15]), // maxMutation
                    Integer.parseInt(parts[16]), // genomLength
                    Integer.parseInt(parts[17])  // minEnergyToKill
            );
            presets.put(name, values);
        } catch (Exception e) {
            System.out.println("Błąd parsowania linii: " + line);
        }
    }
}