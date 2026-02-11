package ma.smartsupply.contoroller;

import ma.smartsupply.dto.ProduitRequest;
import ma.smartsupply.dto.ProduitResponse;
import ma.smartsupply.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @PreAuthorize("hasRole('FOURNISSEUR')")
    public ResponseEntity<ProduitResponse> ajouterProduit(
            @RequestBody ProduitRequest request,
            Principal principal // Contient l'email de l'utilisateur connecté via JWT
    ) {
        return ResponseEntity.ok(produitService.ajouterProduit(request, principal.getName()));
    }


    @GetMapping
    public ResponseEntity<List<ProduitResponse>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }


    @GetMapping("/mes-produits")
    @PreAuthorize("hasRole('FOURNISSEUR')")
    public ResponseEntity<List<ProduitResponse>> getMesProduits(Principal principal) {
        return ResponseEntity.ok(produitService.getMesProduits(principal.getName()));
    }


    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('FOURNISSEUR')")
    public ResponseEntity<ProduitResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantite,
            Principal principal
    ) {
        return ResponseEntity.ok(produitService.updateStock(id, quantite, principal.getName()));
    }
}