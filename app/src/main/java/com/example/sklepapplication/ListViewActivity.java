package com.example.sklepapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> lista;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        listView = findViewById(R.id.list_view);
        lista = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        listOrders();
    }

    private void listOrders() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Orders",null);
        if (cursor != null && cursor.moveToNext()){
            do{
                int index = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int productsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCTS);
                int customerIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_NAME);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_PRICE);
                int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE);


                String przechowywanie = cursor.getString(index);
                int price = cursor.getInt(priceIndex);
                String product = cursor.getString(productsIndex);
                String customer = cursor.getString(customerIndex);
                String date = cursor.getString(dateIndex);



                String orderDetails = "ID: " + przechowywanie +
                        "\nPrice: PLN" + price +
                        "\nDate: " + date +
                        "\nProduct: " + product+
                        "\nCustomer: " + customer;

                lista.add(orderDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ArrayAdapter<String> arrAdap = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(arrAdap);
    }
}