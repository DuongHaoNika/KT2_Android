package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int orderId;
    public int userId;
    public String orderDate;
    public double totalAmount;
    public String status; // "Pending", "Paid"

    public Order(int userId, String orderDate, double totalAmount, String status) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
