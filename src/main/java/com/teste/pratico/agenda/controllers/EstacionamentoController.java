package com.teste.pratico.agenda.controllers;

import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vagas de Estacionamento", description = "Endpoints para gerenciar vagas de estacionamentos e reservas")
@RequestMapping("/api")
public interface EstacionamentoController {

    @Operation(summary = "Salvar uma nova vaga de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vaga de estacionamento criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação dos dados")
    })
    @PostMapping("/vagas")
    ResponseEntity<Estacionamento> salvar(@RequestBody SalvarEstacionamentoDto dto);

    @Operation(summary = "Deletar uma vaga de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vaga de estacionamento deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Vaga de estacionamento não encontrado")
    })
    @DeleteMapping("/vagas/{id}")
    ResponseEntity<Boolean> deletar(@PathVariable Integer id);

    @Operation(summary = "Listar vagas de estacionamentos com filtro e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vagas de estacionamentos listados com sucesso")
    })
    @GetMapping("/vagas")
    ResponseEntity<Page<Estacionamento>> obterTodos(@ModelAttribute EstacionamentoFiltroDto filtroDto);
}
