package com.teste.pratico.agenda.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;

public interface EstacionamentoService {
    
    public Estacionamento salvar(SalvarEstacionamentoDto dto);

    public Estacionamento atualizar(Integer id, AtualizarEstacionamentoDto dto);

    public Boolean deletar(Integer id);

    public Estacionamento obterPorId(Integer id);

    public Optional<Estacionamento> obterPorNumero(Double numero);

    public Page<Estacionamento> obterTodos(EstacionamentoFiltroDto dto);
}
