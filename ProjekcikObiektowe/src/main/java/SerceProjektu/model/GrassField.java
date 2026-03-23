package SerceProjektu.model;

import SerceProjektu.model.util.GenomGenerator;
import SerceProjektu.model.util.GrassPointsGenerator;
import SerceProjektu.model.util.PositionGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import java.util.*;

public class GrassField extends AbstractWorldMap {
    private final int GrassPoints;
    private final int maxHeight;
    private final int width;
    private Map<Vector2d, Grass> grasses = new ConcurrentHashMap<>();
    private Random rand = new Random();
    private final int minGrassEnergy;
    private final int maxGrassEnergy;
    private final int grassGrownPerDay;
    private int deadAnimalsCount = 0;
    private int deadAnimalsLifeSpanSum = 0;
    private int startEnergy;

    private final StartValues values;

    public GrassField(StartValues values) {
        super(values);
        this.values = values;
        this.GrassPoints = values.startGrassPoints();
        this.maxHeight = values.height();
        this.width = values.width();
        this.minGrassEnergy = values.minGrassEnergy();
        this.maxGrassEnergy = values.maxGrassEnergy();
        this.grassGrownPerDay = values.newGrassPerDay();
        this.startEnergy = values.startEnergyAnimals();

    }

    public int howManyInJungle(int grassPoints){
        int x = 0;
        int junglePoints = 0;
        for (int i=0; i<grassPoints; i++){
            x = rand.nextInt(10);
            if (x>=2){
                junglePoints++;
            }
        }
        return junglePoints;
    }

    public void grassGenerator(int count) {
        int jungleMinHeight = (int) Math.floor(maxHeight * 0.4);
        int jungleMaxHeight = (int) Math.ceil(maxHeight * 0.6);
        int jungleGrass = howManyInJungle(count);

        GrassPointsGenerator generator = new GrassPointsGenerator(width, jungleMinHeight, jungleMaxHeight, jungleGrass);

        for (Vector2d grassPosition : generator) {
            if (values.variant()) {
                grasses.put(grassPosition, new Grass(grassPosition, rand.nextInt(minGrassEnergy, maxGrassEnergy + 1)));
            } else {
                grasses.put(grassPosition, new Grass(grassPosition, values.grassEnergy()));
            }
        }

        int noJunglePoints = count - jungleGrass;
        int low = rand.nextInt(101);
        int lowPoints = (int) Math.floor((low * 0.01) * noJunglePoints);
        int topPoints  = count - jungleGrass - lowPoints;

        GrassPointsGenerator generatorLow = new GrassPointsGenerator(width, 0, jungleMinHeight, lowPoints);
        for (Vector2d grassPosition : generatorLow) {
            if (values.variant()) {
                grasses.put(grassPosition, new Grass(grassPosition, rand.nextInt(minGrassEnergy, maxGrassEnergy + 1)));
            } else {
                grasses.put(grassPosition, new Grass(grassPosition, values.grassEnergy()));
            }
        }

        GrassPointsGenerator generatorTop = new GrassPointsGenerator(width, jungleMaxHeight, maxHeight, topPoints);
        for (Vector2d grassPosition : generatorTop) {
            if (values.variant()) {
                grasses.put(grassPosition, new Grass(grassPosition, rand.nextInt(minGrassEnergy, maxGrassEnergy + 1)));
            } else {
                grasses.put(grassPosition, new Grass(grassPosition, values.grassEnergy()));
            }
        }

    }

    public void grassConsume() {
        List<Vector2d> grassPositions = new ArrayList<>(grasses.keySet());

        for (Vector2d grassPosition : grassPositions) {
            List<Animal> animal = animals.get(grassPosition);
            int maxEnergy = 0;
            Animal maxAnimal = null;

            if (animal != null) {

                animal.sort(
                        Comparator.comparingInt(Animal::getEnergy)
                                .thenComparingInt(Animal::getAge)
                                .thenComparingInt(Animal::getChildCount)
                                .reversed()
                );


                for (Animal a : animal) {
                    if (a.getAnimalType() == AnimalType.Victim) {
                        maxAnimal = a;
                        break;
                        }
                    }
                }

                Grass grass = grasses.get(grassPosition);
                if (maxAnimal != null) {
                    maxAnimal.setEnergy(Math.min(maxAnimal.getEnergy() + grass.getEnergy() , 2 * startEnergy));
                    maxAnimal.eatPlant();
                    grasses.remove(grassPosition);
                    mapChanged("Trawa została zjedzona na " + grassPosition);
                }

            }
        }


