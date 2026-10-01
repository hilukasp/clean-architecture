package school.sptech.exemplo_lead.infrastructure.persistence.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

import java.util.Optional;

@Component
@Profile("!memory")
public class LeadJpaAdapter implements LeadGateway {

    private final LeadJpaRepository leadJpaRepository;

    public LeadJpaAdapter(LeadJpaRepository leadJpaRepository) {
        this.leadJpaRepository = leadJpaRepository;
    }

    @Override
    public Lead save(Lead lead) {
        LeadEntity leadEntity =
                new LeadEntity(null, lead.getName(), lead.getEmail());

        LeadEntity saved = leadJpaRepository.save(leadEntity);

        return Lead.reconstitute(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Override
    public Optional<Lead> findById(int id) {
        return leadJpaRepository.findById(id)
                .map(leadEntity -> Lead.reconstitute(
                        leadEntity.getId(),
                        leadEntity.getName(),
                        leadEntity.getEmail()
                ));
    }
}
