package school.sptech.exemplo_lead.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.sptech.exemplo_lead.application.lead.CreateLeadInteractor;
import school.sptech.exemplo_lead.application.lead.CreateLeadUseCase;
import school.sptech.exemplo_lead.application.lead.GetLeadInteractor;
import school.sptech.exemplo_lead.application.lead.GetLeadUseCase;
import school.sptech.exemplo_lead.domain.lead.LeadGateway;

@Configuration
public class LeadUseCaseConfig {

    @Bean
    public CreateLeadUseCase createLeadUseCase(LeadGateway gateway) {
        return new CreateLeadInteractor(gateway);
    }

    @Bean
    public GetLeadUseCase getLeadUseCase(LeadGateway gateway) {
        return new GetLeadInteractor(gateway);
    }
}
