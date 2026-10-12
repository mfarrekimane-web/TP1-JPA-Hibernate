package com.example;

import com.example.model.Produit;
import com.example.model.Categorie;
import org.h2.tools.Server;

import javax.persistence.*;
import javax.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.List;

public class App {

    public static void main(String[] args) {

        // Démarrage de la console H2
        try {
            Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 : http://localhost:8082");
        } catch (Exception e) {
            System.out.println("Erreur console H2");
            e.printStackTrace();
        }

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hibernate-demo");

        try {

            // 1. Insertion des produits et catégories
            insererProduits(emf);

            // 2. Afficher les produits
            lireProduits(emf);

            // 3. Modifier le prix du produit ID = 1
            modifierPrix(emf, 1L, new BigDecimal("899.99"));

            // 4. Supprimer le produit ID = 5
            supprimerProduit(emf, 5L);

            // 5. Recherche par plage de prix
            rechercherParPrix(
                    emf,
                    new BigDecimal("100"),
                    new BigDecimal("500")
            );

            // 6. Afficher les produits après modifications
            lireProduits(emf);

        } finally {
            emf.close();
        }
    }

    // ========================================
    // 1. INSÉRER PRODUITS ET CATÉGORIES
    // ========================================

    private static void insererProduits(EntityManagerFactory emf) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Création des catégories
            Categorie informatique = new Categorie("Informatique");
            Categorie accessoires = new Categorie("Accessoires");

            em.persist(informatique);
            em.persist(accessoires);

            // Création des produits
            Produit p1 = new Produit("Laptop", new BigDecimal("999.99"));
            Produit p2 = new Produit("Smartphone", new BigDecimal("499.99"));
            Produit p3 = new Produit("Tablette", new BigDecimal("299.99"));
            Produit p4 = new Produit("Clavier", new BigDecimal("49.99"));
            Produit p5 = new Produit("Souris", new BigDecimal("19.99"));
            Produit p6 = new Produit("Écran 24 pouces", new BigDecimal("179.99"));
            Produit p7 = new Produit("Casque audio", new BigDecimal("89.99"));

            // Association produit - catégorie
            p1.setCategorie(informatique);
            p2.setCategorie(informatique);
            p3.setCategorie(informatique);
            p6.setCategorie(informatique);

            p4.setCategorie(accessoires);
            p5.setCategorie(accessoires);
            p7.setCategorie(accessoires);

            // Enregistrement des produits
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.persist(p5);
            em.persist(p6);
            em.persist(p7);

            em.getTransaction().commit();

            System.out.println("Produits et catégories insérés !");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    // ========================================
    // 2. LIRE LES PRODUITS
    // ========================================

    private static void lireProduits(EntityManagerFactory emf) {

        EntityManager em = emf.createEntityManager();

        try {

            List<Produit> produits = em.createQuery(
                    "SELECT p FROM Produit p",
                    Produit.class
            ).getResultList();

            System.out.println("\n===== LISTE DES PRODUITS =====");

            for (Produit produit : produits) {
                System.out.println(produit);
            }

            System.out.println("\nRecherche du produit ID = 5");

            Produit produit = em.find(Produit.class, 5L);

            if (produit != null) {
                System.out.println(produit);
            } else {
                System.out.println("Produit non trouvé");
            }

        } finally {
            em.close();
        }
    }

    // ========================================
    // 3. MODIFIER LE PRIX
    // ========================================

    private static void modifierPrix(
            EntityManagerFactory emf,
            Long id,
            BigDecimal nouveauPrix) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, id);

            if (produit != null) {

                produit.setPrix(nouveauPrix);

                System.out.println(
                        "\nPrix du produit " + id +
                                " modifié à " + nouveauPrix
                );

            } else {
                System.out.println("Produit introuvable");
            }

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    // ========================================
    // 4. SUPPRIMER UN PRODUIT
    // ========================================

    private static void supprimerProduit(
            EntityManagerFactory emf,
            Long id) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, id);

            if (produit != null) {

                em.remove(produit);

                System.out.println(
                        "\nProduit ID = " + id + " supprimé !"
                );

            } else {
                System.out.println("Produit introuvable");
            }

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    // ========================================
    // 5. RECHERCHE PAR PLAGE DE PRIX
    // CRITERIA API
    // ========================================

    private static void rechercherParPrix(
            EntityManagerFactory emf,
            BigDecimal prixMin,
            BigDecimal prixMax) {

        EntityManager em = emf.createEntityManager();

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Produit> cq =
                    cb.createQuery(Produit.class);

            Root<Produit> produit =
                    cq.from(Produit.class);

            cq.select(produit)
                    .where(
                            cb.between(
                                    produit.get("prix"),
                                    prixMin,
                                    prixMax
                            )
                    );

            List<Produit> resultats =
                    em.createQuery(cq).getResultList();

            System.out.println(
                    "\n===== PRODUITS ENTRE " +
                            prixMin + " ET " + prixMax + " ====="
            );

            for (Produit p : resultats) {
                System.out.println(p);
            }

        } finally {
            em.close();
        }
    }
}