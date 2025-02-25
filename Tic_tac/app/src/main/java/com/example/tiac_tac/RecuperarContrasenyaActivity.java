package com.example.tiac_tac;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RecuperarContrasenyaActivity extends AppCompatActivity {

    private EditText etEmail, etDNI, etNewPassword, etVerifyPassword;
    private ImageView ivToggleNewPassword, ivToggleVerifyPassword;
    private boolean isNewPasswordVisible = false;
    private boolean isVerifyPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasenya);

        // Enllaça les vistes
        etEmail = findViewById(R.id.etEmail);
        etDNI = findViewById(R.id.etDNI);
        etNewPassword = findViewById(R.id.etNewPassword);
        etVerifyPassword = findViewById(R.id.etVerifyPassword);
        ivToggleNewPassword = findViewById(R.id.ivToggleNewPassword);
        ivToggleVerifyPassword = findViewById(R.id.ivToggleVerifyPassword);
        Button btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Configura els botons d'ull per mostrar/amagar la contrasenya
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

        // Configura el botó per actualitzar la contrasenya
        btnUpdatePassword.setOnClickListener(view -> actualitzarContrasenya());

        // Configura el botó per tornar a la pantalla de login
        btnBackToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RecuperarContrasenyaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tanca RecuperarContrasenyaActivity
        });
    }

    private void actualitzarContrasenya() {
        String email = etEmail.getText().toString();
        String dni = etDNI.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String verifyPassword = etVerifyPassword.getText().toString();

        if (email.isEmpty() || dni.isEmpty() || newPassword.isEmpty() || verifyPassword.isEmpty()) {
            Toast.makeText(this, "Tots els camps són obligatoris.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(verifyPassword)) {
            Toast.makeText(this, "La nova contrasenya i la verificació no coincideixen.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.RECUPERAR_CONTRASENYA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim(); // Eliminar espais en blanc
                        response = response.replace("Conexion exitosa", "").trim(); // Eliminar "Conexion exitosa"
                        Toast.makeText(RecuperarContrasenyaActivity.this, response, Toast.LENGTH_SHORT).show();
                        if (response.equals("Contrasenya actualitzada correctament.")) {
                            // Redirigeix a l'activitat de login
                            Intent intent = new Intent(RecuperarContrasenyaActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Tanca RecuperarContrasenyaActivity
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RecuperarContrasenyaActivity.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("dni", dni);
                params.put("new_password", newPassword);
                params.put("verify_password", verifyPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}