    public Map<Vector2d, List<Animal>> animalMultiplication() {
        Map<Vector2d, List<Animal>> babies = new HashMap<>();

        for (Vector2d animalPosition : animals.keySet()) {
            List<Animal> animal = animals.get(animalPosition);

            if (animal.size() >= 2) {
                animal.sort(
                        Comparator.comparingInt(Animal::getEnergy)
                                .thenComparingInt(Animal::getAge)
                                .thenComparingInt(Animal::getChildCount)
                                .reversed()
                );

                for (int i = 0; i < animal.size() - 1; i += 2) {
                    Animal a1 = animal.get(i);
                    Animal a2 = animal.get(i + 1);
                    if (a1.getEnergy() >= a1.getEnergyNeedToMultiplicate() && a2.getEnergy() >= a2.getEnergyNeedToMultiplicate()) {

                        GenomGenerator babyGenerator = new GenomGenerator(a1.getGEN_LENGTH(), values.minMutation(),  values.maxMutation());

                        PositionGenerator babyDirection = new PositionGenerator(width, maxHeight);

                        List<Integer> babyGenom = babyGenerator.childGenom(a1, a2);

                        Animal baby = new Animal(animalPosition, babyGenom, babyDirection.moveDirectionGenerator(), a1.getAnimalType(), this.values);

                        babies.computeIfAbsent(animalPosition, k -> new ArrayList<>()).add(baby);

                        a1.addChild(baby);
                        a2.addChild(baby);

                        a1.setEnergy(a1.getEnergy() - a1.getEnergyLostPerMultiplications());
                        a2.setEnergy(a2.getEnergy() - a2.getEnergyLostPerMultiplications());

                        a1.setChildCount(a1.getChildCount() + 1);
                        a2.setChildCount(a2.getChildCount() + 1);

                        mapChanged("Zwierzeta rozmnozyly sie na pozycji: " + a1.getPosition());
                    }
                }
            }
        }
        return babies;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();


        builder.append(drawHeader());


        for (int y = maxHeight - 1; y >= 0; y--) {
            builder.append(String.format("%3d: ", y));

            for (int x = 0; x < width; x++) {
                Vector2d currentPosition = new Vector2d(x, y);


                List<Animal> animalsHere = getAnimalsAt(currentPosition);
                if (animalsHere != null && !animalsHere.isEmpty()) {

                    builder.append(animalsHere.get(0).toString());
                }
                else if (grasses.containsKey(currentPosition)) {
                    builder.append("*");
                }
                else {
                    builder.append(".");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String drawHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(" y\\x ");
        for (int x = 0; x < width; x++) {
            sb.append(x % 10);
        }
        sb.append("\n");
        return sb.toString();
    }


    public int getGrassGrownPerDay() {
        return grassGrownPerDay;
    }

    public Map<Vector2d, List<Animal>> animalsKills() {
        Map<Vector2d, List<Animal>> animalsToKill = new HashMap<>();
        for (Vector2d animalPosition : animals.keySet()) {
            List<Animal> animalsHere = animals.get(animalPosition);

            if (animalsHere.size() >= 2) {
                animalsHere.sort(
                        Comparator.comparingInt(Animal::getEnergy)
                                .thenComparingInt(Animal::getAge)
                                .thenComparingInt(Animal::getChildCount)
                                .reversed()
                );

                List<Boolean> isAlive = new ArrayList<>(Collections.nCopies(10, true));

                for (int i = 0; i < animalsHere.size(); i++) {
                    Animal a1 = animalsHere.get(i);
                    if (a1.getAnimalType() == AnimalType.Predator) {

                        for (int j = animalsHere.size() - 1; j >= i + 1; j--) {
                            Animal a2 = animalsHere.get(j);
                            if (isAlive.get(j)) {

                                if (a1.getEnergy() - a2.getEnergy() >= a1.getMinEnergyToKill()) {

                                    int sumEnergy = a1.getEnergy() + a2.getEnergy();
                                    int x = rand.nextInt(sumEnergy + 1);

                                    if (x <= a1.getEnergy()) {

                                        isAlive.set(j, false);
                                        animalsToKill.computeIfAbsent(animalPosition, k -> new ArrayList<>()).add(a2);
                                        a1.setEnergy(Math.min(a1.getEnergy() + a2.getEnergy() , 2 * startEnergy));
                                        mapChanged("Zwierze zostalo zabite na pozycji: " + a1.getPosition());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return animalsToKill;
    }

    public boolean isGrassAt(Vector2d position) {
        return grasses.containsKey(position);
    }

    public void trackDeath(Animal animal, int day) {
        animal.setDeathDay(day);
        deadAnimalsCount++;
        deadAnimalsLifeSpanSum += animal.getAge();
    }

    public Statistics getStatistics() {
        List<Animal> allAnimals = animals.values().stream()
                .flatMap(List::stream)
                .toList();

        int animalCount = allAnimals.size();
        int grassCount = grasses.size();

        double avgEnergy = allAnimals.stream()
                .mapToInt(Animal::getEnergy)
                .average()
                .orElse(0.0);

        double avgChildren = allAnimals.stream()
                .mapToInt(Animal::getChildCount)
                .average()
                .orElse(0.0);

        double avgLifespan = deadAnimalsCount == 0 ? 0.0 : (double) deadAnimalsLifeSpanSum / deadAnimalsCount;

        Set<Vector2d> occupiedPositions = new HashSet<>(grasses.keySet());
        occupiedPositions.addAll(animals.keySet());

        int totalFields = width * maxHeight;
        int freeFieldsCount = Math.max(0, totalFields - occupiedPositions.size());

        List<Integer> mostPopularGenotype = allAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getGenom, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(List.of());

        return new Statistics(
                animalCount,
                grassCount,
                freeFieldsCount,
                mostPopularGenotype,
                avgEnergy,
                avgLifespan,
                avgChildren
        );
    }

    public boolean isPreferredField(Vector2d position) {
        int jungleMinHeight = (int) Math.floor(maxHeight * 0.4);
        int jungleMaxHeight = (int) Math.ceil(maxHeight * 0.6);

        int y = position.getY();
        return y >= jungleMinHeight && y < jungleMaxHeight;
    }
}




