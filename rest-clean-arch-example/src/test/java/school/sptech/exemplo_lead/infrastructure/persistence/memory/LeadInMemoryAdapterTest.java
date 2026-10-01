package school.sptech.exemplo_lead.infrastructure.persistence.memory;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.domain.lead.Lead;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadInMemoryAdapterTest {

    private final LeadInMemoryAdapter adapter = new LeadInMemoryAdapter();

    @Test
    void savingAssignsIncrementingIds() {
        Lead first = adapter.save(Lead.create("Ana Silva", "ana@example.com"));
        Lead second = adapter.save(Lead.create("Bruno Costa", "bruno@example.com"));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void findByIdReturnsSavedLead() {
        Lead saved = adapter.save(Lead.create("Ana Silva", "ana@example.com"));

        Optional<Lead> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Ana Silva", found.get().getName());
        assertEquals("ana@example.com", found.get().getEmail());
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        Optional<Lead> found = adapter.findById(999);

        assertTrue(found.isEmpty());
    }
}
