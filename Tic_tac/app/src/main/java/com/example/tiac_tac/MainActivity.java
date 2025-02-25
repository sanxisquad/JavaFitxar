package com.example.tiac_tac;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tvHoraInici;
    private TextView tvHoraSortida;
    private TextView tvHores;
    private TextView tvEsperantUbicacio;
    private Button btnIniciar;
    private Button btnParar;
    private Button btnInforme;

    private boolean isRunning = false;
    private String userData;
    private String horarisData;
    private Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enllaça les vistes
        TextView tvNomCognoms = findViewById(R.id.tvNomCognoms);
        tvHoraInici = findViewById(R.id.tvHoraInici);
        tvHoraSortida = findViewById(R.id.tvHoraSortida);
        tvHores = findViewById(R.id.tvHores);
        tvEsperantUbicacio = findViewById(R.id.tvEsperantUbicacio);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnParar = findViewById(R.id.btnParar);
        btnInforme = findViewById(R.id.btnInforme);

        // Deshabilita el botó d'iniciar fins que es trobi la ubicació
        btnIniciar.setEnabled(false);
        btnParar.setEnabled(false);
        tvEsperantUbicacio.setVisibility(View.VISIBLE);

        // Obtenir les dades de l'usuari de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");

        // Mostrar les dades de l'usuari (exemple)
        try {
            JSONObject jsonResponse = new JSONObject(userData);
            String nom = jsonResponse.getString("nom");
            String cognoms = jsonResponse.getString("cognoms");
            tvNomCognoms.setText(nom + " " + cognoms);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configura el LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = location;
                Log.d("MainActivity", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                // Habilita el botó d'iniciar quan es trobi la ubicació
                btnIniciar.setEnabled(true);
                tvEsperantUbicacio.setVisibility(View.GONE);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        // Comprova els permisos de localització
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Requesting location permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Log.d("MainActivity", "Location permissions already granted");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Configura els botons
        btnIniciar.setOnClickListener(v -> iniciarCronometre());
        btnParar.setOnClickListener(v -> pararCronometre());
        btnInforme.setOnClickListener(v -> {
            Intent intentInforme = new Intent(MainActivity.this, InformeActivity.class);
            intentInforme.putExtra("user_data", userData);
            intentInforme.putExtra("horaris_data", horarisData);
            intentInforme.putExtra("isRunning", isRunning);
            intentInforme.putExtra("horaInici", tvHoraInici.getText().toString());
            startActivity(intentInforme);
        });

        // Mostrar les hores que li toquen aquell dia
        mostrarHoresDelDia();

        // Configura la barra de navegació inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_inici);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intentMain = new Intent(MainActivity.this, MainActivity.class);
            intentMain.putExtra("user_data", userData);
            intentMain.putExtra("horaris_data", horarisData);
            intentMain.putExtra("isRunning", isRunning);
            intentMain.putExtra("horaInici", tvHoraInici.getText().toString());
            if (id == R.id.nav_inici) {
                startActivity(intentMain);
                return true;
            } else if (id == R.id.nav_horari) {
                Intent intentHorari = new Intent(MainActivity.this, HorariActivity.class);
                intentHorari.putExtras(intentMain.getExtras());
                startActivity(intentHorari);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intentProfile = new Intent(MainActivity.this, Perfil.class);
                intentProfile.putExtras(intentMain.getExtras());
                startActivity(intentProfile);
                return true;
            } else if (id == R.id.nav_incidencia) {
                Intent intentIncidencia = new Intent(MainActivity.this, Incidencia.class);
                intentIncidencia.putExtras(intentMain.getExtras());
                startActivity(intentIncidencia);
                return true;
            } else {
                return false;
            }
        });

        // Restaurar l'estat del botó i la variable isRunning
        if (intent.hasExtra("isRunning")) {
            isRunning = intent.getBooleanExtra("isRunning", false);
            String horaInici = intent.getStringExtra("horaInici");
            tvHoraInici.setText(horaInici);

            if (isRunning) {
                btnIniciar.setVisibility(View.GONE);
                btnParar.setVisibility(View.VISIBLE);
                btnParar.setEnabled(true);
                tvHoraInici.setVisibility(View.VISIBLE);
                tvHoraSortida.setVisibility(View.GONE);
            } else {
                btnIniciar.setVisibility(View.VISIBLE);
                btnParar.setVisibility(View.GONE);
                btnIniciar.setEnabled(true);
                tvHoraSortida.setVisibility(View.VISIBLE);
            }
        }
    }

    private void iniciarCronometre() {
        if (!isRunning) {
            isRunning = true;
            btnIniciar.setVisibility(View.GONE);
            btnParar.setVisibility(View.VISIBLE);
            btnParar.setEnabled(true);
            tvHoraInici.setVisibility(View.VISIBLE);

            // Ocultar l'hora de sortida
            tvHoraSortida.setVisibility(View.GONE);

            // Mostrar l'hora d'inici immediatament
            String horaInici = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            tvHoraInici.setText("Hora d'inici: " + horaInici);

            guardarFitxatge("entrada");
        }
    }

    private void pararCronometre() {
        if (isRunning) {
            isRunning = false;
            btnParar.setVisibility(View.GONE);
            btnIniciar.setVisibility(View.VISIBLE);
            btnIniciar.setEnabled(true);
            tvHoraSortida.setVisibility(View.VISIBLE);

            String horaSortida = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            tvHoraSortida.setText("Hora de sortida: " + horaSortida);

            // Guarda el fitxatge de sortida i després calcula l'horari efectiu
            guardarFitxatge("sortida");

            String horesTreballades = calcularHoresTreballadesAmbHorari();
            Toast.makeText(this, "Hores treballades (segons horari): " + horesTreballades, Toast.LENGTH_LONG).show();
        }
    }

    private void guardarFitxatge(String tipus) {
        try {
            JSONObject userJson = new JSONObject(userData);
            int usuariId = userJson.getInt("id_usuari");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.FITXAR_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MainActivity", "Resposta del servidor: " + response);
                            if (tipus.equals("entrada")) {
                                // L'hora d'inici ja s'ha mostrat, no cal tornar a establir-la
                            } else {
                                verificarHoresTreballades();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MainActivity", "Error en la connexió: " + error.getMessage());
                            Toast.makeText(MainActivity.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usuari_id", String.valueOf(usuariId));
                    params.put("tipus", tipus);
                    try {
                        if (currentLocation != null) {
                            String gps = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                            params.put("gps", gps);
                            Log.d("MainActivity", "GPS data: " + gps);
                        } else {
                            params.put("gps", "No GPS");
                            Log.d("MainActivity", "No GPS data available");
                        }
                        if (tipus.equals("sortida")) {
                            String horesTreballades = calcularHoresTreballadesAmbHorari();
                            params.put("hores_treballades", horesTreballades);
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error obtenint la ubicació GPS: " + e.getMessage());
                        params.put("gps", "Error GPS");
                    }
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("MainActivity", "Error en obtenir les dades de l'usuari: " + e.getMessage());
            Toast.makeText(this, "Error en obtenir les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }

    private String calcularHoresTreballadesAmbHorari() {
        double horesTreballades = 0;
        try {
            // Obtenim les hores reals d'inici i sortida (format "HH:mm:ss")
            String horaIniciText = tvHoraInici.getText().toString().replace("Hora d'inici: ", "");
            String horaSortidaText = tvHoraSortida.getText().toString().replace("Hora de sortida: ", "");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date dataInici = sdf.parse(horaIniciText);
            Date dataSortida = sdf.parse(horaSortidaText);

            // Si no hi ha cap horari programat, no podem calcular la intersecció
            String horesEsperadesText = tvHores.getText().toString();
            if (horesEsperadesText.isEmpty()) {
                Toast.makeText(this, "No hi ha hores esperades per avui", Toast.LENGTH_SHORT).show();
                return "00:00:00";
            }

            // Cada interval esperat està en format "HH:mm - HH:mm"
            String[] intervals = horesEsperadesText.split("\n");

            for (String interval : intervals) {
                String[] parts = interval.split(" - ");
                // Afegim segons per poder comparar amb el format "HH:mm:ss"
                Date intervalInici = sdf.parse(parts[0] + ":00");
                Date intervalFi = sdf.parse(parts[1] + ":00");

                // Calcular l'intersecció:
                // L'inici de l'intersecció és el màxim entre la data d'inici real i l'inici de l'interval
                Date iniciInterseccio = dataInici.after(intervalInici) ? dataInici : intervalInici;
                // La fi de l'intersecció és el mínim entre la data de sortida real i la fi de l'interval
                Date fiInterseccio = dataSortida.before(intervalFi) ? dataSortida : intervalFi;

                // Si hi ha intersecció (la data d'inici de la intersecció és anterior a la fi)
                if (iniciInterseccio.before(fiInterseccio)) {
                    long diffMillis = fiInterseccio.getTime() - iniciInterseccio.getTime();
                    horesTreballades += diffMillis / (1000.0 * 60 * 60); // Convertir a hores
                }
            }

            // Si no hi ha cap intersecció (és a dir, no estaves treballant dins l'horari), horesTreballades quedarà a 0
        } catch (Exception e) {
            Log.e("MainActivity", "Error en calcular les hores treballades amb horari: " + e.getMessage());
            Toast.makeText(this, "Error en calcular les hores treballades", Toast.LENGTH_SHORT).show();
            return "00:00:00";
        }

        // Formateja el resultat a HH:mm:ss
        int hores = (int) horesTreballades;
        int minuts = (int) ((horesTreballades - hores) * 60);
        int segons = (int) ((((horesTreballades - hores) * 60) - minuts) * 60);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hores, minuts, segons);
    }

    private void mostrarHoresDelDia() {
        try {
            JSONArray horarisArray = new JSONArray(horarisData);
            StringBuilder horesDelDia = new StringBuilder();

            // Obtenir el dia de la setmana actual
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String diaSetmana = convertirDiaCatala(dayOfWeek);

            // Iterar sobre les dades de l'horari
            for (int i = 0; i < horarisArray.length(); i++) {
                JSONObject horari = horarisArray.getJSONObject(i);
                String dia = horari.getString("dia");
                String horaInicio = horari.getString("hora_inicio").substring(0, 5); // Format HH:MM
                String horaFin = horari.getString("hora_fin").substring(0, 5); // Format HH:MM

                // Comparar el dia de la setmana
                if (dia.equals(diaSetmana)) {
                    horesDelDia.append(horaInicio).append(" - ").append(horaFin).append("\n");
                }
            }

            tvHores.setText(horesDelDia.toString());
        } catch (Exception e) {
            Log.e("MainActivity", "Error en el format de les dades de l'horari: " + e.getMessage());
            Toast.makeText(this, "Error en el format de les dades de l'horari", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertirDiaCatala(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "DILLUNS";
            case Calendar.TUESDAY:
                return "DIMARTS";
            case Calendar.WEDNESDAY:
                return "DIMECRES";
            case Calendar.THURSDAY:
                return "DIJOUS";
            case Calendar.FRIDAY:
                return "DIVENDRES";
            case Calendar.SATURDAY:
                return "DISSABTE";
            case Calendar.SUNDAY:
                return "DIUMENGE";
            default:
                return "";
        }
    }

        @Override
    protected void onResume() {
        super.onResume();
        // Obtenir les dades de l'usuari de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");
    
        // Mostrar les hores que li toquen aquell dia
        mostrarHoresDelDia();
    
        // Verificar l'estat del fitxatge
        verificarEstatFitxatge();
    }
    private void verificarEstatFitxatge() {
        try {
            JSONObject userJson = new JSONObject(userData);
            int usuariId = userJson.getInt("id_usuari");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.VERIFICAR_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Imprimir la resposta de l'API al logcat
                            Log.d("MainActivity", "Resposta de l'API: " + response);
                            try {
                                // Eliminar la part no JSON de la resposta
                                String jsonResponseString = response.substring(response.indexOf("{"));
                                JSONObject jsonResponse = new JSONObject(jsonResponseString);
                                boolean fitxatgeObert = jsonResponse.getBoolean("fitxatge_obert");
                                String horaInici = jsonResponse.getString("hora_inici");

                                if (fitxatgeObert) {
                                    isRunning = true;
                                    tvHoraInici.setText("Hora d'inici: " + horaInici);
                                    btnIniciar.setVisibility(View.GONE);
                                    btnParar.setVisibility(View.VISIBLE);
                                    btnParar.setEnabled(true);
                                    tvHoraInici.setVisibility(View.VISIBLE);
                                    tvHoraSortida.setVisibility(View.GONE);
                                } else {
                                    isRunning = false;
                                    btnIniciar.setVisibility(View.VISIBLE);
                                    btnParar.setVisibility(View.GONE);
                                    btnIniciar.setEnabled(true);
                                    tvHoraSortida.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                Log.e("MainActivity", "Error en processar la resposta de l'API: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MainActivity", "Error en la connexió: " + error.getMessage());
                            Toast.makeText(MainActivity.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usuari_id", String.valueOf(usuariId));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            Log.e("MainActivity", "Error en obtenir les dades de l'usuari: " + e.getMessage());
            Toast.makeText(this, "Error en obtenir les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Location permissions granted");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                Log.d("MainActivity", "Location permissions denied");
                Toast.makeText(this, "Permís de localització denegat", Toast.LENGTH_SHORT).show();
            }
        }
    }
        private void verificarHoresTreballades() {
        try {
            // Obtenir les hores d'inici i sortida
            String horaInici = tvHoraInici.getText().toString().replace("Hora d'inici: ", "");
            String horaSortida = tvHoraSortida.getText().toString().replace("Hora de sortida: ", "");
    
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date dateInici = sdf.parse(horaInici);
            Date dateSortida = sdf.parse(horaSortida);
    
            // Obtenir les hores esperades des de tvHores
            String horesEsperadesText = tvHores.getText().toString();
            if (horesEsperadesText.isEmpty()) {
                Toast.makeText(this, "No hi ha hores esperades per avui", Toast.LENGTH_SHORT).show();
                return;
            }
    
            String[] horesEsperadesArray = horesEsperadesText.split("\n");
            double horesTreballades = 0;
            boolean estaTreballant = false;
    
            for (String horesEsperades : horesEsperadesArray) {
                String[] hores = horesEsperades.split(" - ");
                Date horaIniciEsperada = sdf.parse(hores[0] + ":00");
                Date horaFiEsperada = sdf.parse(hores[1] + ":00");
    
                // Verificar si l'hora d'inici i l'hora de sortida estan dins dels intervals esperats
                if (dateInici.before(horaFiEsperada) && dateSortida.after(horaIniciEsperada)) {
                    estaTreballant = true;
                    Date horaIniciTreballada = dateInici.after(horaIniciEsperada) ? dateInici : horaIniciEsperada;
                    Date horaFiTreballada = dateSortida.before(horaFiEsperada) ? dateSortida : horaFiEsperada;
                    long diff = horaFiTreballada.getTime() - horaIniciTreballada.getTime();
                    horesTreballades += (double) diff / (1000 * 60 * 60);
                }
            }
    
            // Si no ha treballat dins dels intervals esperats, no sumar res
            if (!estaTreballant) {
                Toast.makeText(this, "No has treballat dins dels intervals esperats", Toast.LENGTH_SHORT).show();
            } else {
                // Mostrar les hores treballades
                String horesTreballadesText = String.format(Locale.getDefault(), "%02d:%02d:%02d", 
                    (int) horesTreballades, 
                    (int) ((horesTreballades * 60) % 60), 
                    (int) ((horesTreballades * 3600) % 60));
                Toast.makeText(this, "Hores treballades: " + horesTreballadesText, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error en verificar les hores treballades: " + e.getMessage());
            Toast.makeText(this, "Error en verificar les hores treballades", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRunning", isRunning);
        outState.putString("horaInici", tvHoraInici.getText().toString());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            isRunning = savedInstanceState.getBoolean("isRunning");
            String horaInici = savedInstanceState.getString("horaInici");
            tvHoraInici.setText(horaInici);
    
            if (isRunning) {
                btnIniciar.setVisibility(View.GONE);
                btnParar.setVisibility(View.VISIBLE);
                btnParar.setEnabled(true);
                tvHoraInici.setVisibility(View.VISIBLE);
                tvHoraSortida.setVisibility(View.GONE);
            } else {
                btnIniciar.setVisibility(View.VISIBLE);
                btnParar.setVisibility(View.GONE);
                btnIniciar.setEnabled(true);
                tvHoraSortida.setVisibility(View.VISIBLE);
            }
        }
    }
}