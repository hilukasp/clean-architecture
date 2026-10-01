package school.sptech.leads.mapper;

import school.sptech.leads.dto.LeadRequestDto;
import school.sptech.leads.dto.LeadResponseDto;
import school.sptech.leads.entity.Lead;

import java.util.List;

public class LeadMapper {

    private LeadMapper() {
    }

    public static Lead toEntity(LeadRequestDto dto) {
        Lead lead = new Lead();
        lead.setNome(dto.getNome());
        lead.setEmail(dto.getEmail());
        lead.setConsumoMedio(dto.getConsumoMedio());
        lead.setTelefone(dto.getTelefone());
        return lead;
    }

    public static LeadResponseDto toResponseDto(Lead lead) {
        LeadResponseDto dto = new LeadResponseDto();
        dto.setId(lead.getId());
        dto.setNome(lead.getNome());
        dto.setEmail(lead.getEmail());
        dto.setConsumoMedio(lead.getConsumoMedio());
        dto.setTelefone(lead.getTelefone());
        return dto;
    }

    public static List<LeadResponseDto> toResponseDto(List<Lead> leads) {
        return leads.stream()
                .map(LeadMapper::toResponseDto)
                .toList();
    }
}
