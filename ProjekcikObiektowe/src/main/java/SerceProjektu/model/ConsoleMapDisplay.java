package SerceProjektu.model;

public class ConsoleMapDisplay implements MapChangeListener {

    private int counter = 0;

    public void mapChanged(WorldMap worldmap, String message) {
        synchronized(this) {
            counter++;
            System.out.print("MAP UPDATE: " + counter);
            System.out.println();
            System.out.println("MAP ID: " + worldmap.getId());
            System.out.println();
            System.out.println(message);
            System.out.println(worldmap.toString());
        }
    }
}
