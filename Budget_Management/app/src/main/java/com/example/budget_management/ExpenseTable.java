package com.example.budget_management;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense")
public class ExpenseTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int amount;
    private String paymentType;
    private String description;

    public ExpenseTable() {

    }

    public ExpenseTable(int id, int amount, String paymentType, String description) {
        this.id = id;
        this.amount = amount;
        this.paymentType = paymentType;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}