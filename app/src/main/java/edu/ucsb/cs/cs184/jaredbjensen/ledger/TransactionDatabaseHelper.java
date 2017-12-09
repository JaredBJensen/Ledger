package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TransactionDatabaseHelper extends SQLiteOpenHelper {
    private static final String CreateTable = "CREATE TABLE Transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT NOT NULL, date INTEGER NOT NULL, amount REAL NOT NULL, description TEXT NOT NULL, category TEXT NOT NULL);";
    private static final String CreateBalanceTable = "CREATE TABLE Balances (id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER NOT NULL, amount REAL NOT NULL);";
    private static final String DatabaseName = "TransactionDatabase.db";
    private static TransactionDatabaseHelper Instance;
    private List<OnDatabaseChangeListener> Listeners;

    private TransactionDatabaseHelper(Context context) {
        super(context, DatabaseName, null, 1);
        Listeners = new ArrayList<>();
        getWritableDatabase();
    }

    public static void Initialize(Context context) {
        Instance = new TransactionDatabaseHelper(context);
    }

    public static TransactionDatabaseHelper GetInstance() {
        return Instance;
    }

    public void Subscribe(OnDatabaseChangeListener listener) {
        Listeners.add(listener);
    }

    private boolean TryUpdate(Cursor cursor) {
        try {
            cursor.moveToFirst();
        } catch (SQLiteConstraintException exception) {
            return false;
        } finally {
            cursor.close();
        }
        NotifyListeners();
        return true;
    }

    private void NotifyListeners() {
        for (OnDatabaseChangeListener listener : Listeners) {
            listener.OnDatabaseChange();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public interface OnDatabaseChangeListener {
        void OnDatabaseChange();
    }

    public void insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(table, nullColumnHack, values);
        NotifyListeners();
    }

    public void update(String table, int rating, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + table + " SET Rating=" + rating + " WHERE id=" + id);
    }

    public void clearDB(int startDate, int endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Transactions WHERE date >= " + startDate + "AND date <= " + endDate);
        NotifyListeners();
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE Transactions");
        db.execSQL("DROP TABLE Balances");
        db.execSQL(CreateTable);
        db.execSQL(CreateBalanceTable);
        NotifyListeners();
    }

    public ArrayList<Transaction> getTransactions(String filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Transaction> transactions = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM Transactions " + filter + " ORDER BY date", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String type = cursor.getString(cursor.getColumnIndex("type"));
                long date = cursor.getLong(cursor.getColumnIndex("date"));
                float amount = cursor.getFloat(cursor.getColumnIndex("amount"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String category = cursor.getString(cursor.getColumnIndex("category"));

                transactions.add(new Transaction(type, date, amount, description, category));

                cursor.moveToNext();
            }
        }
        cursor.close();

        return transactions;
    }

    public float getTransactionSumByCategory(String filter) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT sum(amount) FROM Transactions " + filter, null);
        if (cursor.moveToFirst()) {
            return cursor.getFloat(0);
        }
        cursor.close();

        return 0f;
    }
}