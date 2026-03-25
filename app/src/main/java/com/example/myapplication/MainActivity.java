package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    SessionManager session;
    TextView tvWelcome;
    Button btnLoginLogout, btnViewCart;
    LinearLayout layoutCategories;
    ListView lvProducts;
    List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fix overlapping
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLoginLogout = findViewById(R.id.btnLoginLogout);
        btnViewCart = findViewById(R.id.btnViewCart);
        layoutCategories = findViewById(R.id.layoutCategories);
        lvProducts = findViewById(R.id.lvProducts);

        seedData();
        updateUI();

        btnLoginLogout.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                session.logout();
                updateUI();
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnViewCart.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("productId", selected.productId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (session.isLoggedIn()) {
            User user = db.appDao().getUserById(session.getUserId());
            tvWelcome.setText("Hello, " + (user != null ? user.fullName : "User"));
            btnLoginLogout.setText("Logout");
        } else {
            tvWelcome.setText("Welcome!");
            btnLoginLogout.setText("Login");
        }

        loadCategories();
        loadProducts(-1);
    }

    private void loadCategories() {
        layoutCategories.removeAllViews();
        List<Category> categories = db.appDao().getAllCategories();
        
        Button btnAll = new Button(this);
        btnAll.setText("All");
        btnAll.setOnClickListener(v -> loadProducts(-1));
        layoutCategories.addView(btnAll);

        for (Category cat : categories) {
            Button btn = new Button(this);
            btn.setText(cat.categoryName);
            btn.setOnClickListener(v -> loadProducts(cat.categoryId));
            layoutCategories.addView(btn);
        }
    }

    private void loadProducts(int categoryId) {
        if (categoryId == -1) {
            productList = db.appDao().getAllProducts();
        } else {
            productList = db.appDao().getProductsByCategory(categoryId);
        }

        List<String> displayList = new ArrayList<>();
        for (Product p : productList) {
            displayList.add(p.productName + " - $" + p.price);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvProducts.setAdapter(adapter);
    }

    private void seedData() {
        if (db.appDao().getAllCategories().isEmpty()) {
            db.appDao().insertUser(new User("admin", "123", "Admin User"));
            db.appDao().insertCategory(new Category("Electronics"));
            db.appDao().insertCategory(new Category("Clothing"));
            
            db.appDao().insertProduct(new Product(1, "Smartphone", "High-end smartphone", 800, ""));
            db.appDao().insertProduct(new Product(1, "Laptop", "Powerful gaming laptop", 1500, ""));
            db.appDao().insertProduct(new Product(2, "T-Shirt", "Cotton t-shirt", 20, ""));
            db.appDao().insertProduct(new Product(2, "Jeans", "Denim jeans", 50, ""));
        }
    }
}
