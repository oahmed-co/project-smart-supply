package ma.smartsupply.service;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.dto.RegisterRequest;
import ma.smartsupply.model.Utilisateur;
import ma.smartsupply.enums.Role;
import ma.smartsupply.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final JournalService journalService;

    public List<Utilisateur> listerTousLesAdmins() {
        return utilisateurRepository.findByRole(Role.ADMIN);
    }

    public void supprimerAdmin(Long idAdminASupprimer, String emailSuperAdmin) {
        Utilisateur admin = utilisateurRepository.findById(idAdminASupprimer)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Erreur : Cet utilisateur n'est pas un Administrateur.");
        }

        utilisateurRepository.delete(admin);

        journalService.enregistrerAction(
                emailSuperAdmin,
                "A supprimé définitivement l'administrateur : " + admin.getEmail()
        );
    }

    private final PasswordEncoder passwordEncoder;

    public Utilisateur creerNouvelAdmin(RegisterRequest request, String emailSuperAdmin) {

        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        Utilisateur nouvelAdmin = new Utilisateur();
        nouvelAdmin.setNom(request.getNom());
        nouvelAdmin.setEmail(request.getEmail());
        nouvelAdmin.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        nouvelAdmin.setRole(Role.ADMIN);
        nouvelAdmin.setActif(true);
        nouvelAdmin.setTelephone(request.getTelephone());

        Utilisateur adminSauvegarde = utilisateurRepository.save(nouvelAdmin);

        journalService.enregistrerAction(
                emailSuperAdmin,
                "A créé un nouvel administrateur : " + adminSauvegarde.getEmail()
        );

        return adminSauvegarde;
    }
}