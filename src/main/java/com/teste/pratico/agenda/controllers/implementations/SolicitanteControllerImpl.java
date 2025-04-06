package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.teste.pratico.agenda.controllers.SolicitanteController;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.services.SolicitanteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SolicitanteControllerImpl implements SolicitanteController{

    private final SolicitanteService service;

    @Override
    public ResponseEntity<Solicitante> salvar(SalvarSolicitanteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @Override
    public ResponseEntity<Boolean> deletar(Integer id) {
        return ResponseEntity.ok(service.deletar(id));
    }

    @Override
    public ResponseEntity<Page<Solicitante>> obterTodos(Integer pagina, Integer tamanho) {
        return ResponseEntity.ok(service.obterTodos(new PaginacaoDataDto(pagina, tamanho)));
    }
}
