package school.sptech.exemplo_lead.domain.lead;


import java.util.Optional;

public interface LeadGateway {
    Lead save(Lead lead);

    Optional<Lead> findById(int id);
}
