package ma.smartsupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdminResponse {
    private long totalClients;
    private long totalFournisseurs;
    private long totalProduitsPlateforme;
    private long totalCommandesPassees;
    private double chiffreAffairesGlobal;
}