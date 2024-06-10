package com.example.sklepapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    EditText login, password, repassword;
    TextView haveACC;
    Button signInBtn;
    DBLogin dbLogin;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        // Inicjalizacja komponentów UI
        login = findViewById(R.id.new_username);
        password = findViewById(R.id.new_password);
        repassword = findViewById(R.id.new_repassword);
        signInBtn = findViewById(R.id.sign_in_btn);
        haveACC = findViewById(R.id.have_acc);

        // Przejście do ekranu logowania, jeśli użytkownik ma już konto
        haveACC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish(); // Zamyka SignInActivity
            }
        });

        dbLogin = new DBLogin(this);

        // Obsługa kliknięcia przycisku rejestracji
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = login.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                // Walidacja pól wejściowych
                if (login.getText().toString().length() <= 0){
                    login.setError("Wpisz nazwę użytkownika");
                }else if(password.getText().toString().length() <= 0){
                    password.setError("Wpisz hasło");
                }else if(repassword.getText().toString().length() <= 0){
                    repassword.setError("Wpisz hasło drugi raz");
                } else if (login.equals("") || password.equals("") || repassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "Puste pola", Toast.LENGTH_SHORT).show();
                }else {
                    // Sprawdzenie zgodności haseł
                    if (pass.equals(repass)){
                        Boolean checkUser = checkUsername(user);
                        if (!checkUser){
                            Boolean inserts = insertData(user, pass);
                            if (inserts){
                                Toast.makeText(getApplicationContext(), "Rejestracja udana", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Rejestracja nieudana", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Nazwa użytkownika już istnieje", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Wyświetlenie dialogu, jeśli hasła nie pasują do siebie
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setTitle("Spróbuj ponownie");
                        builder.setIcon(R.drawable.baseline_error_outline_24);
                        builder.setMessage("Hasła nie pasują do siebie");
                        builder.show();
                    }
                }
            }
        });
    }

    // Metoda do dodawania danych użytkownika do bazy danych
    private Boolean insertData(String user, String pass) {
        sqLiteDatabase = dbLogin.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("password", pass);

        long rec = sqLiteDatabase.insert("users", null, contentValues);

        return rec != -1;
    }

    // Metoda do sprawdzania, czy nazwa użytkownika już istnieje
    private Boolean checkUsername(String user) {
        sqLiteDatabase = dbLogin.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Users WHERE username=?", new String[]{user});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
