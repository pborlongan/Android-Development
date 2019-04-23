package pborlongan1.nait.ca.catcafeappoinmentmaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity
{
    DBManager manager;
    SQLiteDatabase database;
    Cursor cursor;

    private static final int REQUEST_SIGNUP = 0;
    EditText emailLogin;
    EditText passwordLogin;
    Button loginButton;
    TextView signupLink;

    static String loginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new DBManager(this);
        database = manager.getReadableDatabase();

        emailLogin = (EditText) findViewById(R.id.input_email);
        passwordLogin = (EditText)findViewById(R.id.input_password);
        loginButton = (Button)findViewById(R.id.btn_login);
        signupLink = (TextView)findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login()
    {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        boolean accountCorrect = IsEqual(email, password);

        if(accountCorrect)
        {
            loginEmail = email;

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        else
        {

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        startActivity(new Intent(this, Booking.class));
    }

    public boolean IsEqual(String email, String password)
    {
        cursor = null;
        try
        {
            String db_query = "select * from account where account_email='" + email + "' and account_pass='" +password + "';";
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

    public void onLoginFailed()
    {
        loginButton.setEnabled(true);
        Toast.makeText(this, "Error! Please check email and password", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.setError("enter a valid email address");
            valid = false;
        } else {
            emailLogin.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordLogin.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordLogin.setError(null);
        }

        return valid;
    }


}
