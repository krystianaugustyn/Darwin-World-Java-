package SerceProjektu.model;

public class Grass {
    private final Vector2d position;
    private final int Energy;

    public Grass(Vector2d position, int Energy) {
        this.position = position;
        this.Energy =  Energy;
    }
    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return Energy;
    }

    @Override
    public String toString() {
        return "*";
    }

}
