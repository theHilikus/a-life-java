package com.github.thehilikus.alife.agent.vision;

import com.github.thehilikus.alife.agent.vision.api.Shape;

public class Rectangle implements Shape {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean contains(double x, double y) {
        return (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height);
    }
}
