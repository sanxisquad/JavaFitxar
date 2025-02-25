package com.example.tiac_tac;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText loginNom, loginPassword;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assignar les vistes
        loginNom = findViewById(R.id.login_nom);
        loginPassword = findViewById(R.id.login_password);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        // Configura el botó d'ull per mostrar/amagar la contrasenya
        ivTogglePassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            isPasswordVisible = !isPasswordVisible;
            loginPassword.setSelection(loginPassword.getText().length());
        });

        // Lògica de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = loginNom.getText().toString();
                String password = loginPassword.getText().toString();

                if (nom.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Nom d'usuari i contrasenya són necessaris", Toast.LENGTH_SHORT).show();
                } else {
                    executarServei(Ip.LOGIN_URL, nom, password);
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RecuperarContrasenyaActivity.class);
                startActivity(intent);
            }
        });
    }

    // Funció per gestionar el servei de login
    private void executarServei(String URL, final String nom, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Imprimir la resposta del servidor al logcat
                        Log.d(TAG, "Resposta del servidor: " + response);

                        // Si la resposta del servidor és positiva
                        if (response.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Nom d'usuari o contrasenya incorrectes", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                // Eliminar el text addicional abans del JSON
                                String jsonResponseString = response.substring(response.indexOf("{"));

                                // Intentar convertir la resposta a JSON
                                JSONObject jsonResponse = new JSONObject(jsonResponseString);
                                if (jsonResponse.has("error")) {
                                    Toast.makeText(LoginActivity.this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Crear l'Intent per a MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("user_data", jsonResponse.toString());
                                    intent.putExtra("horaris_data", jsonResponse.getJSONArray("horaris").toString());
                                    startActivity(intent);
                                    finish(); // Tanca LoginActivity
                                }
                            } catch (Exception e) {
                                // Imprimir l'error al logcat
                                Log.e(TAG, "Error en el format de la resposta: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, "Error en el format de la resposta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nom_usuari", nom);
                params.put("password", password);
                return params;
            }
        };

        // Afegir la petició a la cua de sol·licituds
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}