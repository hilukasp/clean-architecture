package school.sptech.exemplo_lead.application.lead;

import school.sptech.exemplo_lead.domain.exception.LeadNotFoundException;
import school.sptech.exemplo_lead.domain.lead.Lead;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

public class GetLeadInteractor implements GetLeadUseCase {

    private final LeadGateway gateway;

    public GetLeadInteractor(LeadGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Response execute(Command command) {
        Lead lead = gateway.findById(command.id())
                .orElseThrow(() -> new LeadNotFoundException(
                        "Lead não encontrado: " + command.id()));

        return new Response(lead.getId(), lead.getName(), lead.getEmail());
    }
}
