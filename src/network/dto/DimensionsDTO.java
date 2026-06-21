package network.dto;

import java.io.Serializable;

public class DimensionsDTO implements Serializable {
    private int id;
    private double width;
    private double height;
    private double depth;

    public DimensionsDTO(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    public DimensionsDTO(int id, double width, double height, double depth) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public int getId() {
        return id;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Dimensions{id=" + id + ", " + width + "cm x " + height + "cm x " + depth + "cm}";
    }
}