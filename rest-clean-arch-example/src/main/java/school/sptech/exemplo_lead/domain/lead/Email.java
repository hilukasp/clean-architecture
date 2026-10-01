package school.sptech.exemplo_lead.domain.lead;

import school.sptech.exemplo_lead.domain.exception.InvalidParamException;

public class Email {
    private String value;

    private Email(String value) {
        this.value = value;
    }

    // Factory = Metodo para construir uma instância...
    public static Email build(String email) {
        if (email == null || !email.contains("@")) {
            throw new InvalidParamException("Email inválido");
        }

        return new Email(email);
    }

    public String getValue() {
        return value;
    }
}
