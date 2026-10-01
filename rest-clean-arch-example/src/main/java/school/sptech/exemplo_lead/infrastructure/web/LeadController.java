package school.sptech.exemplo_lead.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.exemplo_lead.application.lead.CreateLeadUseCase;
import school.sptech.exemplo_lead.application.lead.GetLeadUseCase;

@RestController
@RequestMapping("/leads")
@Tag(name = "Leads", description = "Captura e consulta de leads")
public class LeadController {

    private final CreateLeadUseCase createLeadUseCase;
    private final GetLeadUseCase getLeadUseCase;

    public LeadController(CreateLeadUseCase createLeadUseCase, GetLeadUseCase getLeadUseCase) {
        this.createLeadUseCase = createLeadUseCase;
        this.getLeadUseCase = getLeadUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo lead")
    public ResponseEntity<LeadResponseDto> post(@RequestBody LeadRequestDto dto) {
        CreateLeadUseCase.Response response = createLeadUseCase.execute(
                new CreateLeadUseCase.Command(dto.name(), dto.email()));

        LeadResponseDto leadResponseDto = new LeadResponseDto(
                response.id(),
                response.name(),
                response.email()
        );

        return ResponseEntity.status(201).body(leadResponseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um lead pelo id")
    public ResponseEntity<LeadResponseDto> findById(@PathVariable int id) {
        GetLeadUseCase.Response response = getLeadUseCase.execute(new GetLeadUseCase.Command(id));

        LeadResponseDto leadResponseDto = new LeadResponseDto(
                response.id(),
                response.name(),
                response.email()
        );

        return ResponseEntity.ok(leadResponseDto);
    }
}
