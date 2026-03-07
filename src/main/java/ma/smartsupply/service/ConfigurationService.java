package ma.smartsupply.service;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.model.Configuration;
import ma.smartsupply.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    public static final String CLE_COMMISSION = "TAUX_COMMISSION";

    public double getTauxCommission() {
        return configurationRepository.findByCle(CLE_COMMISSION)
                .map(config -> Double.parseDouble(config.getValeur()))
                .orElse(0.05);
    }

    public double updateTauxCommission(double nouveauTaux) {
        Configuration config = configurationRepository.findByCle(CLE_COMMISSION)
                .orElse(new Configuration(null, CLE_COMMISSION, "0.05"));

        config.setValeur(String.valueOf(nouveauTaux));
        configurationRepository.save(config);

        return nouveauTaux;
    }
}
