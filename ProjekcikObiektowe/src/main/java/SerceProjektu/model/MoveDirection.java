package SerceProjektu.model;

public enum MoveDirection {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int value;

    MoveDirection(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public MoveDirection next(int gen) {
        return MoveDirection.values()[(this.value + gen) % 8];
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_WEST -> new Vector2d(-1, 1);
        };
    }

    public MoveDirection opposite() {
        return MoveDirection.values()[(this.value + 4) % 8];
    }
}
