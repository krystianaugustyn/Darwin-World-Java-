package SerceProjektu.model.util;

import SerceProjektu.model.Animal;
import SerceProjektu.model.StartValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenomGenerator {
    private final int minMutation;
    private final int maxMutation;

    private final int genomLength;
    private final Random random;

    public GenomGenerator(int genomLength, int minMutation, int maxMutation) {
        this.minMutation = minMutation;
        this.maxMutation = maxMutation;
        this.genomLength = genomLength;
        this.random = new Random();
    }

    public List<Integer> generateGenom() {
        List<Integer> genom = new ArrayList<>();
        for (int i = 0; i < genomLength; i++) {
            genom.add(random.nextInt(8));
        }
        return genom;
    }

    public List<Integer> childGenom(Animal animal1, Animal animal2) {
        int Energy1 =  animal1.getEnergy();
        int Energy2 = animal2.getEnergy();
        int SumEnergy = Energy1 + Energy2;
        float p1 = (float) Energy1 / SumEnergy;
        int firstLength = (int) (p1 * genomLength);
        int secondLength = genomLength - firstLength;
        int LeftOrRight = random.nextInt(2);
        List<Integer> strongerGenom = animal1.getGenom();
        List<Integer> weakGenom = animal2.getGenom();

        List<Integer> genom = new ArrayList<>();

        if (LeftOrRight == 0) {
            for (int i = 0; i < firstLength; i++) {
                genom.add(strongerGenom.get(i));
            }
            for(int i = firstLength; i < genomLength; i++) {
                genom.add(weakGenom.get(i));
            }
        }

        if (LeftOrRight == 1) {
            for (int i = 0; i < secondLength; i++) {
                genom.add(weakGenom.get(i));
            }
            for(int i = secondLength; i < genomLength; i++) {
                genom.add(strongerGenom.get(i));
            }
        }

        int x = random.nextInt(minMutation, maxMutation + 1);

        for (int i = 0; i < x; i++) {
            int index = random.nextInt(genomLength);
            int value = random.nextInt(8);
            genom.set(index, value);

        }





        return genom;
    }
}
