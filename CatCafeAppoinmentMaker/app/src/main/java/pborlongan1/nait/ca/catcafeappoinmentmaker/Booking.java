package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

public class Booking extends AppCompatActivity implements View.OnClickListener {
    LoginActivity login = new LoginActivity();
    DBManager manager;
    SQLiteDatabase database;
    Cursor cursor;
    TextView accountName;
    CardsAdapter cAdapter;

    Button btnAppointment;
    ListView listView;
    String email = login.loginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        manager = new DBManager(this);
        database = manager.getReadableDatabase();

        accountName = (TextView)findViewById(R.id.account_name);
        listView = (ListView)findViewById(R.id.lv_list_appointments);

        String name = FindAccount(email);
        accountName.setText(name);

        btnAppointment = (Button)findViewById(R.id.btn_book_appointment);
        btnAppointment.setOnClickListener(this);

        PopulateAppointmentList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {

            }
        });

    }

    private void PopulateAppointmentList()
    {
        int getAccountID = GetAccountID(email);
        cursor = database.query(DBManager.BOOKING_TABLE, null, DBManager.C_BOOKING_ACCOUNT_ID + "=" + getAccountID, null, null, null, null);
        this.startManagingCursor(cursor);

        cAdapter = new CardsAdapter(this, cursor);
        listView.setAdapter(cAdapter);
    }

    private int GetAccountID(String email)
    {
        int getID = 0;
        try{
            cursor = database.query(DBManager.ACCOUNTS_TABLE, null, DBManager.C_ACCOUNT_EMAIL + "='" + email + "'", null, null, null, null);
            this.startManagingCursor(cursor);

            while(cursor.moveToNext())
            {
                getID = cursor.getInt(cursor.getColumnIndex(DBManager.C_ACCOUNT_ID));
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error: " + ex, Toast.LENGTH_LONG).show();
        }

        return getID;
    }

    private String FindAccount(String email)
    {
        String getName = "";
        try{
            cursor = database.query(DBManager.ACCOUNTS_TABLE, null, DBManager.C_ACCOUNT_EMAIL + "='" + email + "'", null, null, null, null);
            this.startManagingCursor(cursor);

            List<String> names =  new ArrayList<String>();

            while(cursor.moveToNext())
            {
                getName = cursor.getString(cursor.getColumnIndex(DBManager.C_ACCOUNT_NAME));
                names.add(getName);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error: " + ex, Toast.LENGTH_LONG).show();
        }

        return getName;
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_book_appointment:
            {
                startActivity(new Intent(this, NewAppointment.class));
                break;
            }
        }
    }


}
