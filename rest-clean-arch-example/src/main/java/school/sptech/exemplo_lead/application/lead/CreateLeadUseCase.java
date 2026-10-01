package school.sptech.exemplo_lead.application.lead;

public interface CreateLeadUseCase {

    Response execute(Command command);

    record Command(String name, String email) {
    }

    record Response(int id, String name, String email) {
    }
}
