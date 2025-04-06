package com.teste.pratico.agenda.controllers;

import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "BearerAuth")
@Tag(name = "API", description = "Endpoints para gerenciar vagas de estacionamentos e reservas")
@RequestMapping("/api/v1")
public interface ApiController {

    @Operation(summary = "Salvar uma nova vaga de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vaga de estacionamento criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação dos dados"),
        @ApiResponse(responseCode = "401", description = "Acesso Negado")
    })
    @PostMapping("/vagas")
    ResponseEntity<Estacionamento> salvarEstacionamento(@RequestBody SalvarEstacionamentoDto dto);

    @PatchMapping("/vagas/{id}")
    @Operation(summary = "Atualizar status da vaga", description = "Atualiza o status (DISPONIVEL, RESERVADA, OCUPADA) da vaga de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso", content = @Content(schema = @Schema(implementation = Estacionamento.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Acesso Negado"),
        @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado"),
    })
    public ResponseEntity<Estacionamento> atualizarEstacionamento(
            @PathVariable Integer id,
            @RequestBody AtualizarEstacionamentoDto dto
    );

    @Operation(summary = "Deletar uma vaga de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vaga de estacionamento deletado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Acesso Negado"),
        @ApiResponse(responseCode = "404", description = "Vaga de estacionamento não encontrado")
    })
    @DeleteMapping("/vagas/{id}")
    ResponseEntity<Boolean> deletarEstacionamento(@PathVariable Integer id);

    @Operation(summary = "Listar vagas de estacionamentos com filtro e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vagas de estacionamentos listados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os parâmetros e tente novamente."),
        @ApiResponse(responseCode = "401", description = "Acesso Negado"),
    })
    @GetMapping("/vagas")
    ResponseEntity<Page<Estacionamento>> obterTodosEstacionamentos(
        @RequestParam(required = false, defaultValue = "") String tipo,
        @RequestParam(required = false, defaultValue = "DISPONIVEL") StatusVagaEnum status,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer tamanho
    );

    @Operation(summary = "Criar uma nova reserva de estacionamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso", content = @Content(schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Vaga já está ocupada ou reservada"),
        @ApiResponse(responseCode = "401", description = "Acesso Negado"),
        @ApiResponse(responseCode = "404", description = "Estacionamento ou solicitante não encontrado")
    })
    @PostMapping("/reservas")
    ResponseEntity<Reserva> salvarReserva(@RequestBody SalvarReservaDto dto);

    @Operation(summary = "Encerrar uma reserva", description = "Finaliza a reserva de uma vaga, registrando a data de término e calculando o valor total")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encerrada com sucesso", content = @Content(schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Acesso Negado"),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @PatchMapping("/reservas/{id}/encerrar")
    ResponseEntity<Reserva> encerrarReserva(@PathVariable Integer id, @RequestBody EncerrarReservaDto dto);

    @Operation(summary = "Listar reservas com filtros e paginação", description = "Retorna uma lista paginada de reservas com base nos filtros aplicados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas listadas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os parâmetros e tente novamente."),
        @ApiResponse(responseCode = "401", description = "Acesso Negado")
    })
    @GetMapping("/reservas")
    public ResponseEntity<Page<Reserva>> obterTodosReservas(
        @RequestParam(required = false) Integer solicitanteId,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer tamanho
    );
}
