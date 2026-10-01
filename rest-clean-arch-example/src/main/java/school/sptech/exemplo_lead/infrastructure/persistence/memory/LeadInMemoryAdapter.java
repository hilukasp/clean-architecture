package school.sptech.exemplo_lead.infrastructure.persistence.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("memory")
public class LeadInMemoryAdapter implements LeadGateway {

    private final Map<Integer, Lead> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public Lead save(Lead lead) {
        int id = nextId.getAndIncrement();
        Lead saved = Lead.reconstitute(id, lead.getName(), lead.getEmail());
        store.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Lead> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }
}
