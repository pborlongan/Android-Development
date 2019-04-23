package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity
{
    DBManager manager;
    SQLiteDatabase database;
    Cursor cursor;

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    EditText phoneText;
    Button signupButton;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = (EditText)findViewById(R.id.et_name);
        emailText = (EditText)findViewById(R.id.et_email);
        passwordText = (EditText)findViewById(R.id.et_password);
        phoneText = (EditText)findViewById(R.id.et_phone);
        signupButton = (Button)findViewById(R.id.btn_signup);
        loginLink = (TextView)findViewById(R.id.link_login);

        manager = new DBManager(this);
        database = manager.getReadableDatabase();

        signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void signup()
    {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String phone = phoneText.getText().toString();

        CreateAccount(name,email,password,phone);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private boolean DoesEmailExist(String email)
    {
        cursor = null;
        try
        {
            String db_query = "select * from account where account_email='" + email + "';";
            cursor = database.rawQuery(db_query, null);

            if(cursor.getCount() <= 0)
            {
                cursor.close();
                return false;
            }
            else
            {
                cursor.close();
                return true;
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this, "Error: " + ex, Toast.LENGTH_LONG).show();
        }
        cursor.close();
        return true;
    }

    private void CreateAccount(String name, String email, String password, String phone)
    {
        try
        {
            database = manager.getReadableDatabase();
            ContentValues value = new ContentValues();

            value.put(DBManager.C_ACCOUNT_NAME, name);
            value.put(DBManager.C_ACCOUNT_EMAIL, email);
            value.put(DBManager.C_ACCOUNT_PASSWORD, password);
            value.put(DBManager.C_ACCOUNT_PHONE, phone);
            database.insert(DBManager.ACCOUNTS_TABLE, null, value);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String phone = phoneText.getText().toString();
        boolean existEmail = DoesEmailExist(email);

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if(existEmail)
        {
            emailText.setError("email is already in the database");
            valid = false;
        }else
        {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 10) {
            phoneText.setError("phone number not right");
            valid = false;
        } else {
            phoneText.setError(null);
        }

        return valid;
    }
}
