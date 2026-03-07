package ma.smartsupply.repository;

import ma.smartsupply.model.JournalAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalActionRepository extends JpaRepository<JournalAction, Long> {
    List<JournalAction> findAllByOrderByDateActionDesc();
}
