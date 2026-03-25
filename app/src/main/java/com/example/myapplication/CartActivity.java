package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ListView lvCartItems;
    TextView tvTotalAmount;
    Button btnCheckout;
    AppDatabase db;
    SessionManager session;
    Order pendingOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Fix overlapping
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        lvCartItems = findViewById(R.id.lvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        loadCart();

        btnCheckout.setOnClickListener(v -> {
            if (pendingOrder != null) {
                // 1. Cập nhật trạng thái thành PAID và tính tổng tiền
                pendingOrder.status = "Paid";
                pendingOrder.totalAmount = db.appDao().getOrderTotal(pendingOrder.orderId);
                db.appDao().updateOrder(pendingOrder);

                Toast.makeText(this, "Checkout successful!", Toast.LENGTH_SHORT).show();

                // 2. Chuyển sang màn hình hiển thị Hóa đơn (Invoice)
                Intent intent = new Intent(CartActivity.this, InvoiceActivity.class);
                intent.putExtra("orderId", pendingOrder.orderId);
                startActivity(intent);
                finish(); // Đóng màn hình giỏ hàng
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCart() {
        int userId = session.getUserId();
        pendingOrder = db.appDao().getPendingOrder(userId);

        if (pendingOrder == null) {
            tvTotalAmount.setText("Total: $0.00");
            btnCheckout.setEnabled(false);
            return;
        }
        List<OrderDetail> details = db.appDao().getOrderDetails(pendingOrder.orderId);
        if (details.isEmpty()) {
            btnCheckout.setEnabled(false);
        }

        List<String> displayList = new ArrayList<>();
        double total = 0;

        for (OrderDetail d : details) {
            Product p = db.appDao().getProductById(d.productId);
            if (p != null) {
                displayList.add(p.productName + " (x" + d.quantity + ") - $" + (d.quantity * d.unitPrice));
                total += d.quantity * d.unitPrice;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvCartItems.setAdapter(adapter);
        tvTotalAmount.setText("Total: $" + total);
    }
}