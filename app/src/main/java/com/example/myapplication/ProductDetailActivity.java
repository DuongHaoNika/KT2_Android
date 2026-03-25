package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.OrderDetail;
import com.example.myapplication.model.Product;

import java.util.Date;

public class ProductDetailActivity extends AppCompatActivity {
    TextView tvName, tvPrice, tvDescription;
    Button btnAddToCart;
    AppDatabase db;
    SessionManager session;
    int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Fix overlapping with System Bar/Action Bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        productId = getIntent().getIntExtra("productId", -1);
        Product product = db.appDao().getProductById(productId);

        if (product != null) {
            tvName.setText(product.productName);
            tvPrice.setText("$" + product.price);
            tvDescription.setText(product.description);
        }

        btnAddToCart.setOnClickListener(v -> addToCart(product));
    }

    private void addToCart(Product product) {
        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int userId = session.getUserId();
        Order pendingOrder = db.appDao().getPendingOrder(userId);
        if (pendingOrder == null) {
            pendingOrder = new Order(userId, new Date().toString(), 0.0, "Pending");
            long orderId = db.appDao().insertOrder(pendingOrder);
            pendingOrder.orderId = (int) orderId;
        }

        OrderDetail detail = new OrderDetail(pendingOrder.orderId, product.productId, 1, product.price);
        db.appDao().insertOrderDetail(detail);

        Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
        finish();
    }
}