package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DBManager extends SQLiteOpenHelper
{
    static final String DB_NAME = "catef.db";
    static final int DB_VERSION = 1;

    static final String ACCOUNTS_TABLE = "account";
    static final String BOOKING_TABLE = "booking";

    static final String C_ACCOUNT_ID = BaseColumns._ID;
    static final String C_ACCOUNT_NAME = "account_name";
    static final String C_ACCOUNT_EMAIL = "account_email";
    static final String C_ACCOUNT_PASSWORD = "account_pass";
    static final String C_ACCOUNT_PHONE = "account_phone";

    static final String C_BOOKING_ID = BaseColumns._ID;
    static final String C_BOOKING_DATE = "booking_date";
    static final String C_BOOKING_TIME_IN = "booking_time_in";
    static final String C_BOOKING_PEOPLE = "number_of_guests";
    static final String C_BOOKING_ACCOUNT_ID = "account_id";

    public DBManager(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String accountTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);", ACCOUNTS_TABLE, C_ACCOUNT_ID, C_ACCOUNT_NAME, C_ACCOUNT_EMAIL, C_ACCOUNT_PASSWORD,  C_ACCOUNT_PHONE);
        String bookingTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INT, %s INT);", BOOKING_TABLE, C_BOOKING_ID, C_BOOKING_DATE, C_BOOKING_TIME_IN, C_BOOKING_PEOPLE, C_BOOKING_ACCOUNT_ID);

        database.execSQL(accountTable);
        database.execSQL(bookingTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("drop table if exists " + ACCOUNTS_TABLE);
        database.execSQL("drop table if exists " + BOOKING_TABLE);
        onCreate(database);
    }
}
