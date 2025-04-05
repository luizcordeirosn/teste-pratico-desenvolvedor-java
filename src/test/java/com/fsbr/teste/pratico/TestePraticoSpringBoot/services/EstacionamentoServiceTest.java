package com.fsbr.teste.pratico.TestePraticoSpringBoot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.repositories.EstacionamentoRepository;
import com.teste.pratico.agenda.services.implementations.EstacionamentoServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EstacionamentoServiceTest {
    
    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @InjectMocks
    private EstacionamentoServiceImpl estacionamentoService;

    private Estacionamento estacionamento;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento(1.0, "comum", 15.0);
    }

    @Test
    void deveSalvarComSucesso() {
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        SalvarEstacionamentoDto dto = new SalvarEstacionamentoDto(1.0, "comum", 15.0);

        Estacionamento estacionamentoSalvo = estacionamentoService.salvar(dto);

        assertNotNull(estacionamentoSalvo);
        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
    }

    @Test
    void deveLancarExceptionAoSalvarQuandoNumeroJaEstiverRegistrado() {
        Double numero = 1.0;

        when(estacionamentoRepository.findByNumero(numero)).thenReturn(Optional.of(estacionamento));

        SalvarEstacionamentoDto dto = new SalvarEstacionamentoDto(1.0, "comum", 15.0);

        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.salvar(dto));

        verify(estacionamentoRepository, times(0)).save(any(Estacionamento.class));
    }

    @Test
    void deveAtualizarComSucesso() {
        Integer id = 1;

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.of(estacionamento));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(StatusVagaEnum.OCUPADA);

        Estacionamento estacionamentoAtualizado = estacionamentoService.atualiazr(id, dto);

        assertNotNull(estacionamentoAtualizado);
        assertEquals(StatusVagaEnum.OCUPADA, estacionamentoAtualizado.getStatus());

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
    }

    @Test
    void atualizarDeveLancarExceptionQuandoNaoEncontrarEstacionamento() {
        Integer id = 1;

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.empty());

        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(StatusVagaEnum.OCUPADA);

        assertThrows(ResourceNotFoundException.class, () -> estacionamentoService.atualiazr(id, dto));

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(0)).save(any(Estacionamento.class));
    }

    @Test
    void deveDeletarComSucesso(){
        Integer id = 1;

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.of(estacionamento));
        doNothing().when(estacionamentoRepository).delete(any(Estacionamento.class));

        Boolean resultado = estacionamentoService.deletar(id);

        assertTrue(resultado);

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(1)).delete(any(Estacionamento.class));
    }

    @Test
    void deveLancarExceptionQuandoEstacionamentoNaoEncontrado() {
        Integer id = 1;

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estacionamentoService.deletar(id));

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(0)).delete(any(Estacionamento.class));
    }

    @Test
    void deveRetornarEstacionamentosPaginadosAoObterTodos(){

        EstacionamentoFiltroDto dto = new EstacionamentoFiltroDto("comum", StatusVagaEnum.DISPONIVEL, 0, 10);
        Pageable pageable = PageRequest.of(dto.pagina(), dto.tamanho(), Sort.by(Sort.Order.asc("id")));
        Page<Estacionamento> page = new PageImpl<>(List.of(estacionamento));

        when(estacionamentoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Estacionamento> estacionamentos = estacionamentoService.obterTodos(dto);

        assertNotNull(estacionamentos);
        assertEquals(1, estacionamentos.getTotalElements());
        verify(estacionamentoRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
