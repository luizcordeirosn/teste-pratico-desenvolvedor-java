package com.teste.pratico.agenda.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.teste.pratico.agenda.entities.Estacionamento;

@Repository
public interface EstacionamentoRepository
        extends JpaRepository<Estacionamento, Integer>, JpaSpecificationExecutor<Estacionamento> {

    public Optional<Estacionamento> findByNumero(Double numero);
}
