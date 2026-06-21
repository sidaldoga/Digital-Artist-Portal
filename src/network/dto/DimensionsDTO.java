package network.dto;

import java.io.Serializable;

public class DimensionsDTO implements Serializable {
    private double width;
    private double height;
    private double depth;

    public DimensionsDTO(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
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
}