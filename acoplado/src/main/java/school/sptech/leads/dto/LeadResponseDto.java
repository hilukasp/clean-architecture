package school.sptech.leads.dto;

public class LeadResponseDto {
    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private Integer consumoMedio;

    private String faixa;
    private Integer prioridade;
    private String sugestaoRegra;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getConsumoMedio() {
        return consumoMedio;
    }

    public void setConsumoMedio(Integer consumoMedio) {
        this.consumoMedio = consumoMedio;
    }

    public String getFaixa() {
        return faixa;
    }

    public void setFaixa(String faixa) {
        this.faixa = faixa;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public String getSugestaoRegra() {
        return sugestaoRegra;
    }

    public void setSugestaoRegra(String sugestaoRegra) {
        this.sugestaoRegra = sugestaoRegra;
    }
}
