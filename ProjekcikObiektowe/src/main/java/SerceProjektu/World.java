package SerceProjektu;

import SerceProjektu.model.ConsoleMapDisplay;
import SerceProjektu.model.GrassField;
import SerceProjektu.model.StartValues;

public class World {

    public static void main(String[] args) {
        StartValues values = new StartValues(
                20, 20, false, 5, 10, 2, 20,
                5, 30, 50, 5, 25,
                15, 5, 15, 40, 30
        );

        System.out.println("System Darwin World startuje...");
        System.out.println("Parametry mapy: " + values.width() + "x" + values.height());

        try {

            GrassField map = new GrassField(values);
            ConsoleMapDisplay display = new ConsoleMapDisplay();

            map.addObserver(display);

            map.grassGenerator(values.startGrassPoints());

            Simulation simulation = new Simulation(map, values);

            simulation.run();

        } catch (IllegalArgumentException e) {
            System.out.println("Błąd konfiguracji: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Wystąpił nieoczekiwandssddsdssdy błąd symulacji: " + e.getMessage());
            e.printStackTrace();
        }
    }
}