package com.example.tiac_tac;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {
    private String userData;
    private String horarisData;
    private EditText etEmail, etCurrentPassword, etNewPassword, etVerifyPassword;
    private ImageView ivToggleCurrentPassword, ivToggleNewPassword, ivToggleVerifyPassword;
    private Button btnUpdatePassword;
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isVerifyPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Enllaça les vistes

        etEmail = findViewById(R.id.etEmail);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etVerifyPassword = findViewById(R.id.etVerifyPassword);
        ivToggleCurrentPassword = findViewById(R.id.ivToggleCurrentPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivToggleVerifyPassword = findViewById(R.id.ivToggleVerifyPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        // Carrega les dades de l'usuari
        carregarDadesPerfil();

        // Configura el botó per actualitzar la contrasenya
        btnUpdatePassword.setOnClickListener(view -> actualitzarContrasenya());

        // Configura els marges per a Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Obtenir les dades de l'usuari de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");
        // Configura la barra de navegació inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Selecciona l'element de perfil
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;
            if (id == R.id.nav_inici) {
                // Redirigeix a l'activitat principal
                navIntent = new Intent(Perfil.this, MainActivity.class);
            } else if (id == R.id.nav_horari) {
                // Redirigeix a l'activitat d'horari
                navIntent = new Intent(Perfil.this, HorariActivity.class);
            } else if (id == R.id.nav_incidencia) {
                // Redirigeix a l'activitat d'incidència
                navIntent = new Intent(Perfil.this, Incidencia.class);
            } else if (id == R.id.nav_profile) {
                // Redirigeix a l'activitat de perfil
                navIntent = new Intent(Perfil.this, Perfil.class);
            } else {
                return false;
            }
            navIntent.putExtra("user_data", userData);
            navIntent.putExtra("horaris_data", horarisData);
            navIntent.putExtra("isRunning", getIntent().getBooleanExtra("isRunning", false));
            navIntent.putExtra("horaInici", getIntent().getStringExtra("horaInici"));
            startActivity(navIntent);
            return true;
        });

        // Configura els botons d'ull per mostrar/amagar la contrasenya
        ivToggleCurrentPassword.setOnClickListener(view -> {
            if (isCurrentPasswordVisible) {
                etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleCurrentPassword.setImageResource(R.drawable.ic_eye_off);
            }
            isCurrentPasswordVisible = !isCurrentPasswordVisible;
            etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        });

        ivToggleNewPassword.setOnClickListener(view -> {
            if (isNewPasswordVisible) {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleNewPassword.setImageResource(R.drawable.ic_eye_off);
            }
            isNewPasswordVisible = !isNewPasswordVisible;
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        ivToggleVerifyPassword.setOnClickListener(view -> {
            if (isVerifyPasswordVisible) {
                etVerifyPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleVerifyPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etVerifyPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleVerifyPassword.setImageResource(R.drawable.ic_eye_off);
            }
            isVerifyPasswordVisible = !isVerifyPasswordVisible;
            etVerifyPassword.setSelection(etVerifyPassword.getText().length());
        });
    }

    private void carregarDadesPerfil() {
        Intent intent = getIntent();
        String userData = intent.getStringExtra("user_data");

        try {
            JSONObject jsonResponse = new JSONObject(userData);
            etEmail.setText(jsonResponse.getString("email"));
            // Afegeix altres camps segons sigui necessari
        } catch (Exception e) {
            Toast.makeText(this, "Error en carregar les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualitzarContrasenya() {
        String email = etEmail.getText().toString();
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String verifyPassword = etVerifyPassword.getText().toString();

        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || verifyPassword.isEmpty()) {
            Toast.makeText(this, "Tots els camps són obligatoris.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(verifyPassword)) {
            Toast.makeText(this, "La nova contrasenya i la verificació no coincideixen.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(Ip.PERFIL_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", currentPassword);
                params.put("new_password", newPassword);
                params.put("verify_password", verifyPassword);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(param.getKey());
                    postData.append('=');
                    postData.append(param.getValue());
                }

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.toString().getBytes());
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(Perfil.this, "Contrasenya actualitzada correctament.", Toast.LENGTH_SHORT).show();
                        // Redirigeix a l'activitat de login
                        Intent intent = new Intent(Perfil.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Perfil.this, "Error en actualitzar la contrasenya.", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(Perfil.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showSettings() {
        // Implementa accions
    }
}