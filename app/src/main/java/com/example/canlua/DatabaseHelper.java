package com.example.canlua;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.canlua.DatabaseContract.CustomerTable;
import com.example.canlua.DatabaseContract.HistoryTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "canlua.db";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " +
                HistoryTable.TABLE_NAME + " (" +
                HistoryTable._ID + " INTEGER, " +
                HistoryTable.COLUMN_TENGIONG + " VARCHAR, " +
                HistoryTable.COLUMN_DONGIA + " MONEY, " +
                HistoryTable.COLUMN_BAOBI + " INTEGER, " +
                HistoryTable.COLUMN_TONGBAO + " INTEGER, " +
                HistoryTable.COLUMN_TONGKG + " DECIMAL, " +
                HistoryTable.COLUMN_THANHTIEN + " MONEY, " +
                HistoryTable.COLUMN_TIENCOC + " MONEY, " +
                HistoryTable.COLUMN_TIMESTAMP + " VARCHAR PRIMARY KEY" +
                ");";

        //a customer have id to specify (autoincrement or the time the customer added)
        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " +
                CustomerTable.TABLE_NAME + " (" +
                CustomerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CustomerTable.COLUMN_TENKH + " VARCHAR, " +
                CustomerTable.COLUMN_SDT + " VARCHAR, " +
                CustomerTable.COLUMN_TIMESTAMP + " VARCHAR" +
                ");";
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryTable.TABLE_NAME);
        onCreate(db);
    }

    //add new customer
    public void addCustomer(Customer customer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());//time lấy khi ấn OK
        sqLiteDatabase = this.getWritableDatabase();

        //
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.CustomerTable.COLUMN_TENKH, customer.getHoTen());
        if (customer.getSDT().length() > 0)
            cv.put(DatabaseContract.CustomerTable.COLUMN_SDT, customer.getSDT());
        else
            cv.put(DatabaseContract.CustomerTable.COLUMN_SDT, "(trống)");
        cv.put(DatabaseContract.CustomerTable.COLUMN_TIMESTAMP, date);
        //
        sqLiteDatabase.insert(DatabaseContract.CustomerTable.TABLE_NAME, null, cv);
        sqLiteDatabase.close();
    }

    public Customer getCustomer(long id) {
        String selection = CustomerTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Customer customer = new Customer();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor =
                sqLiteDatabase.query(CustomerTable.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
        if (cursor.moveToFirst()) {
            customer.setID(cursor.getLong(cursor.getColumnIndex(CustomerTable._ID)));
            customer.setHoTen(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_TENKH)));
            customer.setSDT(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_SDT)));
            customer.setDate(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_TIMESTAMP)));
        }
        cursor.close();
        return customer;
    }

    //get all customers
    public ArrayList<Customer> getAllCustomers(String orderBy) {
        ArrayList<Customer> customers = new ArrayList<Customer>();
        String[] criterial = null;
        /*String selectQuery = "SELECT * FROM " + DatabaseContract.CustomerTable.TABLE_NAME +
                " ORDER BY " + DatabaseContract.CustomerTable.COLUMN_TENKH + " ASC, "
                + DatabaseContract.CustomerTable._ID + " DESC;";
         */
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = //sqLiteDatabase.rawQuery(selectQuery, null);
                sqLiteDatabase.query(CustomerTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        orderBy);
        if (cursor.moveToFirst()) {
            do {
                Customer customer = new Customer();
                customer.setID(cursor.getLong(cursor.getColumnIndex(CustomerTable._ID)));
                customer.setDate(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_TIMESTAMP)));
                customer.setHoTen(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_TENKH)));
                customer.setSDT(cursor.getString(cursor.getColumnIndex(CustomerTable.COLUMN_SDT)));
                customers.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return customers;
    }

    //update customer info
    public int updateCustomer(Customer customer) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CustomerTable.COLUMN_TENKH, customer.getHoTen());
        if (customer.getSDT().length() > 0)
            values.put(DatabaseContract.CustomerTable.COLUMN_SDT, customer.getSDT());
        else
            values.put(DatabaseContract.CustomerTable.COLUMN_SDT, "(trống)");
        String whereClause = CustomerTable._ID + "=?";
        String[] whereArgs = {String.valueOf(customer.getID())};
        return sqLiteDatabase.update(CustomerTable.TABLE_NAME, values, whereClause, whereArgs);
    }

    // Deleting single customer
    public void deleteCustomer(Customer customer) {
        sqLiteDatabase = this.getWritableDatabase();
        String whereClause = CustomerTable._ID + "=?";
        String[] whereArgs = {String.valueOf(customer.getID())};
        sqLiteDatabase.delete(CustomerTable.TABLE_NAME, whereClause, whereArgs);
        deleteHistoryAll(customer.getID());
    }

    //add new customer
    public void addHistory(History history) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());//khi thêm sẽ lấy thời gian ngay khi click
        sqLiteDatabase = this.getWritableDatabase();

        //
        ContentValues cv = new ContentValues();
        cv.put(HistoryTable.COLUMN_TIMESTAMP, date);
        cv.put(HistoryTable.COLUMN_TENGIONG, history.getTenGiongLua());
        cv.put(HistoryTable.COLUMN_DONGIA, history.getDonGia());
        cv.put(HistoryTable.COLUMN_BAOBI, history.getBaoBi());
        cv.put(HistoryTable.COLUMN_TONGBAO, history.getSoBao());
        cv.put(HistoryTable.COLUMN_THANHTIEN, history.getThanhTien());
        cv.put(HistoryTable.COLUMN_TIENCOC, history.getTienCoc());
        cv.put(HistoryTable.COLUMN_TONGKG, history.getTongSoKG());
        cv.put(HistoryTable._ID, history.getID());
        //
        sqLiteDatabase.insert(HistoryTable.TABLE_NAME, null, cv);
        sqLiteDatabase.close();
    }

    //get one history
    public History getHistory(String timestamp) {
        String selection = HistoryTable.COLUMN_TIMESTAMP + "=?";
        String[] selectionArgs = {timestamp};
        History history = new History();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor =
                sqLiteDatabase.query(HistoryTable.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
        if (cursor.moveToFirst()) {
            history.setID(cursor.getLong(cursor.getColumnIndex(HistoryTable._ID)));
            history.setTenGiongLua(cursor.getString(cursor.getColumnIndex(HistoryTable.COLUMN_TENGIONG)));
            history.setTimestamp(cursor.getString(cursor.getColumnIndex(HistoryTable.COLUMN_TIMESTAMP)));
            history.setDonGia(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_DONGIA)));
            history.setBaoBi(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_BAOBI)));
            history.setSoBao(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_TONGBAO)));
            history.setThanhTien(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_THANHTIEN)));
            history.setTienCoc(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_TIENCOC)));
            history.setTongSoKG(cursor.getDouble(cursor.getColumnIndex(HistoryTable.COLUMN_TONGKG)));
        }
        cursor.close();
        return history;
    }


    //get all histories of a customer
    public ArrayList<History> getAllHistories(long id) {
        ArrayList<History> histories = new ArrayList<History>();
        String selection = CustomerTable._ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        /*String selectQuery = "SELECT * FROM " + DatabaseContract.CustomerTable.TABLE_NAME +
                " ORDER BY " + DatabaseContract.CustomerTable.COLUMN_TENKH + " ASC, "
                + DatabaseContract.CustomerTable._ID + " DESC;";
         */
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = //sqLiteDatabase.rawQuery(selectQuery, null);
                sqLiteDatabase.query(HistoryTable.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        HistoryTable.COLUMN_TIMESTAMP + " DESC");
        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setID(cursor.getLong(cursor.getColumnIndex(HistoryTable._ID)));
                history.setTenGiongLua(cursor.getString(cursor.getColumnIndex(HistoryTable.COLUMN_TENGIONG)));
                history.setTimestamp(cursor.getString(cursor.getColumnIndex(HistoryTable.COLUMN_TIMESTAMP)));
                history.setDonGia(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_DONGIA)));
                history.setBaoBi(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_BAOBI)));
                history.setSoBao(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_TONGBAO)));
                history.setThanhTien(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_THANHTIEN)));
                history.setTienCoc(cursor.getInt(cursor.getColumnIndex(HistoryTable.COLUMN_TIENCOC)));
                history.setTongSoKG(cursor.getDouble(cursor.getColumnIndex(HistoryTable.COLUMN_TONGKG)));
                histories.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return histories;
    }

    //update history info
    public int updateHistory(History history) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HistoryTable.COLUMN_TENGIONG, history.getTenGiongLua());
        values.put(HistoryTable.COLUMN_DONGIA, history.getDonGia());
        values.put(HistoryTable.COLUMN_BAOBI, history.getBaoBi());
        values.put(HistoryTable.COLUMN_THANHTIEN, history.getThanhTien());
        values.put(HistoryTable.COLUMN_TIENCOC, history.getTienCoc());
        String whereClause = HistoryTable.COLUMN_TIMESTAMP + "=?";
        String[] whereArgs = {String.valueOf(history.getTimestamp())};
        return sqLiteDatabase.update(HistoryTable.TABLE_NAME, values, whereClause, whereArgs);
    }

    // Deleting single history
    public void deleteHistory(History history) {
        sqLiteDatabase = this.getWritableDatabase();
        String whereClause = HistoryTable.COLUMN_TIMESTAMP + "=?";
        String[] whereArgs = {String.valueOf(history.getTimestamp())};
        sqLiteDatabase.delete(HistoryTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteHistoryAll(long id) {
        sqLiteDatabase = this.getWritableDatabase();
        String whereClause = HistoryTable._ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        sqLiteDatabase.delete(HistoryTable.TABLE_NAME, whereClause, whereArgs);
    }
}