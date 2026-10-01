package school.sptech.exemplo_lead.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record LeadRequestDto(
        @Schema(example = "Ana Silva") String name,
        @Schema(example = "ana.silva@example.com") String email
) {
}
