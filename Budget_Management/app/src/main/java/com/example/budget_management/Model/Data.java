package com.example.budget_management.Model;

public class Data {
    private int amount;
    private String type;
    private String note;
    private String id;
    private String date;
    private String color;
    private int icon;
    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public int getIcon() { return icon; }

    public void setIcon(int icon) { this.icon = icon; }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Data(){

    }

    public Data(int amount, String type, String note, String id, String date, String color, int icon) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
        this.color = color;
        this.icon = icon;
    }
}
