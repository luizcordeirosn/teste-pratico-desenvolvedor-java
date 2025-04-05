package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.teste.pratico.agenda.controllers.EstacionamentoController;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.services.EstacionamentoService;

@RestController
public class EstacionamentoControllerImpl implements EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Override
    public ResponseEntity<Estacionamento> salvar(SalvarEstacionamentoDto dto) {
        Estacionamento estacionamentoSalvo = estacionamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(estacionamentoSalvo);
    }

    @Override
    public ResponseEntity<Boolean> deletar(Integer id) {
        Boolean resultado = estacionamentoService.deletar(id);
        return ResponseEntity.ok(resultado);
    }

    @Override
    public ResponseEntity<Page<Estacionamento>> obterTodos(EstacionamentoFiltroDto filtroDto) {
        Page<Estacionamento> estacionamentos = estacionamentoService.obterTodos(filtroDto);
        return ResponseEntity.ok(estacionamentos);
    }
    
}
