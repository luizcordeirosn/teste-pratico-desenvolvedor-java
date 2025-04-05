package com.teste.pratico.agenda.services.implementations;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.mappers.EstacionamentoMapper;
import com.teste.pratico.agenda.repositories.EstacionamentoRepository;
import com.teste.pratico.agenda.services.EstacionamentoService;
import com.teste.pratico.agenda.specifications.EstacionamentoSpecification;

@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    private static final Logger logger = LoggerFactory.getLogger(EstacionamentoServiceImpl.class);

    @Autowired
    private EstacionamentoRepository repository;

    @Override
    public Estacionamento salvar(SalvarEstacionamentoDto dto) {
        logger.info("salvar: {}", dto);

        Optional<Estacionamento> optionalEstacionamentoPorNumero = obterPorNumero(dto.numero());

        if (optionalEstacionamentoPorNumero.isPresent()) {
            throw new IllegalArgumentException("Número de vaga já registrada.");
        }

        Estacionamento estacionamento = EstacionamentoMapper.dtoSalvarParaEntidade(dto);

        return repository.save(estacionamento);
    }

    @Override
    public Estacionamento atualiazr(Integer id, AtualizarEstacionamentoDto dto) {
        logger.info("atualiazr(id: {}, dto: {})", id, dto);

        Estacionamento estacionamento = obterPorId(id);

        estacionamento.setStatus(dto.status());

        return repository.save(estacionamento);
    }

    @Override
    public Boolean deletar(Integer id) {
        logger.info("deletar: {}", id);

        Estacionamento estacionamento = obterPorId(id);

        repository.delete(estacionamento);

        return true;
    }

    @Override
    public Estacionamento obterPorId(Integer id) {
        logger.info("obterPorId: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado para o ID fornecido."));
    }

    @Override
    public Optional<Estacionamento> obterPorNumero(Double numero) {
        logger.info("obterPorNumero: {}", numero);

        return repository.findByNumero(numero);
    }

    @Override
    public Page<Estacionamento> obterTodos(EstacionamentoFiltroDto dto) {
        logger.info("obterTodos: {}", dto);

        Specification<Estacionamento> specification = EstacionamentoSpecification.filtrarEstacionamento(dto);
        Pageable pageable = PageRequest.of(dto.pagina(), dto.tamanho(), Sort.by(Sort.Order.asc("id")));

        return repository.findAll(specification, pageable);
    }
}
