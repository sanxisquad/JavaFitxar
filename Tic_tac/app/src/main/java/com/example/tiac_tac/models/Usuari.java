package com.example.tiac_tac.models;
import java.io.Serializable;

public class Usuari implements Serializable {
    private String nom;
    private String cognoms;
    private String email;
    private String nom_usuari;

    public Usuari(String nom, String cognoms, String email, String nom_usuari) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.nom_usuari = nom_usuari;
    }

    // Getters i setters
    public String getNom() {
        return nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public String getEmail() {
        return email;
    }

    public String getNomUsuari() {
        return nom_usuari;
    }
}
