package com.teste.pratico.agenda.services.implementations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
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

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceImpl.class);

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private SolicitanteService solicitanteService;

    @Override
    public Reserva salvar(SalvarReservaDto dto) {
        logger.info("salvar: {}", dto);

        Estacionamento estacionamento = estacionamentoService.obterPorId(dto.estacionamentoId());
        Solicitante solicitante = solicitanteService.obterPorId(dto.solicitanteId());

        Page<Reserva> reservas = obterTodos(new ReservaFiltroDto(solicitante.getId(), 0, 1));

        if (reservas.getTotalElements() >= 1
                && !reservas.getContent().get(0).getEstacionamento().getStatus().equals(StatusVagaEnum.DISPONIVEL)) {
            throw new IllegalArgumentException(
                    "Não é permitido que o mesmo solicitante tenha mais de uma vaga reservada ou ocupada simultaneamente.");
        }

        if (dto.dataInicio() != null) {
            LocalDate dataInicio = DataUtil.stringParaLocalDate(dto.dataInicio());
            LocalDate hoje = LocalDate.now();

            if (dataInicio.isAfter(hoje)) {
                throw new IllegalArgumentException("Não é possível agendar para datas posteriores ao dia atual.");
            }

            if (dataInicio.isBefore(hoje)) {
                throw new IllegalArgumentException("A data de início não pode ser anterior à data atual.");
            }
        }

        if (estacionamento.getStatus().equals(StatusVagaEnum.RESERVADA)
                || estacionamento.getStatus().equals(StatusVagaEnum.OCUPADA)) {

            throw new IllegalArgumentException("A vaga de estacionamento está reservada ou ocupada.");
        }

        Reserva reserva = ReservaMapper.dtoSalvarParaEntidade(estacionamento, solicitante, dto);

        reserva = repository.save(reserva);

        estacionamentoService.atualizar(estacionamento.getId(), new AtualizarEstacionamentoDto(
                dto.dataInicio() == null ? StatusVagaEnum.OCUPADA : StatusVagaEnum.RESERVADA));

        return reserva;
    }

    @Override
    public Reserva encerrarReserva(Integer id, EncerrarReservaDto dto) {
        logger.info("encerrarReserva(id: {}, dto: {})", id, dto);

        Reserva reserva = obterPorId(id);
        LocalDate dataFim;

        if (reserva.getDataFim() != null) {
            throw new IllegalArgumentException("Esta reserva já foi encerrada anteriormente.");
        }

        if (dto.dataFim() != null) {

            dataFim = DataUtil.stringParaLocalDate(dto.dataFim());
            LocalDate hoje = LocalDate.now();

            if (dataFim.isBefore(hoje)) {
                throw new IllegalArgumentException("A data de início não pode ser anterior à data atual.");
            }

        } else {
            dataFim = LocalDate.now();
        }

        Integer diasReservados = DataUtil.calcularDiasEntre(DataUtil.timestampParaLocalDate(reserva.getDataInicio()),
                dataFim);

        reserva.setDataFim(DataUtil.stringParaTimestamp(dto.dataFim()));
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
        Pageable pageable = PageRequest.of(dto.pagina(), dto.tamanho(), Sort.by(Sort.Order.desc("dataFim")));

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
