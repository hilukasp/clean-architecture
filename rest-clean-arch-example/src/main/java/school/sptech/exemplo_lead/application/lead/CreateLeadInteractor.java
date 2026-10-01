package school.sptech.exemplo_lead.application.lead;

import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

public class CreateLeadInteractor implements CreateLeadUseCase {

    private final LeadGateway gateway;

    public CreateLeadInteractor(LeadGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Response execute(Command command) {
        Lead lead = Lead.create(command.name(), command.email());

        Lead saved = gateway.save(lead);

        return new Response(saved.getId(), saved.getName(), saved.getEmail());
    }
}
