package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.teste.pratico.agenda.controllers.SolicitanteController;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.services.SolicitanteService;

@RestController
public class SolicitanteControllerImpl implements SolicitanteController{

    @Autowired
    private SolicitanteService service;

    @Override
    public ResponseEntity<Solicitante> salvar(SalvarSolicitanteDto dto) {
        Solicitante solicitanteSalvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitanteSalvo);
    }

    @Override
    public ResponseEntity<Boolean> deletar(Integer id) {
        Boolean resultado = service.deletar(id);
        return ResponseEntity.ok(resultado);
    }

    @Override
    public ResponseEntity<Page<Solicitante>> obterTodos(Integer pagina, Integer tamanho) {
        Page<Solicitante> solicitantes = service.obterTodos(pagina, tamanho);
        return ResponseEntity.ok(solicitantes);
    }
}
