package com.example.git_practica;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtName, edtEmail;
    private Button btnRegister;
    private TextView tvUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);
        tvUserData = findViewById(R.id.tvUserData);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();

                String userData = "Nombre: " + name + "\nCorreo: " + email;
                tvUserData.setText(userData);

                mensaje();
            }
        });

    }
    //Primer Commit de Kevin
// primer commit de diego
    public void mensaje () {
        Toast.makeText(getApplicationContext(), "Este es un mensaje para probar los cambios.", Toast.LENGTH_SHORT).show();
    }
    }



