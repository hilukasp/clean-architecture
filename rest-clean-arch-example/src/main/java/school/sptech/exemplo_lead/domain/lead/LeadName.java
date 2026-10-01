package school.sptech.exemplo_lead.domain.lead;

import school.sptech.exemplo_lead.domain.exception.InvalidParamException;

public class LeadName {
    private static final int MAX_LENGTH = 120;

    private final String value;

    private LeadName(String value) {
        this.value = value;
    }

    public static LeadName build(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidParamException("Nome inválido");
        }

        String trimmed = name.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new InvalidParamException("Nome inválido: máximo de " + MAX_LENGTH + " caracteres");
        }

        return new LeadName(trimmed);
    }

    public String getValue() {
        return value;
    }
}
