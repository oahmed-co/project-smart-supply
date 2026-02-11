package ma.smartsupply.service;

import ma.smartsupply.dto.CommandeRequest;
import ma.smartsupply.dto.LigneCommandeRequest;
import ma.smartsupply.enums.StatutCommande;
import ma.smartsupply.model.*;
import ma.smartsupply.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final StockRepository stockRepository;

    @Transactional
    public Commande passerCommande(CommandeRequest request, String emailClient) {


        Utilisateur user = utilisateurRepository.findByEmail(emailClient)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));


        if (!(user instanceof Client)) {
            throw new RuntimeException("Erreur : L'utilisateur connecté n'est pas un Client.");
        }
        Client client = (Client) user;


        Commande commande = Commande.builder()
                .client(client)
                .dateCreation(LocalDateTime.now())
                .statut(StatutCommande.EN_ATTENTE_VALIDATION)
                .montantTotal(0.0)
                .lignes(new ArrayList<>())
                .build();

        double total = 0.0;


        for (LigneCommandeRequest ligneReq : request.getLignes()) {
            Produit produit = produitRepository.findById(ligneReq.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + ligneReq.getProduitId()));

            Stock stock = produit.getStock();

            if (stock == null) {
                throw new RuntimeException("Le produit " + produit.getNom() + " n'a pas de stock associé !");
            }

            if (stock.getQuantiteDisponible() < ligneReq.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour le produit : " + produit.getNom());
            }

            LigneCommande ligne = LigneCommande.builder()
                    .produit(produit)
                    .commande(commande)
                    .quantite(ligneReq.getQuantite())
                    .sousTotal(produit.getPrix() * ligneReq.getQuantite())
                    .build();

            commande.getLignes().add(ligne);
            total += ligne.getSousTotal();
        }

        commande.setMontantTotal(total);
        return commandeRepository.save(commande);
    }

    @Transactional
    public Commande validerCommande(Long commandeId, String emailFournisseur) {


        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));


        if (commande.getStatut() != StatutCommande.EN_ATTENTE_VALIDATION) {
            throw new RuntimeException("Cette commande ne peut plus être validée");
        }




        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            Stock stock = produit.getStock();


            if (stock == null) {
                throw new RuntimeException("Erreur critique : Stock introuvable pour " + produit.getNom());
            }

            int nouvelleQuantite = stock.getQuantiteDisponible() - ligne.getQuantite();

            if (nouvelleQuantite < 0) {
                throw new RuntimeException("Stock insuffisant lors de la validation pour : " + produit.getNom());
            }


            stock.setQuantiteDisponible(nouvelleQuantite);
            stock.setDateDerniereMiseAJour(LocalDateTime.now());
            stockRepository.save(stock);


            if (stock.estEnAlerte()) {
                System.out.println("⚠️ ALERTE STOCK : " + produit.getNom());
            }
        }


        commande.setStatut(StatutCommande.VALIDEE);
        return commandeRepository.save(commande);
    }

    public List<Commande> getMesCommandes(String email) {
        return commandeRepository.findAll();
    }
}
