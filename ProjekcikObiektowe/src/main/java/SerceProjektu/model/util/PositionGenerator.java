package SerceProjektu.model.util;

import SerceProjektu.model.MoveDirection;
import SerceProjektu.model.Vector2d;

import java.util.Random;

public class PositionGenerator {

    private final int maxWidth;
    private final int maxHeight;
    private final Random random;

    public PositionGenerator(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.random = new Random();
    }

    public Vector2d positionGenerator() {
        int x = random.nextInt(maxWidth);
        int y = random.nextInt(maxHeight);
        return new Vector2d(x, y);
    }

    public MoveDirection moveDirectionGenerator() {
        int i = random.nextInt(8);
        return MoveDirection.values()[i];
    }

}
