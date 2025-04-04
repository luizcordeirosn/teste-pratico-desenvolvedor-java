package com.teste.pratico.agenda.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.teste.pratico.agenda.entities.Solicitante;

@Repository
public interface SolicitanteRepository extends JpaRepository<Solicitante, Integer> {
    
    public Optional<Solicitante> findByCpf(String cpf);
}
