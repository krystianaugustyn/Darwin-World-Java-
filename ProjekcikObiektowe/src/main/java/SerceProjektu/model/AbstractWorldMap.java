package SerceProjektu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWorldMap implements WorldMap {

    protected final int width;
    protected final int height;
    protected final StartValues values;
    protected Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    private List<MapChangeListener> observers = new CopyOnWriteArrayList<>();
    private static int nextId = 0;
    private final int id = nextId++;


    public AbstractWorldMap(StartValues values) {
        this.values = values;
        this.width = values.width();
        this.height = values.height();
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void place(Animal animal) {
        Vector2d position = animal.getPosition();
        animals.computeIfAbsent(position, k -> new CopyOnWriteArrayList<>()).add(animal);
        mapChanged("Zwierzę zostało umieszczone na pozycji " + position);
    }

    public List<Animal> getAnimalsAt(Vector2d position) {
        return animals.get(position);
    }
    
    public boolean checkMapX(Vector2d position) {
        int x =  position.getX();
        
        if (x >= width || x < 0) {
            return false;
        }
        return true;
    }

    public boolean checkMapY(Vector2d position) {
        int y =  position.getY();

        if (y >= height || y < 0) {
            return false;
        }
        return true;
    }

    public void move(Animal animal,MoveDirection direction) {
        
        Vector2d oldPosition = animal.getPosition();
        Vector2d newPosition = oldPosition.add(direction.toUnitVector());
        
        if (!checkMapX(newPosition)) {
            int y =  newPosition.getY();
            if (newPosition.getX() < 0) {
                newPosition = new Vector2d(width - 1,y);
            }
            else {
                newPosition = new Vector2d(0,y);
            }
        }


        if (!checkMapY(newPosition)) {
            int x =  newPosition.getX();
            if (newPosition.getY() < 0 || newPosition.getY() >= height) {
                animal.setDirection(animal.getDirection().opposite());
                if(newPosition.getY() < 0) {
                    newPosition = new Vector2d(x , 0);
                }
                else if(newPosition.getY() >= height) {
                    newPosition = new Vector2d(x , height-1);
                }
            }
        }

       List<Animal> list = animals.get(oldPosition);

       if(list != null) {
           list.remove(animal);
       }

       animal.setPosition(newPosition);
       animals.computeIfAbsent(newPosition, k -> new CopyOnWriteArrayList<>()).add(animal);
        mapChanged("Zwierzę zmieniło pozycję na " + animal.getPosition());

    }

    public void remover(Animal animal) {
        List<Animal> list = animals.get(animal.getPosition());

        if(list != null) {
            list.remove(animal);
        }
        mapChanged("Zwierzę zostało usunięte z mapy");
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    public void mapChanged(String message) {
        for (MapChangeListener observer: observers) {
            observer.mapChanged(this, message);
        }
    }

    @Override
    public int getId() { return id; }

}
