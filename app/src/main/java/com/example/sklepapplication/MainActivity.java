package com.example.sklepapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<CheckBox, Integer> pricesMap;
    private EditText editTextCustomerName;
    private TextView totalPriceTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja komponentów UI
        editTextCustomerName = findViewById(R.id.edit_text_customer_name);
        totalPriceTextView = findViewById(R.id.text_view_price);
        databaseHelper = new DatabaseHelper(this);

        // Ustawienie przycisku do obliczania ceny
        Button calculatePriceButton = findViewById(R.id.button_calculate_price);
        calculatePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateOrderPrice();
            }
        });

        // Ustawienie przycisku do składania zamówienia
        Button placeOrderButton = findViewById(R.id.button_place_order);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        // Mapowanie checkboxów na ceny
        pricesMap = new HashMap<>();
        pricesMap.put((CheckBox) findViewById(R.id.realmadryt), 350);
        pricesMap.put((CheckBox) findViewById(R.id.borussiadort), 280);
        pricesMap.put((CheckBox) findViewById(R.id.acmilan), 250);
        pricesMap.put((CheckBox) findViewById(R.id.pomorzanin), 300);

        // Ustawienie toolbaru
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // Metoda do składania zamówienia
    private void placeOrder() {
        String customerName = editTextCustomerName.getText().toString().trim();
        if (customerName.isEmpty()) {
            Toast.makeText(this, "Proszę wprowadzić imię i nazwisko/Firmę", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalPrice = calculateTotalPrice();
        String orderSummary = createOrderSummary(customerName, totalPrice);
        saveOrderToDatabase(customerName, totalPrice);
        sendOrder(orderSummary);
        clearFields();
        resetCheckBoxes();
    }

    // Metoda do zapisywania zamówienia w bazie danych
    private void saveOrderToDatabase(String customerName, float totalPrice) {
        List<String> selectedProducts = new ArrayList<>();
        for (Map.Entry<CheckBox, Integer> entry : pricesMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            if (checkBox.isChecked()) {
                selectedProducts.add(checkBox.getText().toString());
            }
        }

        Order order = new Order(customerName, selectedProducts, totalPrice, getCurrentDateTime());
        databaseHelper.addOrder(order);
    }

    // Metoda do obliczania ceny zamówienia
    private void calculateOrderPrice() {
        Log.d("MainActivity", "calculateOrderPrice() called");
        float totalPrice = 0;
        for (Map.Entry<CheckBox, Integer> entry : pricesMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            if (checkBox.isChecked()) {
                int pricePerUnit = entry.getValue();
                totalPrice += pricePerUnit;
            }
        }

        totalPriceTextView.setText(String.format(Locale.getDefault(), "Cena zamówienia: %.2f zł", totalPrice));
    }

    // Metoda do obliczania całkowitej ceny zamówienia
    private float calculateTotalPrice() {
        float totalPrice = 0;
        for (Map.Entry<CheckBox, Integer> entry : pricesMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            if (checkBox.isChecked()) {
                int pricePerUnit = entry.getValue();
                totalPrice += pricePerUnit;
            }
        }
        return totalPrice;
    }

    // Metoda do tworzenia podsumowania zamówienia
    private String createOrderSummary(String customerName, float totalPrice) {
        StringBuilder orderSummary = new StringBuilder();
        orderSummary.append("Zamówienie ze sklepu Kocia Łapka:\n");
        orderSummary.append("Produkty:\n");

        for (Map.Entry<CheckBox, Integer> entry : pricesMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            int price = entry.getValue();
            if (checkBox.isChecked()) {
                orderSummary.append(checkBox.getText()).append(" - ").append(price).append(" zł\n");
            }
        }

        orderSummary.append(String.format(Locale.getDefault(), "Cena razem: %.2f zł\n", totalPrice));
        orderSummary.append("Data zamówienia: ").append(getCurrentDateTime()).append("\n");
        orderSummary.append("Zamawiający: ").append(customerName).append("\n");

        return orderSummary.toString();
    }

    // Metoda do wysyłania zamówienia
    private void sendOrder(String orderSummary) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Zamówienie");
        intent.putExtra(Intent.EXTRA_TEXT, orderSummary);
        startActivity(intent);
    }

    // Metoda do pobierania aktualnej daty i czasu
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Metoda do czyszczenia pól tekstowych
    private void clearFields() {
        editTextCustomerName.setText("");
        totalPriceTextView.setText("");
    }

    // Metoda do resetowania zaznaczeń checkboxów
    private void resetCheckBoxes() {
        for (Map.Entry<CheckBox, Integer> entry : pricesMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            checkBox.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_orders_list) {
            showOrdersList();
            return true;
        } else if (id == R.id.menu_save_login_data) {
            saveLoginData();
            return true;
        } else if (id == R.id.menu_about_author) {
            showAboutAuthor();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // Metoda do wyświetlania listy zamówień
    private void showOrdersList() {
        Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
        startActivity(intent);
    }

    // Metoda do zapisywania danych logowania
    private void saveLoginData() {
        // kod do zapisywania danych
    }

    // Metoda do wyświetlania informacji o autorze
    private void showAboutAuthor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("O autorze");
        builder.setMessage("Autorem jest: Kamil Paczkowski 4P");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
