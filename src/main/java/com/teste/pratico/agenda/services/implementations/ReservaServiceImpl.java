package com.teste.pratico.agenda.services.implementations;

import java.time.LocalDate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.Errors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.mappers.ReservaMapper;
import com.teste.pratico.agenda.repositories.ReservaRepository;
import com.teste.pratico.agenda.services.EstacionamentoService;
import com.teste.pratico.agenda.services.ReservaService;
import com.teste.pratico.agenda.services.SolicitanteService;
import com.teste.pratico.agenda.specifications.ReservaSpecification;
import com.teste.pratico.agenda.utils.DataUtil;
import com.teste.pratico.agenda.validators.EncerrarReservaDtoValidator;
import com.teste.pratico.agenda.validators.SalvarReservaDtoValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository repository;
    private final EstacionamentoService estacionamentoService;
    private final SolicitanteService solicitanteService;

    private final SalvarReservaDtoValidator salvarReservaDtoValidator;
    private final EncerrarReservaDtoValidator encerrarReservaDtoValidator;

    @Override
    public Reserva salvar(SalvarReservaDto dto) {
        logger.info("salvar: {}", dto);

        Errors errors = new BeanPropertyBindingResult(dto, "salvarReservaDto");
        salvarReservaDtoValidator.validate(dto, errors);

        if (errors.hasErrors()) {
            String mensagens = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(mensagens);
        }

        Estacionamento estacionamento = estacionamentoService.obterPorId(dto.estacionamentoId());
        Solicitante solicitante = solicitanteService.obterPorId(dto.solicitanteId());

        Reserva reserva = ReservaMapper.dtoSalvarParaEntidade(estacionamento, solicitante, dto);

        reserva = repository.save(reserva);

        estacionamentoService.atualizar(estacionamento.getId(),
                new AtualizarEstacionamentoDto(
                        dto.dataInicio() == null ? StatusVagaEnum.OCUPADA : StatusVagaEnum.RESERVADA));

        return reserva;
    }

    @Override
    public Reserva encerrarReserva(Integer id, EncerrarReservaDto dto) {
        logger.info("encerrarReserva(id: {}, dto: {})", id, dto);

        Errors errors = new BeanPropertyBindingResult(dto, "encerrarReservaDto");
        encerrarReservaDtoValidator.validate(dto, errors);

        if (errors.hasErrors()) {
            String mensagens = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(mensagens);
        }

        Reserva reserva = obterPorId(id);

        if (reserva.getDataFim() != null) {
            throw new IllegalArgumentException("Esta reserva já foi encerrada anteriormente.");
        }

        LocalDate dataFim = dto.dataFim() != null
                ? DataUtil.stringParaLocalDate(dto.dataFim())
                : LocalDate.now();

        Integer diasReservados = DataUtil.calcularDiasEntre(
                DataUtil.timestampParaLocalDate(reserva.getDataInicio()),
                dataFim);

        reserva.setDataFim(DataUtil.localDateParaTimestamp(dataFim));
        reserva.setValorTotal(calcularValorTotalReserva(reserva, diasReservados));

        reserva = repository.save(reserva);

        estacionamentoService.atualizar(reserva.getEstacionamento().getId(),
                new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL));

        return reserva;
    }

    @Override
    public Reserva obterPorId(Integer id) {
        logger.info("obterPorId: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada para o ID fornecido."));
    }

    @Override
    public Page<Reserva> obterTodos(ReservaFiltroDto dto) {
        logger.info("obterTodos: {}", dto);

        Specification<Reserva> specification = ReservaSpecification.filtrarReserva(dto);
        Pageable pageable = PageRequest.of(dto.paginacao().pagina(), dto.paginacao().tamanho(),
                Sort.by(Sort.Order.desc("dataFim")));

        return repository.findAll(specification, pageable);
    }

    private Double calcularValorTotalReserva(Reserva reserva, Integer diasReservados) {

        Double valorReserva = reserva.getEstacionamento().getValorPorHora();

        if (reserva.getEstacionamento().getStatus().equals(StatusVagaEnum.RESERVADA)) {
            valorReserva += 5;
        }

        if (diasReservados >= 1) {
            valorReserva += reserva.getEstacionamento().getValorPorHora() * diasReservados;
        }

        return valorReserva;
    }

}
