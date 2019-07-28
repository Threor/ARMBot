package de.arm.bot.model.math;

/**
 * A class representing a simple 2d vector
 *
 * @author Team ARM
 */
public class Vector2d {

    /**
     * The x-value of the vector
     */
    private double x;

    /**
     * The y-value of the vector
     */
    private double y;

    /** Default constructor, initializes all field
     * @param x The x-value of the vector
     * @param y The y-value of the vector
     */
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Initializes an empty vector (0/0)
     */
    public Vector2d() {
        this(0, 0);
    }

    /** Adds the coordinates of two vectors
     * @param vector2d The second vector
     * @return The new Vector
     */
    public Vector2d add(Vector2d vector2d) {
        this.x += vector2d.x;
        this.y += vector2d.y;
        return this;
    }

    /** Normalizes a vector (not mathematically), so that the long side has a value of 1
     * @return The normalizes vector
     */
    public Vector2d norm() {
        if (x == 0 && y == 0) return this;
        double temp = Math.max(Math.abs(x), Math.abs(y));
        this.x /= temp;
        this.y /= temp;
        return this;
    }

    /** Getter for the attribute x
     * @return The x-value of the vector
     */
    public double getX() {
        return x;
    }

    /** Getter for the attribute y
     * @return The y-value of the vector
     */
    public double getY() {
        return y;
    }

    /** Calculates and returns the length of the vector
     * @return The length of the vector
     */
    public double getLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /** Calculates and returns the dot product of this vector and another one
     * @param vector2d The second vector
     * @return The calculates dot product
     */
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
