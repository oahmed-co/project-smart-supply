package ma.smartsupply.controller;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.dto.RegisterRequest;
import ma.smartsupply.model.JournalAction;
import ma.smartsupply.model.Utilisateur;
import ma.smartsupply.service.ConfigurationService;
import ma.smartsupply.service.JournalService;
import ma.smartsupply.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class SuperAdminController {

    private final ConfigurationService configurationService;
    private final JournalService journalService;
    private final SuperAdminService superAdminService;

    @GetMapping("/config/commission")
    public ResponseEntity<Double> getCommission() {
        return ResponseEntity.ok(configurationService.getTauxCommission());
    }

    @PutMapping("/config/commission")
    public ResponseEntity<String> modifierCommission(@RequestParam double nouveauTaux) {
        double tauxMisAJour = configurationService.updateTauxCommission(nouveauTaux);
        return ResponseEntity.ok("La commission de la plateforme a été modifiée à : " + (tauxMisAJour * 100) + "%");
    }

    @GetMapping("/logs")
    public ResponseEntity<List<JournalAction>> voirHistoriqueActions() {
        return ResponseEntity.ok(journalService.getHistoriqueComplet());
    }

    @GetMapping("/admins")
    public ResponseEntity<List<Utilisateur>> getListeAdmins() {
        return ResponseEntity.ok(superAdminService.listerTousLesAdmins());
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<String> supprimerAdministrateur(
            @PathVariable Long id,
            Principal principal
    ) {
        String emailSuperAdmin = principal.getName();

        superAdminService.supprimerAdmin(id, emailSuperAdmin);

        return ResponseEntity.ok("L'administrateur a été supprimé avec succès.");
    }

    @PostMapping("/creer-admin")
    public ResponseEntity<String> creerAdmin(@RequestBody RegisterRequest request, Principal principal) {
        String emailSuperAdmin = principal.getName();

        Utilisateur nouvelAdmin = superAdminService.creerNouvelAdmin(request, emailSuperAdmin);

        return ResponseEntity.ok("Succès : L'administrateur " + nouvelAdmin.getEmail() + " a été créé !");
    }
}