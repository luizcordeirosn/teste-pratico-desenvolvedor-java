package com.teste.pratico.agenda.services.implementations;

import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.BeanPropertyBindingResult;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.mappers.SolicitanteMapper;
import com.teste.pratico.agenda.repositories.SolicitanteRepository;
import com.teste.pratico.agenda.services.SolicitanteService;
import com.teste.pratico.agenda.validators.SalvarSolicitanteDtoValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitanteServiceImpl implements SolicitanteService{

    private static final Logger logger = LoggerFactory.getLogger(SolicitanteServiceImpl.class);
    
    private final SolicitanteRepository repository;
    private final SalvarSolicitanteDtoValidator salvarSolicitanteDtoValidator;

    @Override
    public Solicitante salvar(SalvarSolicitanteDto dto) {
        logger.info("salvar: {}", dto);

        Errors errors = new BeanPropertyBindingResult(dto, "salvarSolicitanteDto");
        salvarSolicitanteDtoValidator.validate(dto, errors);

        if (errors.hasErrors()) {
            String mensagens = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(mensagens);
        }

        Solicitante solicitante = SolicitanteMapper.dtoSalvarParaEntidade(dto);
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
    public Page<Solicitante> obterTodos(PaginacaoDataDto dto) {
        logger.info("obterTodos: {}", dto);
        
        Pageable pageable = PageRequest.of(dto.pagina(), dto.tamanho(), Sort.by(Sort.Order.asc("id")));

        return repository.findAll(pageable);
    }
}
