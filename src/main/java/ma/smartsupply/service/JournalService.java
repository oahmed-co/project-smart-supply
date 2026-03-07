package ma.smartsupply.service;

import lombok.RequiredArgsConstructor;
import ma.smartsupply.model.JournalAction;
import ma.smartsupply.repository.JournalActionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalActionRepository journalActionRepository;

    public void enregistrerAction(String emailActeur, String detail) {
        JournalAction log = new JournalAction(null, emailActeur, detail, LocalDateTime.now());
        journalActionRepository.save(log);
    }

    public List<JournalAction> getHistoriqueComplet() {
        return journalActionRepository.findAllByOrderByDateActionDesc();
    }
}