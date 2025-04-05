package com.fsbr.teste.pratico.TestePraticoSpringBoot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.exceptions.ResourceNotFoundException;
import com.teste.pratico.agenda.repositories.SolicitanteRepository;
import com.teste.pratico.agenda.services.implementations.SolicitanteServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SolicitanteServiceTest {

    @Mock
    private SolicitanteRepository solicitanteRepository;

    @InjectMocks
    private SolicitanteServiceImpl solicitanteService;

    private Solicitante solicitante;

    @BeforeEach
    void setUp() {
        solicitante = new Solicitante("Luiz", "12345678900");
    }

    @Test
    void deveSalvarComSucesso() {

        when(solicitanteRepository.save(any(Solicitante.class))).thenReturn(solicitante);

        SalvarSolicitanteDto dto = new SalvarSolicitanteDto("Luiz", "12345678900");

        Solicitante solicitanteSalvo = solicitanteService.salvar(dto);

        assertNotNull(solicitanteSalvo);
        verify(solicitanteRepository, times(1)).save(any(Solicitante.class));
    }

    @Test
    void deveLancarExceptionQuandoExistirOutroSolicitanteComMesmoCpf() {
        when(solicitanteRepository.findByCpf("12345678900")).thenReturn(Optional.of(solicitante));

        SalvarSolicitanteDto dto = new SalvarSolicitanteDto("Luiz", "12345678900");

        assertThrows(IllegalArgumentException.class, () -> solicitanteService.salvar(dto));
        
        verify(solicitanteRepository, times(0)).save(any(Solicitante.class));
    }

    @Test
    void deveDeletarComSucesso() {
        Integer id = 1;

        when(solicitanteRepository.findById(id)).thenReturn(Optional.of(solicitante));
        doNothing().when(solicitanteRepository).delete(any(Solicitante.class));

        assertTrue(solicitanteService.deletar(id));

        verify(solicitanteRepository, times(1)).findById(id);
        verify(solicitanteRepository, times(1)).delete(any(Solicitante.class));
    }

    @Test 
    void deveLancarExceptionQuandoSolicitanteNaoEncontrado() {
        Integer id = 1;

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

        Page<Solicitante> solicitantes = solicitanteService.obterTodos(0, 10);

        assertNotNull(solicitantes);
        assertEquals(1, solicitantes.getTotalElements());
        verify(solicitanteRepository, times(1)).findAll(pageable);
    }
}
