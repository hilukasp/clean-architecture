package school.sptech.exemplo_lead.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadJpaRepository extends JpaRepository<LeadEntity, Integer> {
}
