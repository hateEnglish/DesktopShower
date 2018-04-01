package com.xubao.client.pojo;

import javafx.scene.paint.Color;

/**
 * @Author xubao
 * @Date 2018/3/31
 */
public class MousePointInfo {
    private int x;
    private int y;
    private Color color;
    //半径
    private int radius;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
