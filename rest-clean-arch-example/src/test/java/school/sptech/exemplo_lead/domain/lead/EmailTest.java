package school.sptech.exemplo_lead.domain.lead;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.domain.exception.InvalidParamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void buildsWithValidValue() {
        Email email = Email.build("ana@example.com");

        assertEquals("ana@example.com", email.getValue());
    }

    @Test
    void rejectsNull() {
        assertThrows(InvalidParamException.class, () -> Email.build(null));
    }

    @Test
    void rejectsValueWithoutAtSign() {
        assertThrows(InvalidParamException.class, () -> Email.build("ana.example.com"));
    }
}
