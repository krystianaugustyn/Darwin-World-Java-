package SerceProjektu.model;

public record StartValues(
        int width,
        int height,
        boolean variant,
        int startGrassPoints,
        int grassEnergy,
        int minGrassEnergy,
        int maxGrassEnergy,
        int newGrassPerDay,
        int startAnimals,
        int startEnergyAnimals,
        int energyLostPerDay,
        int energyForMultiplication,
        int energyLostForMultiplication,
        int minMutation,
        int maxMutation,
        int genomLength,
        int minEnergyToKill
) {
}
