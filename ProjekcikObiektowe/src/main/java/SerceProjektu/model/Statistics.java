package SerceProjektu.model;

import java.util.List;

public record Statistics(
        int animalCount,
        int grassCount,
        int freeFieldsCount,
        List<Integer> mostPopularGenotype,
        double avgEnergy,
        double avgLifespan,
        double avgChildren
) {
}