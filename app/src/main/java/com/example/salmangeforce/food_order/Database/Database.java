package com.example.salmangeforce.food_order.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.salmangeforce.food_order.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "EatIt.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        String sqlTable = "OrderDetail";
        String[] sqlColumns = {"ProductId", "ProductName", "Quantity", "Price", "Discount"};

        sqLiteQueryBuilder.setTables(sqlTable);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, sqlColumns, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do {
                result.add(new Order(cursor.getString(cursor.getColumnIndex("ProductId")),
                    cursor.getString(cursor.getColumnIndex("ProductName")),
                    cursor.getString(cursor.getColumnIndex("Quantity")),
                    cursor.getString(cursor.getColumnIndex("Price")),
                    cursor.getString(cursor.getColumnIndex("Discount"))
                ));
            }
            while (cursor.moveToNext());
        }
        return result;
    }


    public void addToCart(Order order)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail('ProductId', 'ProductName', 'Quantity', 'Price', 'Discount')" +
                        "VALUES('%s', '%s', '%s', '%s', '%s');",
                        order.getProductId(), order.getProductName(), order.getQuantity(), order.getPrice(), order.getDiscount());
        sqLiteDatabase.execSQL(query);
    }


    public void cleanCart()
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        sqLiteDatabase.execSQL(query);
    }

}//class ends
