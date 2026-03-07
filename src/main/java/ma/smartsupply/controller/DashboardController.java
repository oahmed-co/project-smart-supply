package ma.smartsupply.controller;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.dto.DashboardAdminResponse;
import ma.smartsupply.dto.DashboardFournisseurResponse;
import ma.smartsupply.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/fournisseur")
    @PreAuthorize("hasAuthority('FOURNISSEUR')")
    public ResponseEntity<DashboardFournisseurResponse> getDashboardFournisseur(Principal principal) {
        DashboardFournisseurResponse stats = dashboardService.getStatistiquesFournisseur(principal.getName());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DashboardAdminResponse> getDashboardAdmin() {
        DashboardAdminResponse statsGlobales = dashboardService.getStatistiquesGlobalesAdmin();
        return ResponseEntity.ok(statsGlobales);
    }
}