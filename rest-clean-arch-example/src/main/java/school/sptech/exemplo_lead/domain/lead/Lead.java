package school.sptech.exemplo_lead.domain.lead;

public class Lead {
    private final Integer id;
    private final LeadName name;
    private final Email email;

    private Lead(Integer id, LeadName name, Email email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static Lead create(
            String name,
            String email
    ){
        return new Lead(null, LeadName.build(name), Email.build(email));
    }

    public static Lead reconstitute(
            Integer id,
            String name,
            String email
    ){
        return new Lead(id, LeadName.build(name), Email.build(email));
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name.getValue(); // Lei de Demeter
    }

    public String getEmail() {
        return email.getValue(); // Lei de Demeter
    }
}
