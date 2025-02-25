package com.example.tiac_tac;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class HorariActivity extends AppCompatActivity {

    private String userData;
    private String horarisData;
    private TableLayout tlHorari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horari);

        // Obtenir les dades de l'usuari i els horaris de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");

        Log.d("HorariActivity", "Dades de l'usuari: " + userData);
        Log.d("HorariActivity", "Dades dels horaris: " + horarisData);

        // Configura la barra de navegació inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_horari); // Selecciona l'element d'horari
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;
            if (id == R.id.nav_inici) {
                // Redirigeix a l'activitat principal
                navIntent = new Intent(HorariActivity.this, MainActivity.class);
            } else if (id == R.id.nav_horari) {
                // Redirigeix a l'activitat d'horari
                navIntent = new Intent(HorariActivity.this, HorariActivity.class);
            } else if (id == R.id.nav_incidencia) {
                // Redirigeix a l'activitat d'incidència
                navIntent = new Intent(HorariActivity.this, Incidencia.class);
            } else if (id == R.id.nav_profile) {
                // Redirigeix a l'activitat de perfil
                navIntent = new Intent(HorariActivity.this, Perfil.class);
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

        // Assignar la vista de la taula
        tlHorari = findViewById(R.id.tlHorari);

        // Mostrar les dades de l'horari
        mostrarHorari();
    }

    private void mostrarHorari() {
        try {
            JSONObject userJson = new JSONObject(userData);
            JSONArray horarisArray = userJson.getJSONArray("horaris");

            if (horarisArray.length() == 0) {
                mostrarMissatge("No es pot mostrar l'horari");
                return;
            }

            // Iterar sobre les files de la taula
            for (int i = 1; i < tlHorari.getChildCount(); i++) {
                TableRow row = (TableRow) tlHorari.getChildAt(i);
                TextView timeTextView = (TextView) row.getChildAt(0);
                String time = timeTextView.getText().toString();

                // Iterar sobre les dades de l'horari
                for (int j = 0; j < horarisArray.length(); j++) {
                    JSONObject horari = horarisArray.getJSONObject(j);
                    String dia = horari.getString("dia");
                    String horaInicio = horari.getString("hora_inicio").substring(0, 5); // Format HH:MM

                    // Comparar el dia i l'hora d'inici
                    if (time.equals(horaInicio)) {
                        int columnIndex = getColumnIndexForDay(dia);
                        if (columnIndex != -1) {
                            TextView cell = (TextView) row.getChildAt(columnIndex);
                            cell.setText("X");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("HorariActivity", "Error en el format de les dades de l'horari: " + e.getMessage());
            mostrarMissatge("Error en el format de les dades de l'horari");
        }
    }

    private int getColumnIndexForDay(String dia) {
        switch (dia) {
            case "DILLUNS":
                return 1;
            case "DIMARTS":
                return 2;
            case "DIMECRES":
                return 3;
            case "DIJOUS":
                return 4;
            case "DIVENDRES":
                return 5;
            default:
                return -1;
        }
    }

    private void mostrarMissatge(String missatge) {
        Toast.makeText(this, missatge, Toast.LENGTH_SHORT).show();
    }
}