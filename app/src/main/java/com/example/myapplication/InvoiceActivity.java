package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.db.AppDatabase;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.OrderDetail;
import com.example.myapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    TextView tvInvoiceId, tvInvoiceDate, tvInvoiceTotal;
    ListView lvInvoiceItems;
    Button btnBackToHome;
    AppDatabase db;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Fix overlapping
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);

        tvInvoiceId = findViewById(R.id.tvInvoiceId);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        lvInvoiceItems = findViewById(R.id.lvInvoiceItems);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        orderId = getIntent().getIntExtra("orderId", -1);
        loadInvoice();

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void loadInvoice() {
        Order order = db.appDao().getOrderById(orderId);
        if (order == null) return;

        tvInvoiceId.setText("Order ID: #" + order.orderId);
        tvInvoiceDate.setText("Date: " + order.orderDate);
        tvInvoiceTotal.setText("Grand Total: $" + order.totalAmount);

        List<OrderDetail> details = db.appDao().getOrderDetails(order.orderId);
        List<String> displayList = new ArrayList<>();

        for (OrderDetail d : details) {
            Product p = db.appDao().getProductById(d.productId);
            displayList.add(p.productName + " (x" + d.quantity + ") - $" + (d.quantity * d.unitPrice));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvInvoiceItems.setAdapter(adapter);
    }
}