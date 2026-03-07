package ma.smartsupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFournisseurResponse {
    private double chiffreAffairesTotal;
    private int commandesEnAttente;
    private int commandesValidees;
    private int totalProduitsActifs;
}