package com.bea.lifecyclev6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private final String correctMail = "test@gmail.com";
    private final String correctPassword = "123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EditText mailInput = findViewById(R.id.mailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button btn = findViewById(R.id.button);

        //Log in btn -> Homepage activity
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Inloggning
                String email = mailInput.getText().toString().trim();
                String password = passwordInput.getText().toString();

                if (email.equals(correctMail) && password.equals(correctPassword)) {
                    Intent i = new Intent(MainActivity.this, HomepageActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "Wrong mail or password", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Register link -> register activity
        TextView link = findViewById(R.id.registerLink);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}