package school.sptech.leads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.leads.entity.Lead;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    boolean existsByNomeAndEmpresa(String nome, String empresa);
    boolean existsByNomeAndEmpresaAndIdNot(String nome, String empresa, Long id);
}
