package school.sptech.exemplo_lead.testsupport;

import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeLeadGateway implements LeadGateway {

    private final Map<Integer, Lead> store = new HashMap<>();
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
