package com.teste.pratico.agenda.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.repositories.SolicitanteRepository;

@Component
public class SalvarSolicitanteDtoValidator implements Validator {

    private final SolicitanteRepository repository;

    public SalvarSolicitanteDtoValidator(SolicitanteRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SalvarSolicitanteDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SalvarSolicitanteDto dto = (SalvarSolicitanteDto) target;

        String cpf = dto.cpf();

        if (cpf == null || cpf.length() != 11) {
            errors.rejectValue("cpf", "cpf.invalido", "CPF inválido.");
        } else if (repository.findByCpf(cpf).isPresent()) {
            errors.rejectValue("cpf", "cpf.duplicado", "CPF já cadastrado na base de dados.");
        }
    }
}
