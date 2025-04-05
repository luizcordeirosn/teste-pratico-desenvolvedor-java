package com.teste.pratico.agenda.services.implementations;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.mappers.SolicitanteMapper;
import com.teste.pratico.agenda.repositories.SolicitanteRepository;
import com.teste.pratico.agenda.services.SolicitanteService;

@Service
public class SolicitanteServiceImpl implements SolicitanteService{

    private static final Logger logger = LoggerFactory.getLogger(SolicitanteServiceImpl.class);

    @Autowired
    private SolicitanteRepository repository;

    @Override
    public Solicitante salvar(SalvarSolicitanteDto dto) {
        logger.info("salvar: {}", dto);

        Solicitante solicitante = SolicitanteMapper.dtoSalvarParaEntidade(dto);

        Optional<Solicitante> optionalSolicitantePorCpf = obterPorCpf(solicitante.getCpf());

        if(solicitante.getCpf().length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if(optionalSolicitantePorCpf.isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado na base de dados.");
        }

        return repository.save(solicitante);
    }

    @Override
    public Boolean deletar(Integer id) {
        logger.info("deletar: {}", id);

        Solicitante solicitante = obterPorId(id);

        repository.delete(solicitante);
        
        return true;
    }

    @Override
    public Solicitante obterPorId(Integer id) {
        logger.info("obterPorId: {}", id);

        return repository.findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Solicitante não encontrado para o ID fornecido."));
    }

    @Override
    public Optional<Solicitante> obterPorCpf(String cpf) {
        logger.info("obterPorCpf: {}", cpf);
        
        return repository.findByCpf(cpf);
    }

    @Override
    public Page<Solicitante> obterTodos(Integer pagina, Integer tamanho) {
        logger.info("obterTodos(pagina: {}, tamanho: {})", pagina, tamanho);
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Order.asc("id")));

        return repository.findAll(pageable);
    }
}
