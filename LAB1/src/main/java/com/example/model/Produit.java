package com.example.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private BigDecimal prix;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    public Produit() {
    }

    public Produit(String nom, BigDecimal prix) {
        this.nom = nom;
        this.prix = prix;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", categorie=" +
                (categorie != null ? categorie.getNom() : "Aucune") +
                '}';
    }
}