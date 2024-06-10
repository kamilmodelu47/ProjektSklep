package com.example.sklepapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Orders.db";
    private static final int DATABASE_VERSION = 1;

    // Nazwy tabeli i kolumn
    static final String TABLE_ORDERS = "orders";
    static final String COLUMN_ID = "id";
    static final String COLUMN_CUSTOMER_NAME = "customer_name";
    static final String COLUMN_PRODUCTS = "products";
    static final String COLUMN_TOTAL_PRICE = "total_price";
    static final String COLUMN_ORDER_DATE = "order_date";

    // SQL zapytanie do utworzenia tabeli
    private static final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CUSTOMER_NAME + " TEXT," +
                    COLUMN_PRODUCTS + " TEXT," +
                    COLUMN_TOTAL_PRICE + " REAL," +
                    COLUMN_ORDER_DATE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Utworzenie tabeli zamówień
        db.execSQL(SQL_CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aktualizacja tabeli (wymagana tylko, jeśli zmieni się wersja bazy danych)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // Metoda do pobierania wszystkich zamówień
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_ORDERS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                String customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME));
                String productsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTS));
                float totalPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));

                // Konwersja ciągu produktów na listę produktów
                List<String> productsList = Arrays.asList(productsString.split(", "));

                // Utworzenie obiektu zamówienia i dodanie go do listy
                Order order = new Order(customerName, productsList, totalPrice, orderDate);
                orderList.add(order);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Błąd podczas odczytu zamówień z bazy danych: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return orderList;
    }

    // Metoda do dodawania nowego zamówienia
    public void addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CUSTOMER_NAME, order.getCustomerName());
            values.put(COLUMN_PRODUCTS, convertProductsListToString(order.getProducts()));
            values.put(COLUMN_TOTAL_PRICE, order.getTotalPrice());
            values.put(COLUMN_ORDER_DATE, order.getOrderDate());

            // Dodanie nowego zamówienia do bazy danych
            db.insert(TABLE_ORDERS, null, values);
            Log.d("DatabaseHelper", "Dodano nowe zamówienie do bazy danych.");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Błąd podczas dodawania zamówienia do bazy danych: " + e.getMessage());
        }
    }

    // Pomocnicza metoda do konwersji listy produktów na ciąg znaków
    private String convertProductsListToString(List<String> productsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String product : productsList) {
            stringBuilder.append(product).append(", ");
        }
        // Usunięcie ostatniego przecinka i spacji, jeśli są obecne
        String productsString = stringBuilder.toString();
        if (productsString.endsWith(", ")) {
            productsString = productsString.substring(0, productsString.length() - 2);
        }
        return productsString;
    }
}
