package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "categoryId",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int productId;
    public int categoryId;
    public String productName;
    public String description;
    public double price;
    public String imageUrl;

    public Product(int categoryId, String productName, String description, double price, String imageUrl) {
        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
