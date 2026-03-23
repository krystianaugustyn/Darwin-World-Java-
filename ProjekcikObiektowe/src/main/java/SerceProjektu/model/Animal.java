package SerceProjektu.model;

import SerceProjektu.model.util.GenomGenerator;
import SerceProjektu.model.util.PositionGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Animal {
    private final StartValues values;

    private final int GEN_LENGTH;
    private int energy;
    private final int energyLostPerDay;
    private final int EnergyNeedToMultiplicate;
    private final int EnergyLostPerMultiplications;
    private final AnimalType animaltype;
    private final int minEnergyToKill;
    private int age=0;
    private int childCount = 0;

    private List<Integer> genom;
    private Vector2d position;
    private MoveDirection direction;

    private int plantsEaten = 0;
    private List<Animal> children = new ArrayList<>();
    private Integer deathDay = null;
    private int activeGeneIndex = 0;

    public Animal(Vector2d position , List<Integer> genom, MoveDirection direction,AnimalType animaltype, StartValues values) {
        this.values = values;

        this.position = position;
        this.genom = genom;
        this.direction = direction;
        this.animaltype = animaltype;

        this.GEN_LENGTH = values.genomLength();
        this.energy = values.startEnergyAnimals();
        this.energyLostPerDay = values.energyLostPerDay();
        this.EnergyNeedToMultiplicate = values.energyForMultiplication();
        this.EnergyLostPerMultiplications = values.energyLostForMultiplication();
        this.minEnergyToKill = values.minEnergyToKill();
    }

    public List<Integer> getGenom() {
        return genom;
    }

    public Vector2d getPosition() {
        return position;
    }

    public int getEnergyNeedToMultiplicate() {
        return EnergyNeedToMultiplicate;
    }

    public int getEnergyLostPerMultiplications() {
        return EnergyLostPerMultiplications;
    }

    public MoveDirection getDirection() {
        return direction;
    }

    public int getGEN_LENGTH() {
        return GEN_LENGTH;
    }

    public void setDirection(MoveDirection direction){
        this.direction = direction;
    }

    public void setPosition(Vector2d position){
        this.position = position;
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    public int getEnergy(){
        return energy;
    }
    public int getEnergyLostPerDay() {
        return energyLostPerDay;
    }

    @Override
    public String toString() {
        return switch (this.direction) {
            case NORTH -> "^";
            case NORTH_EAST -> "↗";
            case EAST -> ">";
            case SOUTH_EAST -> "↘";
            case SOUTH -> "v";
            case SOUTH_WEST -> "↙";
            case WEST -> "<";
            case NORTH_WEST -> "↖";
        };
    }

    public AnimalType getAnimalType() {
        return animaltype;
    }

    public int getMinEnergyToKill() {
        return minEnergyToKill;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public void eatPlant() {
        this.plantsEaten++;
    }

    public void addChild(Animal child) {
        this.children.add(child);
    }

    public void setDeathDay(int day) {
        this.deathDay = day;
    }

    public Integer getDeathDay() {
        return deathDay;
    }

    public int getPlantsEaten() {
        return plantsEaten;
    }

    public List<Animal> getChildren() {
        return children;
    }

    public void setActiveGeneIndex(int index) {
        this.activeGeneIndex = index;
    }

    public int getActiveGeneIndex() {
        return activeGeneIndex;
    }

    public int getDescendantsCount() {
        Set<Animal> descendants = new HashSet<>();
        findDescendants(this, descendants);
        return descendants.size();
    }

    private void findDescendants(Animal parent, Set<Animal> visited) {
        for (Animal child : parent.getChildren()) {
            if (!visited.contains(child)) {
                visited.add(child);
                findDescendants(child, visited);
            }
        }
    }
}
