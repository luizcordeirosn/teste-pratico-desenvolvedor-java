package com.fsbr.teste.pratico.TestePraticoSpringBoot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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
import org.springframework.validation.Errors;
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.repositories.EstacionamentoRepository;
import com.teste.pratico.agenda.services.implementations.EstacionamentoServiceImpl;
import com.teste.pratico.agenda.validators.SalvarEstacionamentoDtoValidator;

@ExtendWith(MockitoExtension.class)
public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private SalvarEstacionamentoDtoValidator salvarEstacionamentoDtoValidator;

    @InjectMocks
    private EstacionamentoServiceImpl estacionamentoService;

    private Estacionamento estacionamento;

    private final Integer id = 1;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento(1.0, "comum", 15.0);
    }

    @Test
    void deveSalvarComSucesso() {

        SalvarEstacionamentoDto dto = new SalvarEstacionamentoDto(1.0, "comum", 15.0);

        doAnswer(invocation -> null).when(salvarEstacionamentoDtoValidator).validate(eq(dto), any(Errors.class));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        Estacionamento estacionamentoSalvo = estacionamentoService.salvar(dto);

        assertNotNull(estacionamentoSalvo);

        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
        verify(salvarEstacionamentoDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveLancarExceptionAoSalvarQuandoNumeroJaEstiverRegistrado() {

        SalvarEstacionamentoDto dto = new SalvarEstacionamentoDto(1.0, "comum", 15.0);

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("numero", "estacionamento.numero.existente", "Número de vaga já registrada.");
            return null;
        }).when(salvarEstacionamentoDtoValidator).validate(eq(dto), any(Errors.class));

        assertThrows(IllegalArgumentException.class, () -> estacionamentoService.salvar(dto));

        verify(estacionamentoRepository, times(0)).save(any(Estacionamento.class));
        verify(salvarEstacionamentoDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveAtualizarComSucesso() {

        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(StatusVagaEnum.OCUPADA);

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.of(estacionamento));
        when(estacionamentoRepository.save(any(Estacionamento.class))).thenReturn(estacionamento);

        Estacionamento estacionamentoAtualizado = estacionamentoService.atualizar(id, dto);

        assertNotNull(estacionamentoAtualizado);
        assertEquals(StatusVagaEnum.OCUPADA, estacionamentoAtualizado.getStatus());

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
    }

    @Test
    void atualizarDeveLancarExceptionQuandoNaoEncontrarEstacionamento() {

        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(StatusVagaEnum.OCUPADA);

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> estacionamentoService.atualizar(id, dto));

        assertEquals("Estacionamento não encontrado para o ID fornecido.", exception.getMessage());

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(0)).save(any(Estacionamento.class));
    }

    @Test
    void deveDeletarComSucesso() {

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.of(estacionamento));
        doNothing().when(estacionamentoRepository).delete(any(Estacionamento.class));

        Boolean resultado = estacionamentoService.deletar(id);

        assertTrue(resultado);

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(1)).delete(any(Estacionamento.class));
    }

    @Test
    void deveLancarExceptionQuandoEstacionamentoNaoEncontrado() {

        when(estacionamentoRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> estacionamentoService.deletar(id));

        assertEquals("Estacionamento não encontrado para o ID fornecido.", exception.getMessage());

        verify(estacionamentoRepository, times(1)).findById(id);
        verify(estacionamentoRepository, times(0)).delete(any(Estacionamento.class));
    }

    @Test
    void deveRetornarEstacionamentosPaginadosAoObterTodos() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
        Page<Estacionamento> page = new PageImpl<>(List.of(estacionamento));
        EstacionamentoFiltroDto dto = new EstacionamentoFiltroDto("comum", StatusVagaEnum.DISPONIVEL,
                new PaginacaoDataDto(0, 10));

        when(estacionamentoRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<Estacionamento> estacionamentos = estacionamentoService.obterTodos(dto);

        assertNotNull(estacionamentos);
        assertEquals(1, estacionamentos.getTotalElements());

        verify(estacionamentoRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
