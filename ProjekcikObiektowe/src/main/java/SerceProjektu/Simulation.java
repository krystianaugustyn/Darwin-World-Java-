package SerceProjektu;

import SerceProjektu.model.*;
import SerceProjektu.model.util.GenomGenerator;
import SerceProjektu.model.util.PositionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Simulation implements Runnable {
    public final List<Animal> animals =  new ArrayList<>();
    private final GrassField map;
    private final Random random;
    private final StartValues values;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public Simulation(GrassField map, StartValues values) {
        this.map = map;
        this.values = values;
        this.random = new Random();

        for(int i = 0; i < values.startAnimals(); i++) {

            PositionGenerator position = new PositionGenerator(values.width(), values.height());
            GenomGenerator genom = new GenomGenerator(values.genomLength(), values.minMutation(), values.maxMutation());

            AnimalType type = null;

            if (!values.variant()) { type = AnimalType.Victim; }
            else {
                int x = random.nextInt(3);

                if (x == 0) {
                    type = AnimalType.Predator;
                } else {
                    type = AnimalType.Victim;
                }
            }

            Animal animal = new Animal(position.positionGenerator(),genom.generateGenom(),position.moveDirectionGenerator(),type, this.values);

            map.place(animal);
            animals.add(animal);
        }
    }

    public void pauseSimulation() {
        paused = true;
    }

    public void resumeSimulation() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify();
        }
    }

    public StartValues getStartValues() {
        return values;
    }

    @Override
    public void run() {
        int day = 0;
        int grassGrownPerDay = map.getGrassGrownPerDay();

        try {
            while(true) {
                synchronized (pauseLock) {
                    while (paused) {
                        pauseLock.wait();
                    }
                }

                int dayy = day;
                animals.removeIf(animal -> {
                    if (animal.getEnergy() <= 0) {
                        map.trackDeath(animal, dayy);
                        map.remover(animal);
                        map.mapChanged("Zdechlo");
                        return true;
                    }
                    return false;
                });

                for(int i = 0; i < animals.size(); i++) {
                    Animal animal = animals.get(i);

                    int geneIndex = day % values.genomLength();
                    int move = animal.getGenom().get(geneIndex);
                    animal.setActiveGeneIndex(geneIndex);

                    MoveDirection nextDirection = animal.getDirection().next(move);
                    animal.setDirection(nextDirection);
                    map.move(animal, nextDirection);

                }

                Map<Vector2d, List<Animal>> animalsToKill = map.animalsKills();
                for (Vector2d position : animalsToKill.keySet()) {
                    List<Animal> animalsOnPosition = animalsToKill.get(position);
                    for (Animal animal : animalsOnPosition) {
                        animals.remove(animal);
                        map.remover(animal);
                    }
                }

                map.grassConsume();

                Map<Vector2d, List<Animal>> babies = map.animalMultiplication();
                for (Vector2d position : babies.keySet()) {
                    List<Animal> babiesOnPosition = babies.get(position);
                    for (Animal baby : babiesOnPosition) {
                        map.place(baby);
                        animals.add(baby);
                    }
                }

                for (int i = 0; i < animals.size(); i++) {
                    Animal animal = animals.get(i);
                    animal.setEnergy(animal.getEnergy() - animal.getEnergyLostPerDay());
                    if(animal.getEnergy() > 0) {
                        animal.setAge(animal.getAge() + 1);
                    }

                }

                map.grassGenerator(grassGrownPerDay);

                map.mapChanged("Dzień symulacji: " + day);

                sleep(300);
                day++;
        }}
        catch (InterruptedException e) {
            System.out.println("Symulacja zatrzymana.");
    }}
}
