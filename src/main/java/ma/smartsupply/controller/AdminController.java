package ma.smartsupply.controller;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.model.Utilisateur;
import ma.smartsupply.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getTousLesUtilisateurs() {
        return ResponseEntity.ok(adminService.getAllUtilisateurs());
    }

    @PutMapping("/utilisateurs/{id}/statut")
    public ResponseEntity<String> changerStatutUtilisateur(
            @PathVariable Long id,
            Principal principal
    ) {
        String emailAdmin = principal.getName();

        Utilisateur userMaj = adminService.basculerStatutUtilisateur(id, emailAdmin);

        String etat = userMaj.isActif() ? "activé " : "désactivé";
        return ResponseEntity.ok("Le compte de l'utilisateur " + userMaj.getEmail() + " a été " + etat);
    }
}