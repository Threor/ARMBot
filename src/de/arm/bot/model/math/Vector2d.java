package de.arm.bot.model.math;

public class Vector2d {

    private double x;

    private double y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d() {
        this(0, 0);
    }

    public Vector2d add(Vector2d vector2d) {
        this.x += vector2d.x;
        this.y += vector2d.y;
        return this;
    }

    public Vector2d norm() {
        if (x == 0 && y == 0) return this;
        double temp = Math.max(Math.abs(x), Math.abs(y));
        this.x /= temp;
        this.y /= temp;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double dotProduct(Vector2d vector2d) {
        return x * vector2d.x + y * vector2d.y;
    }

    @Override
    public String toString() {
        return "Vector2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
