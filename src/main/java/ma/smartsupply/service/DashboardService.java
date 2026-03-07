package ma.smartsupply.service;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.dto.CommandeResponse;
import ma.smartsupply.dto.DashboardAdminResponse;
import ma.smartsupply.dto.DashboardFournisseurResponse;
import ma.smartsupply.dto.ProduitResponse;
import ma.smartsupply.repository.UtilisateurRepository;
import ma.smartsupply.repository.ProduitRepository;
import ma.smartsupply.repository.CommandeRepository;
import ma.smartsupply.model.Commande;
import ma.smartsupply.enums.StatutCommande;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CommandeService commandeService;
    private final ProduitService produitService;

    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    public DashboardFournisseurResponse getStatistiquesFournisseur(String emailFournisseur) {

        List<CommandeResponse> mesVentes = commandeService.getMesVentes(emailFournisseur);
        List<ProduitResponse> mesProduits = produitService.getMesProduits(emailFournisseur);

        double chiffreAffaires = 0;
        int enAttente = 0;
        int validees = 0;

        for (CommandeResponse commande : mesVentes) {
            String statut = commande.getStatut().toString();

            if (statut.equals("EN_ATTENTE_VALIDATION") || statut.equals("EN_ATTENTE")) {
                enAttente++;
            } else if (statut.equals("VALIDEE") || statut.equals("EXPEDIEE") || statut.equals("LIVREE")) {
                validees++;
                chiffreAffaires += commande.getMontantTotal();
            }
        }

        return new DashboardFournisseurResponse(
                chiffreAffaires,
                enAttente,
                validees,
                mesProduits.size()
        );
    }

    public DashboardAdminResponse getStatistiquesGlobalesAdmin() {

        long totalUsers = utilisateurRepository.count();
        long totalProduits = produitRepository.count();

        List<Commande> toutesLesCommandes = commandeRepository.findAll();
        long totalCommandes = toutesLesCommandes.size();

        double caGlobal = toutesLesCommandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.LIVREE || c.getStatut() == StatutCommande.VALIDEE)
                .mapToDouble(Commande::getMontantTotal)
                .sum();

        return new DashboardAdminResponse(
                totalUsers,
                totalUsers,
                totalProduits,
                totalCommandes,
                caGlobal
        );

    }


}