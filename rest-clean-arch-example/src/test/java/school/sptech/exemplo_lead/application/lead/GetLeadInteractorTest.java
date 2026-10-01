package school.sptech.exemplo_lead.application.lead;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.domain.exception.LeadNotFoundException;
import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.testsupport.FakeLeadGateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetLeadInteractorTest {

    private final FakeLeadGateway gateway = new FakeLeadGateway();
    private final GetLeadUseCase useCase = new GetLeadInteractor(gateway);

    @Test
    void returnsExistingLead() {
        Lead saved = gateway.save(Lead.create("Ana Silva", "ana@example.com"));

        GetLeadUseCase.Response response = useCase.execute(new GetLeadUseCase.Command(saved.getId()));

        assertEquals(saved.getId(), response.id());
        assertEquals("Ana Silva", response.name());
        assertEquals("ana@example.com", response.email());
    }

    @Test
    void throwsWhenLeadDoesNotExist() {
        assertThrows(LeadNotFoundException.class,
                () -> useCase.execute(new GetLeadUseCase.Command(999)));
    }
}
