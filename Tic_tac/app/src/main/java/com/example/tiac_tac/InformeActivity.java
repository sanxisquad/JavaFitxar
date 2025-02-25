package com.example.tiac_tac;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class InformeActivity extends AppCompatActivity {

    private String userData;
    private EditText etDiaInici;
    private EditText etDiaFi;
    private Button btnMostrar;
    private LinearLayout llInforme;
    private String horarisData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe);

        // Obtenir les dades de l'usuari de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");

        // Enllaça les vistes
        etDiaInici = findViewById(R.id.etDiaInici);
        etDiaFi = findViewById(R.id.etDiaFi);
        btnMostrar = findViewById(R.id.btnMostrar);
        llInforme = findViewById(R.id.llInforme);

        // Obtenir l'usuari_id de les dades de l'usuari
        final int usuariId;
        try {
            JSONObject userJson = new JSONObject(userData);
            usuariId = userJson.getInt("id_usuari");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Configura el DatePickerDialog per etDiaInici
        etDiaInici.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(InformeActivity.this, (view, year1, month1, dayOfMonth) -> {
                String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                etDiaInici.setText(date);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Configura el DatePickerDialog per etDiaFi
        etDiaFi.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(InformeActivity.this, (view, year1, month1, dayOfMonth) -> {
                String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                etDiaFi.setText(date);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Configura el botó per mostrar els resultats
        btnMostrar.setOnClickListener(v -> {
            final String diaInici = etDiaInici.getText().toString();
            final String diaFi = etDiaFi.getText().toString();

            if (diaInici.isEmpty() || diaFi.isEmpty()) {
                Toast.makeText(InformeActivity.this, "Si us plau, introdueix les dates d'inici i finalització", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fer la sol·licitud a l'API per obtenir les hores treballades
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = Ip.INFORME_URL + "?usuari_id=" + usuariId + "&dia_inici=" + diaInici + "&dia_fi=" + diaFi;
            Log.d("InformeActivity", "URL: " + url);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("InformeActivity", "Resposta de l'API: " + response);
                                // Eliminar el missatge de connexió exitosa
                                String jsonResponse = response.replace("Conexion exitosa", "");
                                JSONArray jsonArray = new JSONArray(jsonResponse);

                                llInforme.removeAllViews(); // Netejar les vistes anteriors

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject fitxatge = jsonArray.getJSONObject(i);
                                    String data = fitxatge.getString("data");
                                    String horaEntrada = fitxatge.getString("hora_entrada");
                                    String horaSortida = fitxatge.getString("hora_sortida");
                                    String recompteHores = fitxatge.getString("recompte_hores");

                                    View cardView = LayoutInflater.from(InformeActivity.this).inflate(R.layout.card_informe, llInforme, false);

                                    TextView tvData = cardView.findViewById(R.id.tvData);
                                    TextView tvHoraEntrada = cardView.findViewById(R.id.tvHoraEntrada);
                                    TextView tvHoraSortida = cardView.findViewById(R.id.tvHoraSortida);
                                    TextView tvRecompteHores = cardView.findViewById(R.id.tvRecompteHores);

                                    tvData.setText("Data: " + data);
                                    tvHoraEntrada.setText("Hora d'entrada: " + horaEntrada);
                                    tvHoraSortida.setText("Hora de sortida: " + horaSortida);
                                    tvRecompteHores.setText("Hores treballades: " + recompteHores);

                                    llInforme.addView(cardView);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(InformeActivity.this, "Error en processar les dades", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("InformeActivity", "Error en la connexió: " + error.getMessage());
                            Toast.makeText(InformeActivity.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                            // Afegir log per veure la resposta de l'error
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String errorResponse = new String(error.networkResponse.data);
                                Log.e("InformeActivity", "Resposta d'error: " + errorResponse);
                            }
                        }
                    });

            requestQueue.add(stringRequest);
        });

        // Configura la barra de navegació inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_inici);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intentMain = new Intent(InformeActivity.this, MainActivity.class);
            intentMain.putExtra("user_data", userData);
            intentMain.putExtra("horaris_data", horarisData);
            intentMain.putExtra("isRunning", getIntent().getBooleanExtra("isRunning", false));
            intentMain.putExtra("horaInici", getIntent().getStringExtra("horaInici"));
            if (id == R.id.nav_inici) {
                startActivity(intentMain);
                return true;
            } else if (id == R.id.nav_horari) {
                Intent intentHorari = new Intent(InformeActivity.this, HorariActivity.class);
                intentHorari.putExtras(intentMain.getExtras());
                startActivity(intentHorari);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intentProfile = new Intent(InformeActivity.this, Perfil.class);
                intentProfile.putExtras(intentMain.getExtras());
                startActivity(intentProfile);
                return true;
            } else if (id == R.id.nav_incidencia) {
                Intent intentIncidencia = new Intent(InformeActivity.this, Incidencia.class);
                intentIncidencia.putExtras(intentMain.getExtras());
                startActivity(intentIncidencia);
                return true;
            } else {
                return false;
            }
        });
    }
}