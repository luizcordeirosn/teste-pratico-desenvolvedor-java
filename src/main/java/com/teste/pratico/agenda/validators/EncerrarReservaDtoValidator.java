package com.teste.pratico.agenda.validators;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.utils.DataUtil;

@Component
public class EncerrarReservaDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EncerrarReservaDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EncerrarReservaDto dto = (EncerrarReservaDto) target;

        if (dto.dataFim() != null) {
            LocalDate dataFim = DataUtil.stringParaLocalDate(dto.dataFim());
            LocalDate hoje = LocalDate.now();

            if (dataFim.isBefore(hoje)) {
                errors.rejectValue("dataFim", "reserva.passada",
                    "A data de encerramento não pode ser anterior à data atual.");
            }
        }
    }
}
