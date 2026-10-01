package school.sptech.exemplo_lead.application.lead;

public interface GetLeadUseCase {

    Response execute(Command command);

    record Command(int id) {
    }

    record Response(int id, String name, String email) {
    }
}
