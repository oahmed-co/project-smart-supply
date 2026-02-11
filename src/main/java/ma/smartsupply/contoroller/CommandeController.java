package ma.smartsupply.contoroller;

import ma.smartsupply.dto.CommandeRequest;
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
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Commande> passerCommande(
            @RequestBody CommandeRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(commandeService.passerCommande(request, principal.getName()));
    }


    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('FOURNISSEUR')")
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
}