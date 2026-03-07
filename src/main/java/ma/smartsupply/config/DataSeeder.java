package ma.smartsupply.config;

import ma.smartsupply.enums.Role;
import ma.smartsupply.model.Utilisateur;
import ma.smartsupply.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initSuperAdmin(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            String emailBoss = "pfe@smartsupply.ma";

            if (utilisateurRepository.findByEmail(emailBoss).isEmpty()) {

                Utilisateur superAdmin = new Utilisateur();
                superAdmin.setNom("Le Boss");
                superAdmin.setEmail(emailBoss);

                superAdmin.setMotDePasse(passwordEncoder.encode("admin"));

                superAdmin.setRole(Role.SUPER_ADMIN);
                superAdmin.setActif(true);

                superAdmin.setTelephone("0600000000");

                utilisateurRepository.save(superAdmin);
                System.out.println(" COMPTE SUPER ADMIN CRÉÉ AUTOMATIQUEMENT !");
                System.out.println("Email: " + emailBoss);
                System.out.println("Mot de passe: admin");
            } else {
                System.out.println("✅ Le Super Admin existe déjà dans la base de données.");
            }
        };
    }
}