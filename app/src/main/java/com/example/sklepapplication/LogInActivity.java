package com.example.sklepapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {

    EditText login, password;
    Button loginBtn;
    TextView dontHaveAcc;

    DBLogin dbLogin;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        login = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.log_in_btn);
        dontHaveAcc = findViewById(R.id.dont_have_acc);

        dontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish(); // Zamyka LogInActivity
            }
        });



        dbLogin = new DBLogin(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = login.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("")||pass.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                }else {
                    Boolean checkUserPass = checkuserPassword(user,pass);
                    if (checkUserPass == true){
                        Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                        builder.setTitle("Error message");
                        builder.setMessage("Username and password are wrong");
                        builder.setIcon(R.drawable.baseline_error_outline_24);
                        builder.setPositiveButton("OK", null);
                        builder.setCancelable(true);

                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        alertDialog.getWindow().setGravity(Gravity.TOP);
                    }
                }
            }
        });

    }

    private Boolean checkuserPassword(String user, String pass) {
        sqLiteDatabase = dbLogin.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Users WHERE username=? AND password=?", new String[]{user,pass});

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }
}