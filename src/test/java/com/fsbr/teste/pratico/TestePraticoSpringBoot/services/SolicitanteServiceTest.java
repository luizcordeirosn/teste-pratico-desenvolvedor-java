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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.Errors;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.repositories.SolicitanteRepository;
import com.teste.pratico.agenda.services.implementations.SolicitanteServiceImpl;
import com.teste.pratico.agenda.validators.SalvarSolicitanteDtoValidator;

@ExtendWith(MockitoExtension.class)
public class SolicitanteServiceTest {

    @Mock
    private SolicitanteRepository solicitanteRepository;

    @Mock
    private SalvarSolicitanteDtoValidator salvarSolicitanteDtoValidator;

    @InjectMocks
    private SolicitanteServiceImpl solicitanteService;

    private Solicitante solicitante;

    private final Integer id = 1;

    @BeforeEach
    void setUp() {
        solicitante = new Solicitante("Luiz", "12345678900");
    }

    @Test
    void deveSalvarComSucesso() {

        SalvarSolicitanteDto dto = new SalvarSolicitanteDto("Luiz", "12345678900");

        doAnswer(invocation -> null).when(salvarSolicitanteDtoValidator).validate(eq(dto), any(Errors.class));
        when(solicitanteRepository.save(any(Solicitante.class))).thenReturn(solicitante);

        Solicitante solicitanteSalvo = solicitanteService.salvar(dto);

        assertNotNull(solicitanteSalvo);

        verify(solicitanteRepository, times(1)).save(any(Solicitante.class));
        verify(salvarSolicitanteDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
    }

    @Test
    void deveLancarExceptionQuandoExistirOutroSolicitanteComMesmoCpf() {

        SalvarSolicitanteDto dto = new SalvarSolicitanteDto("Luiz", "12345678900");

        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("cpf", "cpf.duplicado", "CPF já cadastrado na base de dados.");
            return null;
        }).when(salvarSolicitanteDtoValidator).validate(eq(dto), any(Errors.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> solicitanteService.salvar(dto));

        assertEquals("CPF já cadastrado na base de dados.", exception.getMessage());

        verify(salvarSolicitanteDtoValidator, times(1)).validate(eq(dto), any(Errors.class));
        verify(solicitanteRepository, times(0)).save(any(Solicitante.class));
    }

    @Test
    void deveDeletarComSucesso() {

        when(solicitanteRepository.findById(id)).thenReturn(Optional.of(solicitante));
        doNothing().when(solicitanteRepository).delete(any(Solicitante.class));

        Boolean resultado = solicitanteService.deletar(id);

        assertTrue(resultado);

        verify(solicitanteRepository, times(1)).findById(id);
        verify(solicitanteRepository, times(1)).delete(any(Solicitante.class));
    }

    @Test
    void deveLancarExceptionQuandoSolicitanteNaoEncontrado() {

        when(solicitanteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitanteService.deletar(id));

        verify(solicitanteRepository, times(1)).findById(id);
        verify(solicitanteRepository, times(0)).delete(any(Solicitante.class));
    }

    @Test
    void deveRetornarSolicitantesPaginadosAoObterTodos() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
        Page<Solicitante> page = new PageImpl<>(List.of(solicitante));

        when(solicitanteRepository.findAll(pageable)).thenReturn(page);

        Page<Solicitante> solicitantes = solicitanteService.obterTodos(new PaginacaoDataDto(0, 10));

        assertNotNull(solicitantes);
        assertEquals(1, solicitantes.getTotalElements());

        verify(solicitanteRepository, times(1)).findAll(pageable);
    }
}
