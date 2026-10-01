package school.sptech.leads.service;

import org.springframework.stereotype.Service;
import school.sptech.leads.entity.Lead;
import school.sptech.leads.exception.EntidadeConflitoException;
import school.sptech.leads.exception.EntidadeNaoEncontradoException;
import school.sptech.leads.repository.LeadRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LeadService {
    private final LeadRepository repository;

    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }

    public List<Lead> listar(){
        return repository.findAll();
    }

    public Lead buscarPorId(Long id){
        Optional<Lead> leadOptional= repository.findById(id);

        if (leadOptional.isEmpty()){
            throw new EntidadeNaoEncontradoException("entidade não existe");
        }
        return leadOptional.get();
    }

    public Lead cadastrarGame(Lead leadParaCadastrar){
        if (repository.existsByNomeAndEmpresa(leadParaCadastrar.getNome(), leadParaCadastrar.getEmpresa())){
            throw new EntidadeConflitoException("Duplicidade");
        }
        Lead leadRegistrado = repository.save(leadParaCadastrar);
        return leadRegistrado;
    }

    public Lead atualizarLead(Lead leadParaAtualizar, Long id){
        if (!repository.existsById(id)){
            throw new EntidadeNaoEncontradoException("não existe");
        }
        if (repository.existsByNomeAndEmpresaAndIdNot(leadParaAtualizar.getNome(),leadParaAtualizar.getEmpresa(),leadParaAtualizar.getId())){
            throw new EntidadeConflitoException("");
        }
        leadParaAtualizar.setId(id);
        Lead leadAtualizado=repository.save(leadParaAtualizar);
        return leadAtualizado;
    }

    public void deletarLead(Long id){
        if (!repository.existsById(id)){
            throw new EntidadeNaoEncontradoException("");
        }
        repository.deleteById(id);
    }
}
