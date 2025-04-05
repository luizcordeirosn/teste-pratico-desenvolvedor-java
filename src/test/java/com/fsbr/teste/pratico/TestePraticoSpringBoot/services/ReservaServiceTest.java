package com.fsbr.teste.pratico.TestePraticoSpringBoot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
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
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
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

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SolicitanteServiceImpl solicitanteService;

    @Mock
    private EstacionamentoServiceImpl estacionamentoService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Estacionamento estacionamento;
    private Solicitante solicitante;
    private Reserva reserva;

    @BeforeEach
    void setUp() {

        estacionamento = new Estacionamento(1.0, "comum", 15.0);
        solicitante = new Solicitante("Luiz", "12345678900");
        reserva = new Reserva(estacionamento, solicitante, new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void deveSalvarComSucesso() {

        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().toString());

        Reserva reservaSalva = reservaService.salvar(dto);

        assertNotNull(reservaSalva);

        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoSolicitanteJaPossuiReservaAtiva() {
        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(reserva)));

        estacionamento.setStatus(StatusVagaEnum.RESERVADA);

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.salvar(dto));
        assertEquals("Não é permitido que o mesmo solicitante tenha mais de uma vaga reservada ou ocupada simultaneamente.", exception.getMessage());

        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoDataInicioForDepoisDeHoje() {
        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().plusDays(1).toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.salvar(dto));
        assertEquals("Não é possível agendar para datas posteriores ao dia atual.", exception.getMessage());

        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoDataInicioForAntesDeHoje() {
        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().minusDays(1).toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.salvar(dto));
        assertEquals("A data de início não pode ser anterior à data atual.", exception.getMessage());

        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionAoSalvarReservaQuandoVagaNaoEstiverDisponivel() {
        when(estacionamentoService.obterPorId(2)).thenReturn(estacionamento);
        when(solicitanteService.obterPorId(3)).thenReturn(solicitante);
        when(reservaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        estacionamento.setStatus(StatusVagaEnum.OCUPADA);

        SalvarReservaDto dto = new SalvarReservaDto(2, 3, LocalDate.now().toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.salvar(dto));
        assertEquals("A vaga de estacionamento está reservada ou ocupada.", exception.getMessage());

        verify(estacionamentoService, times(1)).obterPorId(2);
        verify(solicitanteService, times(1)).obterPorId(3);
        verify(reservaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveEncerarReservaComSucesso() {
        estacionamento.setId(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(estacionamentoService.atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL)))
                .thenReturn(estacionamento);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        EncerrarReservaDto dto = new EncerrarReservaDto(LocalDate.now().toString());

        Reserva reservaEncerrada = reservaService.encerrarReserva(1, dto);

        assertNotNull(reservaEncerrada);

        verify(reservaRepository, times(1)).findById(1);
        verify(estacionamentoService, times(1)).atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL));
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoReservaJaEstiverEncerrada() {
        estacionamento.setId(1);
        reserva.setDataFim(new Timestamp(System.currentTimeMillis()));

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        EncerrarReservaDto dto = new EncerrarReservaDto(LocalDate.now().toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.encerrarReserva(1, dto));
        assertEquals("Esta reserva já foi encerrada anteriormente.", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1);
        verify(estacionamentoService, times(0)).atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveLancarExceptionQuandoDataFimForAntesDeHoje() {
        estacionamento.setId(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        EncerrarReservaDto dto = new EncerrarReservaDto(LocalDate.now().minusDays(1).toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservaService.encerrarReserva(1, dto));
        assertEquals("A data de início não pode ser anterior à data atual.", exception.getMessage());

        verify(reservaRepository, times(1)).findById(1);
        verify(estacionamentoService, times(0)).atualizar(1, new AtualizarEstacionamentoDto(StatusVagaEnum.DISPONIVEL));
        verify(reservaRepository, times(0)).save(any(Reserva.class));
    }

    @Test
    void deveRetornarReservasPaginadosAoObterTodos() {
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("dataFim")));
        Page<Solicitante> page = new PageImpl<>(List.of(solicitante));

        when(reservaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        ReservaFiltroDto dto = new ReservaFiltroDto(1, 0, 10);

        Page<Reserva> reservas = reservaService.obterTodos(dto);

        assertNotNull(reservas);
        assertEquals(1, reservas.getTotalElements());

        verify(reservaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
