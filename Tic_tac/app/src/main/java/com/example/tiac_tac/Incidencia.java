package com.example.tiac_tac;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Incidencia extends AppCompatActivity {

    private String userData;
    private String horarisData;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> incidenciesList;
    private ArrayList<String> tipusIncidenciaList;
    private ArrayList<Integer> tipusIncidenciaIdList;
    private ArrayList<String> horesList;
    private ArrayList<Integer> horesIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia);

        // Obtenir les dades de l'usuari de l'intent
        Intent intent = getIntent();
        userData = intent.getStringExtra("user_data");
        horarisData = intent.getStringExtra("horaris_data");

        // Configura la barra de navegació inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_incidencia); // Selecciona l'element d'incidència
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent navIntent;
            if (id == R.id.nav_inici) {
                // Redirigeix a l'activitat principal
                navIntent = new Intent(Incidencia.this, MainActivity.class);
            } else if (id == R.id.nav_horari) {
                // Redirigeix a l'activitat d'horari
                navIntent = new Intent(Incidencia.this, HorariActivity.class);
            } else if (id == R.id.nav_profile) {
                // Redirigeix a l'activitat de perfil
                navIntent = new Intent(Incidencia.this, Perfil.class);
            } else if (id == R.id.nav_incidencia) {
                // Redirigeix a l'activitat d'incidència
                navIntent = new Intent(Incidencia.this, Incidencia.class);
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

        // Configura el botó per afegir una nova incidència
        Button btnAfegirIncidencia = findViewById(R.id.btnAfegirIncidencia);
        btnAfegirIncidencia.setOnClickListener(v -> mostrarDiàlegAfegirIncidencia());

        // Configura l'historial d'incidències
        ListView lvIncidencies = findViewById(R.id.lvIncidencies);
        incidenciesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, incidenciesList);
        lvIncidencies.setAdapter(adapter);

        // Obtenir les incidències del servidor
        obtenirIncidencies();
    }

    private void mostrarDiàlegAfegirIncidencia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_afegir_incidencia, null);
        builder.setView(dialogView);

        Spinner spinnerTipusIncidencia = dialogView.findViewById(R.id.spinnerTipusIncidencia);
        EditText etDescripcio = dialogView.findViewById(R.id.etDescripcio);
        EditText etData = dialogView.findViewById(R.id.etData);
        Spinner spinnerHores = dialogView.findViewById(R.id.spinnerHores);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);

        // Configura el spinner de tipus d'incidència
        tipusIncidenciaList = new ArrayList<>();
        tipusIncidenciaIdList = new ArrayList<>();
        ArrayAdapter<String> tipusIncidenciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipusIncidenciaList);
        tipusIncidenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipusIncidencia.setAdapter(tipusIncidenciaAdapter);
        obtenirTipusIncidencia(tipusIncidenciaAdapter);

        // Configura el camp de data
        etData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                etData.setText(selectedDate);
                obtenirHoresDisponibles(selectedDate, spinnerHores);
            }, year, month, day);
            datePickerDialog.show();
        });

        AlertDialog dialog = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String descripcio = etDescripcio.getText().toString();
            String data = etData.getText().toString();
            int tipusIncidenciaIndex = spinnerTipusIncidencia.getSelectedItemPosition();
            int tipusIncidenciaId = tipusIncidenciaIdList.get(tipusIncidenciaIndex);
            int horaIndex = spinnerHores.getSelectedItemPosition();
            int horaId = horesIdList.get(horaIndex);

            if (descripcio.isEmpty() || data.isEmpty() || tipusIncidenciaId == -1 || horaId == -1) {
                Toast.makeText(this, "Tots els camps són obligatoris", Toast.LENGTH_SHORT).show();
            } else {
                guardarIncidencia(descripcio, data, tipusIncidenciaId, horaId);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void obtenirTipusIncidencia(ArrayAdapter<String> tipusIncidenciaAdapter) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Ip.TIPUS_INCIDENCIA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Incidencia", "Resposta del servidor: " + response);
                        try {
                            // Eliminar el text addicional abans del JSON
                            String jsonResponseString = response.substring(response.indexOf("["));

                            JSONArray jsonArray = new JSONArray(jsonResponseString);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int idTipusIncidencia = jsonObject.getInt("id_tipus_incidencia");
                                String nomTipusIncidencia = jsonObject.getString("nom_tipus_incidencia");
                                tipusIncidenciaList.add(nomTipusIncidencia);
                                tipusIncidenciaIdList.add(idTipusIncidencia);
                            }
                            tipusIncidenciaAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e("Incidencia", "Error en el format de les dades dels tipus d'incidència: " + e.getMessage());
                            Toast.makeText(Incidencia.this, "Error en el format de les dades dels tipus d'incidència", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Incidencia.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void obtenirHoresDisponibles(String data, Spinner spinnerHores) {
        try {
            JSONObject userJson = new JSONObject(userData);
            int usuariId = userJson.getInt("id_usuari");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.HORES_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Incidencia", "Resposta del servidor: " + response);
                            try {
                                // Eliminar el text addicional abans del JSON
                                String jsonResponseString = response.substring(response.indexOf("["));

                                JSONArray jsonArray = new JSONArray(jsonResponseString);
                                horesList = new ArrayList<>();
                                horesIdList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int idHorari = jsonObject.getInt("id_horario");
                                    String horaInicio = jsonObject.getString("hora_inicio");
                                    String horaFin = jsonObject.getString("hora_fin");
                                    horesList.add(horaInicio + " - " + horaFin);
                                    horesIdList.add(idHorari);
                                }
                                ArrayAdapter<String> horesAdapter = new ArrayAdapter<>(Incidencia.this, android.R.layout.simple_spinner_item, horesList);
                                horesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerHores.setAdapter(horesAdapter);
                            } catch (Exception e) {
                                Log.e("Incidencia", "Error en el format de les dades de les hores: " + e.getMessage());
                                Toast.makeText(Incidencia.this, "Error en el format de les dades de les hores", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Incidencia.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usuari_id", String.valueOf(usuariId));
                    params.put("data", data);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Incidencia", "Error en obtenir les dades de l'usuari: " + e.getMessage());
            Toast.makeText(this, "Error en obtenir les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarIncidencia(String descripcio, String data, int tipusIncidenciaId, int horaId) {
        try {
            JSONObject userJson = new JSONObject(userData);
            int usuariId = userJson.getInt("id_usuari");
    
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.INCIDENCIA_AFEGIR_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Incidencia", "Resposta del servidor: " + response);
                            try {
                                // Eliminar la part no JSON de la resposta
                                String jsonResponseString = response.substring(response.indexOf("{"));
                                JSONObject jsonResponse = new JSONObject(jsonResponseString);
                                String status = jsonResponse.getString("status");
                                if (status.equals("success")) {
                                    // Reiniciar l'activitat per actualitzar la llista d'incidències
                                    Intent intent = new Intent(Incidencia.this, Incidencia.class);
                                    intent.putExtra("user_data", userData);
                                    intent.putExtra("horaris_data", horarisData);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String message = jsonResponse.getString("message");
                                    Toast.makeText(Incidencia.this, "Error en afegir la incidència: " + message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e("Incidencia", "Error en processar la resposta del servidor: " + e.getMessage());
                                Toast.makeText(Incidencia.this, "Error en processar la resposta del servidor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Incidencia.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usuari_id", String.valueOf(usuariId));
                    params.put("descripcio", descripcio);
                    params.put("data_incidencia", data);
                    params.put("id_tipus_incidencia", String.valueOf(tipusIncidenciaId));
                    params.put("id_horari", String.valueOf(horaId));
                    return params;
                }
            };
    
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Incidencia", "Error en obtenir les dades de l'usuari: " + e.getMessage());
            Toast.makeText(this, "Error en obtenir les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenirIncidencies() {
        try {
            JSONObject userJson = new JSONObject(userData);
            int usuariId = userJson.getInt("id_usuari");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Ip.INCIDENCIA_MOSTRAR_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Incidencia", "Resposta del servidor: " + response);
                            try {
                                // Eliminar el text addicional abans del JSON
                                String jsonResponseString = response.substring(response.indexOf("["));

                                JSONArray jsonArray = new JSONArray(jsonResponseString);
                                incidenciesList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String data = jsonObject.getString("data_incidencia");
                                    String descripcio = jsonObject.getString("descripcio");
                                    String tipusIncidencia = jsonObject.getString("nom_tipus_incidencia");
                                    String dia = jsonObject.getString("dia");
                                    String horaInicio = jsonObject.getString("hora_inicio");
                                    String horaFin = jsonObject.getString("hora_fin");
                                    incidenciesList.add(data + ": " + descripcio + " (" + tipusIncidencia + ") - " + dia + " " + horaInicio + " - " + horaFin);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e("Incidencia", "Error en el format de les dades de les incidències: " + e.getMessage());
                                Toast.makeText(Incidencia.this, "Error en el format de les dades de les incidències", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Incidencia.this, "Error en la connexió", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("usuari_id", String.valueOf(usuariId));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Incidencia", "Error en obtenir les dades de l'usuari: " + e.getMessage());
            Toast.makeText(this, "Error en obtenir les dades de l'usuari", Toast.LENGTH_SHORT).show();
        }
    }
}