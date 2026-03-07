package ma.smartsupply.service;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.model.Utilisateur;
import ma.smartsupply.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;

    private final JournalService journalService;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur basculerStatutUtilisateur(Long id, String emailAdminConnecte) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + id));

        utilisateur.setActif(!utilisateur.isActif());
        Utilisateur userMaj = utilisateurRepository.save(utilisateur);

        String actionEtat = userMaj.isActif() ? "débloqué" : "bloqué";
        String detailAction = "A " + actionEtat + " le compte de l'utilisateur : " + userMaj.getEmail();

        journalService.enregistrerAction(emailAdminConnecte, detailAction);

        return userMaj;
    }
}
