package com.example.budget_management.Model;

public class Catalog {
    private String name;
    private int color;
    private String type;
    private int icon;

    Catalog() {

    }

    public Catalog(String name, int color, String type, int icon) {
        this.name = name;
        this.color = color;
        this.type = type;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
