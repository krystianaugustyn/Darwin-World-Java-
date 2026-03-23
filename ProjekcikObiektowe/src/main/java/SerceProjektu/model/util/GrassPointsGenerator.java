package SerceProjektu.model.util;

import SerceProjektu.model.Vector2d;

import java.util.Random;
import java.util.*;

public class GrassPointsGenerator implements Iterable<Vector2d> {
    private final int maxWidth;
    private final int minHeigth;
    private final int maxHeight;
    private final int count;
    private final List<Vector2d> allPositions;
    private final Random rand;

    public GrassPointsGenerator(int maxWidth, int minHeigth, int maxHeight, int count){
        this.maxWidth = maxWidth;
        this.minHeigth = minHeigth;
        this.maxHeight = maxHeight;
        this.count = count;
        this.rand = new Random();
        this.allPositions = generateAllPositions();

        Collections.shuffle(allPositions);
    }

    private List<Vector2d> generateAllPositions() {
        List<Vector2d> positions = new ArrayList<>();
        for (int x = 0; x < maxWidth; x++) {
            for (int y = minHeigth; y < maxHeight; y++) {
                positions.add(new Vector2d(x, y));
                }
            }

        return positions;
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return new Iterator<Vector2d>() {
            private int generatedCount = 0;

            @Override
            public boolean hasNext() {
                return generatedCount < count;
            }

            @Override
            public Vector2d next() {
                if (!hasNext()) {
                    throw new UnsupportedOperationException("No more positions to generate");
                }

                Vector2d position = allPositions.get(generatedCount);
                generatedCount++;
                return position;
            }
        };
    }

}