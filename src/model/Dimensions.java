package model;

import java.util.Objects;

public class Dimensions {
    private double width;
    private double height;
    private double depth;

    public Dimensions (double width, double height, double depth){
        this.width = width;
        this.height = height;
        this.depth = depth;
    }



    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return width + "cm x " + height + "cm x " + depth + "cm";

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Dimensions dimensions)) return false;

        return Double.compare(dimensions.width, width) == 0
                && Double.compare(dimensions.height, height) == 0
                && Double.compare(dimensions.depth, depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }
}
