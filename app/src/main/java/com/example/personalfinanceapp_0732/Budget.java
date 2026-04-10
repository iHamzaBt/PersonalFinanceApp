package com.example.personalfinanceapp_0732;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_table")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String category;
    private double amount;

    public Budget(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}