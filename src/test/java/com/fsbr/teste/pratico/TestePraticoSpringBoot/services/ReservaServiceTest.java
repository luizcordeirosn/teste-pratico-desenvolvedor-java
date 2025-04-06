package com.fsbr.teste.pratico.TestePraticoSpringBoot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.Errors;
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.repositories.ReservaRepository;
import com.teste.pratico.agenda.services.implementations.EstacionamentoServiceImpl;
import com.teste.pratico.agenda.services.implementations.ReservaServiceImpl;
import com.teste.pratico.agenda.services.implementations.SolicitanteServiceImpl;
import com.teste.pratico.agenda.validators.EncerrarReservaDtoValidator;
import com.teste.pratico.agenda.validators.SalvarReservaDtoValidator;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SolicitanteServiceImpl solicitanteService;

    @Mock
    private EstacionamentoServiceImpl estacionamentoService;

    @Mock
    private SalvarReservaDtoValidator salvarReservaDtoValidator;

    @Mock
    private EncerrarReservaDtoValidator encerrarReservaDtoValidator;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Estacionamento estacionamento;
    private Solicitante solicitante;
    private Reserva reserva;

    private final Integer id = 1;
    private final Integer estacionamentoId = 2;
    private final Integer solicitanteId = 3;

    @BeforeEach
    void setUp() {

        estacionamento = new Estacionamento(1.0, "comum", 15.0);
        solicitante = new Solicitante("Luiz", "12345678900");
        reserva = new Reserva(estacionamento, solicitante, new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void deveSalvarComSucesso() {

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().toString());

        doAnswer(invocation -> null).when(salvarReservaDtoValidator).validate(eq(dto), any(Errors.class));
        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva reservaSalva = reservaService.salvar(dto);

        assertNotNull(reservaSalva);

        verify(salvarReservaDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoSolicitanteJaPossuiReservaAtiva() {

        estacionamento.setStatus(StatusVagaEnum.RESERVADA);

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().toString());

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.reject("reserva.multiplas",
                    "Não é permitido que o mesmo solicitante tenha mais de uma vaga reservada ou ocupada simultaneamente.");
            return null;
        }).when(salvarReservaDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.salvar(dto));
                
        assertEquals(
                "Não é permitido que o mesmo solicitante tenha mais de uma vaga reservada ou ocupada simultaneamente.",
                exception.getMessage());

        verify(salvarReservaDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveLancarExceptionQuandoDataInicioForDepoisDeHoje() {

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().plusDays(1).toString());

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("dataInicio", "reserva.futura",
                    "Não é possível agendar para datas posteriores ao dia atual.");
            return null;
        }).when(salvarReservaDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.salvar(dto));

        assertEquals("Não é possível agendar para datas posteriores ao dia atual.", exception.getMessage());

        verify(salvarReservaDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveLancarExceptionQuandoDataInicioForAntesDeHoje() {

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().minusDays(1).toString());

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("dataInicio", "reserva.passada",
                    "A data de início não pode ser anterior à data atual.");
            return null;
        }).when(salvarReservaDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.salvar(dto));

        assertEquals("A data de início não pode ser anterior à data atual.", exception.getMessage());

        verify(salvarReservaDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveLancarExceptionAoSalvarReservaQuandoVagaNaoEstiverDisponivel() {

        estacionamento.setId(estacionamentoId);
        estacionamento.setStatus(StatusVagaEnum.OCUPADA);

        SalvarReservaDto dto = new SalvarReservaDto(estacionamentoId, solicitanteId, LocalDate.now().toString());

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.reject("vaga.indisponivel", "A vaga de estacionamento está reservada ou ocupada.");
            return null;
        }).when(salvarReservaDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.salvar(dto));

        assertEquals("A vaga de estacionamento está reservada ou ocupada.", exception.getMessage());

        verify(salvarReservaDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveEncerarReservaComSucesso() {
        estacionamento.setId(1);

        EncerrarReservaDto dto = new EncerrarReservaDto(LocalDate.now().toString());

        doAnswer(invocation -> null).when(encerrarReservaDtoValidator).validate(eq(dto), any(Errors.class));
        when(reservaRepository.findById(id)).thenReturn(Optional.of(reserva));
        when(estacionamentoService.atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL)))
                .thenReturn(estacionamento);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        Reserva reservaEncerrada = reservaService.encerrarReserva(id, dto);

        assertNotNull(reservaEncerrada);

        verify(reservaRepository, times(1)).findById(id);
        verify(estacionamentoService, times(1))
                .atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL));
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoReservaJaEstiverEncerrada() {

        estacionamento.setId(1);
        reserva.setDataFim(new Timestamp(System.currentTimeMillis()));

        EncerrarReservaDto dto = new EncerrarReservaDto(LocalDate.now().toString());

        doAnswer(invocation -> null).when(encerrarReservaDtoValidator).validate(eq(dto), any(Errors.class));
        when(reservaRepository.findById(id)).thenReturn(Optional.of(reserva));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.encerrarReserva(id, dto));

        assertEquals("Esta reserva já foi encerrada anteriormente.", exception.getMessage());

        verify(reservaRepository, times(1)).findById(id);
        verify(estacionamentoService, times(0))
                .atualizar(eq(estacionamento.getId()), any(AtualizarEstacionamentoDto.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoDataFimForAntesDeHoje() {

        estacionamento.setId(1);

        LocalDate dataInvalida = LocalDate.now().minusDays(1);
        EncerrarReservaDto dto = new EncerrarReservaDto(dataInvalida.toString());

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("dataFim", "reserva.passada",
                    "A data de encerramento não pode ser anterior à data atual.");
            return null;
        }).when(encerrarReservaDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.encerrarReserva(id, dto));

        assertEquals("A data de encerramento não pode ser anterior à data atual.", exception.getMessage());

        verify(estacionamentoService, times(0))
                .atualizar(eq(estacionamento.getId()), any(AtualizarEstacionamentoDto.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveRetornarReservasPaginadosAoObterTodos() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("dataFim")));
        Page<Reserva> page = new PageImpl<>(List.of(reserva));
        ReservaFiltroDto dto = new ReservaFiltroDto(1, new PaginacaoDataDto(0, 10));

        when(reservaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Reserva> resultado = reservaService.obterTodos(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        verify(reservaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
