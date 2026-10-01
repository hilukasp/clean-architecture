package school.sptech.exemplo_lead.domain.lead;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.domain.exception.InvalidParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeadNameTest {

    @Test
    void buildsWithTrimmedValue() {
        LeadName name = LeadName.build("  Ana Silva  ");

        assertEquals("Ana Silva", name.getValue());
    }

    @Test
    void rejectsNull() {
        assertThrows(InvalidParamException.class, () -> LeadName.build(null));
    }

    @Test
    void rejectsBlank() {
        assertThrows(InvalidParamException.class, () -> LeadName.build("   "));
    }

    @Test
    void rejectsNameLongerThan120Characters() {
        String tooLong = "a".repeat(121);

        assertThrows(InvalidParamException.class, () -> LeadName.build(tooLong));
    }

    @Test
    void acceptsNameWithExactly120Characters() {
        String exactly120 = "a".repeat(120);

        LeadName name = LeadName.build(exactly120);

        assertEquals(exactly120, name.getValue());
    }
}
