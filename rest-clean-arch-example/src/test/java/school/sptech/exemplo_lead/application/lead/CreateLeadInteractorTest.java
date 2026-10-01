package school.sptech.exemplo_lead.application.lead;

import org.junit.jupiter.api.Test;
import school.sptech.exemplo_lead.testsupport.FakeLeadGateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CreateLeadInteractorTest {

    private final FakeLeadGateway gateway = new FakeLeadGateway();
    private final CreateLeadUseCase useCase = new CreateLeadInteractor(gateway);

    @Test
    void createsLeadAndReturnsAssignedId() {
        CreateLeadUseCase.Response response = useCase.execute(
                new CreateLeadUseCase.Command("Ana Silva", "ana@example.com"));

        assertNotEquals(0, response.id());
        assertEquals("Ana Silva", response.name());
        assertEquals("ana@example.com", response.email());
    }

    @Test
    void persistsLeadInGateway() {
        CreateLeadUseCase.Response response = useCase.execute(
                new CreateLeadUseCase.Command("Ana Silva", "ana@example.com"));

        assertEquals("Ana Silva", gateway.findById(response.id()).orElseThrow().getName());
    }
}
