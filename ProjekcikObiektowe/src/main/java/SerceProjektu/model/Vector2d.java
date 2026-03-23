package SerceProjektu.model;

import java.util.Objects;

public class Vector2d {

    private final int x;
    private final int y;
    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public Vector2d add(Vector2d v) {
        return new Vector2d(this.x + v.x, this.y + v.y);
    }

    public Vector2d upperRight(Vector2d other){
        return new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
    }

    public Vector2d lowerLeft(Vector2d other){
        return new Vector2d(Math.min(this.x,other.x), Math.min(this.y,other.y));
    }

    @Override
    public boolean equals(Object other){
        if(this == other){
            return true; }

        if (!(other instanceof Vector2d))
            return false;

        Vector2d that = (Vector2d)other;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }
}
