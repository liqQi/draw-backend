package com.chyorange.drawandguess.models;

import java.util.List;

public class DrawPath {
    private List<DrawPathPoint> path;
    private float width;
    private int color;

    public List<DrawPathPoint> getPath() {
        return path;
    }

    public void setPath(List<DrawPathPoint> path) {
        this.path = path;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
