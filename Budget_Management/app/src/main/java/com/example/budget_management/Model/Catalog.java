package com.example.budget_management.Model;

public class Catalog {
    private String name;
    private String color;
    private String type;
    private String icon;

    Catalog() {

    }

    public Catalog(String name, String color, String type, String icon) {
        this.name = name;
        this.color = color;
        this.type = type;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
