package ma.smartsupply.contoroller;

import ma.smartsupply.dto.CommandeRequest;
import ma.smartsupply.enums.StatutCommande;
import ma.smartsupply.model.Commande;
import ma.smartsupply.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Commande> passerCommande(@RequestBody CommandeRequest request) {
        return ResponseEntity.ok(commandeService.passerCommande(request));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAuthority('FOURNISSEUR')")
    public ResponseEntity<Commande> validerCommande(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(commandeService.validerCommande(id, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Commande>> getMesCommandes(Principal principal) {
        return ResponseEntity.ok(commandeService.getMesCommandes(principal.getName()));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAuthority('FOURNISSEUR')")
    public ResponseEntity<Commande> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutCommande statut) {

        Commande commandeMaj = commandeService.changerStatutCommande(id, statut);
        return ResponseEntity.ok(commandeMaj);
    }
}