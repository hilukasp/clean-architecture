package school.sptech.exemplo_lead.domain.lead;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.domain.exception.InvalidParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeadTest {

    @Test
    void createBuildsNewLeadWithoutId() {
        Lead lead = Lead.create("Ana Silva", "ana@example.com");

        assertNull(lead.getId());
        assertEquals("Ana Silva", lead.getName());
        assertEquals("ana@example.com", lead.getEmail());
    }

    @Test
    void reconstituteBuildsLeadWithId() {
        Lead lead = Lead.reconstitute(1, "Ana Silva", "ana@example.com");

        assertEquals(1, lead.getId());
        assertEquals("Ana Silva", lead.getName());
        assertEquals("ana@example.com", lead.getEmail());
    }

    @Test
    void createTrimsName() {
        Lead lead = Lead.create("  Ana Silva  ", "ana@example.com");

        assertEquals("Ana Silva", lead.getName());
    }

    @Test
    void createRejectsBlankName() {
        assertThrows(InvalidParamException.class, () -> Lead.create("   ", "ana@example.com"));
    }

    @Test
    void createRejectsInvalidEmail() {
        assertThrows(InvalidParamException.class, () -> Lead.create("Ana Silva", "invalid-email"));
    }
}
