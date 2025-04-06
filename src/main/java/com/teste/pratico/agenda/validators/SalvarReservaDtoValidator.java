package com.teste.pratico.agenda.validators;

import java.time.LocalDate;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.services.EstacionamentoService;
import com.teste.pratico.agenda.services.ReservaService;
import com.teste.pratico.agenda.services.SolicitanteService;
import com.teste.pratico.agenda.utils.DataUtil;

@Component
public class SalvarReservaDtoValidator implements Validator {

    private final EstacionamentoService estacionamentoService;
    private final SolicitanteService solicitanteService;

    @Lazy
    private ReservaService reservaService;

    public SalvarReservaDtoValidator(
            EstacionamentoService estacionamentoService,
            SolicitanteService solicitanteService,
            @Lazy ReservaService reservaService) {
        this.estacionamentoService = estacionamentoService;
        this.solicitanteService = solicitanteService;
        this.reservaService = reservaService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SalvarReservaDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SalvarReservaDto dto = (SalvarReservaDto) target;

        Estacionamento estacionamento = estacionamentoService.obterPorId(dto.estacionamentoId());
        Solicitante solicitante = solicitanteService.obterPorId(dto.solicitanteId());

        Page<Reserva> reservas = reservaService
                .obterTodos(new ReservaFiltroDto(solicitante.getId(), new PaginacaoDataDto(0, 1)));

        if (reservas.getTotalElements() >= 1
                && !reservas.getContent().get(0).getEstacionamento().getStatus().equals(StatusVagaEnum.DISPONIVEL)) {
            errors.reject("reserva.multiplas",
                    "Não é permitido que o mesmo solicitante tenha mais de uma vaga reservada ou ocupada simultaneamente.");
        }

        if (dto.dataInicio() != null) {
            LocalDate dataInicio = DataUtil.stringParaLocalDate(dto.dataInicio());
            LocalDate hoje = LocalDate.now();

            if (dataInicio.isAfter(hoje)) {
                errors.rejectValue("dataInicio", "reserva.futura",
                        "Não é possível agendar para datas posteriores ao dia atual.");
            }

            if (dataInicio.isBefore(hoje)) {
                errors.rejectValue("dataInicio", "reserva.passada",
                        "A data de início não pode ser anterior à data atual.");
            }
        }

        if (estacionamento.getStatus().equals(StatusVagaEnum.RESERVADA)
                || estacionamento.getStatus().equals(StatusVagaEnum.OCUPADA)) {
            errors.reject("vaga.indisponivel", "A vaga de estacionamento está reservada ou ocupada.");
        }
    }
}