package ma.smartsupply.service;

import ma.smartsupply.dto.CommandeRequest;
import ma.smartsupply.dto.LigneCommandeRequest;
import ma.smartsupply.enums.StatutCommande;
import ma.smartsupply.model.*;
import ma.smartsupply.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final NotificationService notificationService;

    public List<Commande> getMesCommandes(String email) {
        return commandeRepository.findAll();
    }

    @Transactional
    public Commande passerCommande(CommandeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!(utilisateur instanceof Client)) {
            throw new RuntimeException("Seuls les clients peuvent passer des commandes.");
        }
        Client client = (Client) utilisateur;


        Commande commande = new Commande();
        commande.setClient(client);
        commande.setDateCreation(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_ATTENTE_VALIDATION);
        commande.setLignes(new ArrayList<>());

        double montantTotal = 0;


        for (LigneCommandeRequest lcr : request.getLignes()) {
            Produit produit = produitRepository.findById(lcr.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + lcr.getProduitId()));

            Stock stock = stockRepository.findByProduitId(produit.getId())
                    .orElseThrow(() -> new RuntimeException("Le stock n'existe pas pour le produit : " + produit.getNom()));

            int stockDispo = (stock.getQuantiteDisponible() != null) ? stock.getQuantiteDisponible() : 0;

            if (stockDispo < lcr.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour : " + produit.getNom() +
                        " (Dispo: " + stockDispo + ", Demandé: " + lcr.getQuantite() + ")");
            }

            stock.setQuantiteDisponible(stockDispo - lcr.getQuantite());
            stockRepository.save(stock);

            notificationService.creer(
                    produit.getFournisseur(),
                    "Nouvelle commande pour votre produit : " + produit.getNom() + " (Quantité: " + lcr.getQuantite() + ")"
            );

            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(lcr.getQuantite());

            double sousTotal = produit.getPrix() * lcr.getQuantite();
            ligne.setSousTotal(sousTotal);

            commande.getLignes().add(ligne);
            montantTotal += sousTotal;
        }

        commande.setMontantTotal(montantTotal);

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
            Stock stock = stockRepository.findByProduitId(ligne.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Stock introuvable"));

            if (stock.estEnAlerte()) {
                System.out.println("️ ALERTE STOCK : " + ligne.getProduit().getNom());
            }
        }
        commande.setStatut(StatutCommande.VALIDEE);
        Commande commandeMaj = commandeRepository.save(commande);

        notificationService.creer(
                commande.getClient(),
                " Votre commande n°" + commande.getId() + " a été validée."
        );

        return commandeMaj;
    }

    @Transactional
    public Commande changerStatutCommande(Long commandeId, StatutCommande nouveauStatut) {

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'ID : " + commandeId));


        if (nouveauStatut == StatutCommande.ANNULEE && commande.getStatut() != StatutCommande.ANNULEE) {
            for (LigneCommande ligne : commande.getLignes()) {
                Stock stock = stockRepository.findByProduitId(ligne.getProduit().getId())
                        .orElseThrow(() -> new RuntimeException("Stock introuvable"));

                stock.setQuantiteDisponible(stock.getQuantiteDisponible() + ligne.getQuantite());
                stockRepository.save(stock);
            }
        }

        commande.setStatut(nouveauStatut);
        Commande commandeMiseAJour = commandeRepository.save(commande);

        String message = "Mise à jour : Votre commande #" + commande.getId() + " est maintenant " + nouveauStatut.name();
        notificationService.creer(commande.getClient(), message);

        return commandeMiseAJour;
    }
}
