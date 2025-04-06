package com.teste.pratico.agenda.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.repositories.EstacionamentoRepository;

@Component
public class SalvarEstacionamentoDtoValidator implements Validator {

    private final EstacionamentoRepository repository;

    public SalvarEstacionamentoDtoValidator(EstacionamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SalvarEstacionamentoDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SalvarEstacionamentoDto dto = (SalvarEstacionamentoDto) target;

        if (dto.numero() != null && repository.findByNumero(dto.numero()).isPresent()) {
            errors.rejectValue("numero", "estacionamento.numero.existente", "Número de vaga já registrada.");
        }
    }
}
