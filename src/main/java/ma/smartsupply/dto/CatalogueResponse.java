package ma.smartsupply.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueResponse {
    private long totalProduits;
    private List<ProduitResponse> produits;
}