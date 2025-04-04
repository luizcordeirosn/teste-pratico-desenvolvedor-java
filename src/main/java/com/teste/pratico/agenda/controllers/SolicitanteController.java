package com.teste.pratico.agenda.controllers;

import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/solicitante")
@Tag(name = "Solicitante", description = "Endpoints para gerenciar solicitantes")
public interface SolicitanteController {

    @Operation(summary = "Salvar um novo solicitante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação dos dados")
    })
    @PostMapping
    ResponseEntity<Solicitante> salvar(@RequestBody SalvarSolicitanteDto dto);

    @Operation(summary = "Deletar um solicitante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitante deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Solicitante não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Boolean> deletar(@PathVariable Integer id);

    @Operation(summary = "Obter todos os solicitantes paginados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitantes retornada com sucesso")
    })
    @GetMapping
    ResponseEntity<Page<Solicitante>> obterTodos(
            @RequestParam(required = false, defaultValue = "0") Integer pagina,
            @RequestParam(required = false, defaultValue = "10") Integer tamanho);
}