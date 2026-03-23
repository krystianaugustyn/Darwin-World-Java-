package SerceProjektu.model;

import java.util.List;

public interface WorldMap {

    void place(Animal animal);

    void move(Animal animal, MoveDirection direction);

    List<Animal> getAnimalsAt(Vector2d position);

    void addObserver(MapChangeListener observer);

    void removeObserver(MapChangeListener observer);

    void mapChanged(String message);

    int getId();
}