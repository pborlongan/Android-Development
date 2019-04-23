package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewAppointment extends AppCompatActivity implements View.OnClickListener
{
    LoginActivity login = new LoginActivity();
    DBManager manager;
    SQLiteDatabase database;
    Cursor cursor;

    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = DateFormat.getDateInstance();

    Button btnMakeAppointment;
    Spinner spinTime, spinPeople;
    TextView tvChosenDate;

    int timeIndex, peopleIndex;
    int guests;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        manager = new DBManager(this);
        database = manager.getReadableDatabase();

        spinTime = (Spinner)findViewById(R.id.spinner_time);
        spinPeople = (Spinner)findViewById(R.id.spinner_guests);
        tvChosenDate = (TextView) findViewById(R.id.chosen_date);

        btnMakeAppointment = (Button)findViewById(R.id.btn_make_appointment);
        btnMakeAppointment.setOnClickListener(this);

        calendar.add(Calendar.DATE, 0);
        PopulateSpinners();

        spinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long row) {
                timeIndex = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        spinPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long row) {
                peopleIndex = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
    }

    private void updateTimeLabel()
    {
        tvChosenDate.setText(formatter.format(calendar));
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day)
        {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateTimeLabel();
        }
    };

    public void chooseDate(View view)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void PopulateSpinners()
    {
        PopulateTime();
        PopulateGuests();
    }

    private void PopulateGuests()
    {
        List<String> people =  new ArrayList<String>();
        people.add("1 person");
        people.add("2 persons");
        people.add("3 persons");
        people.add("4 persons");
        people.add("5 persons");

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, people);

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinGuests = (Spinner) findViewById(R.id.spinner_guests);
        spinGuests.setAdapter(adapter3);
    }

    private void PopulateTime()
    {
        List<String> timeHours =  new ArrayList<String>();
        timeHours.add("9:00 AM");
        timeHours.add("10:00 AM");
        timeHours.add("11:00 AM");
        timeHours.add("1:00 PM");
        timeHours.add("2:00 PM");
        timeHours.add("3:00 PM");
        timeHours.add("4:00 PM");
        timeHours.add("5:00 PM");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, timeHours){
        };
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinHours = (Spinner) findViewById(R.id.spinner_time);
            spinHours.setAdapter(adapter);
    }

    private int NumberOfGuests()
    {
        int guests = 0;
        Cursor cur = database.rawQuery("SELECT SUM(number_of_guests) FROM booking where booking_date='"+ tvChosenDate.getText().toString() + "' AND booking_time_in='"+ spinTime.getSelectedItem().toString()+"';", null);
        if(cur.moveToFirst())
        {
            guests = cur.getInt(0);
            return guests;
        }
        return guests;
    }

    @Override
    public void onClick(View view)
    {
        int guests = NumberOfGuests();
        boolean booked = CheckAccount();

        if(guests >= 10)
        {
            Toast.makeText(this, "Sorry, the maximum number of people for that date and time was reached!", Toast.LENGTH_LONG).show();
        }
        else if (booked == true)
        {
            Toast.makeText(this, "Sorry, you already booked for this date and time!", Toast.LENGTH_LONG).show();
        }
        else
        {
            int people = GetNumberOfGuests(peopleIndex);

            String email = login.loginEmail;
            int id = FindAccount(email);
            try
            {
                database = manager.getReadableDatabase();
                ContentValues value = new ContentValues();

                value.put(DBManager.C_BOOKING_DATE, tvChosenDate.getText().toString());
                value.put(DBManager.C_BOOKING_TIME_IN, spinTime.getSelectedItem().toString());
                value.put(DBManager.C_BOOKING_PEOPLE, people);
                value.put(DBManager.C_BOOKING_ACCOUNT_ID, id);
                database.insert(DBManager.BOOKING_TABLE, null, value);

                Toast.makeText(this, "Your appointment has been booked!", Toast.LENGTH_LONG).show();
                SetNotification();
                this.finish();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void SetNotification()
    {
        long currentTime= System.currentTimeMillis();
        String time = GetTime(timeIndex);
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        try
        {
            Date date = format.parse(time);
            long timeInMilli = date.getTime();
            if(timeInMilli <= currentTime + 10800000)
            {
                scheduleNotification(getNotification("Time to meet those cats soon! :)"), 1000);
            }
            else
            {
                scheduleNotification(getNotification("Time to meet those cats soon! :)"), timeInMilli - 10800000);
            }
        }
        catch (ParseException e)
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private void scheduleNotification(Notification notification, long delay)
    {
        int id = (int)getCount();
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id+1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content)
    {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Cat cafe reminder");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_lock_lock);
        builder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21)
            builder.setVibrate(new long[0]);
        return builder.build();
    }

    private boolean CheckAccount()
    {
        String email = login.loginEmail;
        int id = FindAccount(email);
        Cursor cur = database.rawQuery("SELECT * FROM booking where booking_date='"+ tvChosenDate.getText().toString() + "' AND booking_time_in='"+ spinTime.getSelectedItem().toString()+"' AND account_id=" + id, null);
        if(cur.moveToFirst())
        {
            return true;
        }
        return false;
    }

    private String GetTime(int timeIndex)
    {
        String dateTime = "";

        switch (timeIndex)
        {
            case 0:
            {
                dateTime = tvChosenDate.getText().toString() + " 09:00";
                return dateTime;
            }
            case 1:
            {
                dateTime = tvChosenDate.getText().toString() + " 10:00";
                return dateTime;
            }
            case 2:
            {
                dateTime = tvChosenDate.getText().toString() + " 11:00";
                return dateTime;
            }
            case 3:
            {
                dateTime = tvChosenDate.getText().toString() + " 13:00";
                return dateTime;
            }
            case 4:
            {
                dateTime = tvChosenDate.getText().toString() + " 14:00";
                return dateTime;
            }
            case 5:
            {
                dateTime = tvChosenDate.getText().toString() + " 15:00";
                return dateTime;
            }
            case 6:
            {
                dateTime = tvChosenDate.getText().toString() + " 16:00";
                return dateTime;
            }
            case 7:
            {
                dateTime =  tvChosenDate.getText().toString() + " 17:00";
                return dateTime;
            }
        }
        return dateTime;
    }



    private int GetNumberOfGuests(int peopleIndex)
    {
        int peeps = 1;
        switch (peopleIndex)
        {
            case 0:
            {
                peeps = 1;
                break;
            }
            case 1:
            {
                peeps = 2;
                break;
            }
            case 2:
            {
                peeps = 3;
                break;
            }
            case 3:
            {
                peeps = 4;
                break;
            }
            case 4:
            {
                peeps = 5;
                break;
            }
        }

        return peeps;
    }

    private int FindAccount(String email)
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


    public long getCount()
    {
        SQLiteDatabase db = manager.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DBManager.BOOKING_TABLE);
        db.close();
        return count;
    }

}
