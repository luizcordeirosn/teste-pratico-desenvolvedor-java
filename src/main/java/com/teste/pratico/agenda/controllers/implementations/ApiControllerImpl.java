package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.teste.pratico.agenda.controllers.ApiController;
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.PaginacaoDataDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.enums.StatusVagaEnum;
import com.teste.pratico.agenda.services.EstacionamentoService;
import com.teste.pratico.agenda.services.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApiControllerImpl implements ApiController {

    private final EstacionamentoService estacionamentoService;
    private final ReservaService reservaService;

    @Override
    public ResponseEntity<Estacionamento> salvarEstacionamento(SalvarEstacionamentoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estacionamentoService.salvar(dto));
    }

    @Override
    public ResponseEntity<Estacionamento> atualizarEstacionamento(Integer id, AtualizarEstacionamentoDto dto) {
        return ResponseEntity.ok(estacionamentoService.atualizar(id, dto));
    }

    @Override
    public ResponseEntity<Boolean> deletarEstacionamento(Integer id) {
        return ResponseEntity.ok(estacionamentoService.deletar(id));
    }

    @Override
    public ResponseEntity<Page<Estacionamento>> obterTodosEstacionamentos(String tipo, StatusVagaEnum status,
            Integer pagina, Integer tamanho) {
        return ResponseEntity.ok(estacionamentoService.obterTodos(new EstacionamentoFiltroDto(tipo, status, new PaginacaoDataDto(pagina, tamanho))));
    }

    @Override
    public ResponseEntity<Reserva> salvarReserva(SalvarReservaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.salvar(dto));
    }

    @Override
    public ResponseEntity<Reserva> encerrarReserva(Integer id, EncerrarReservaDto dto) {
        return ResponseEntity.ok(reservaService.encerrarReserva(id, dto));
    }

    @Override
    public ResponseEntity<Page<Reserva>> obterTodosReservas(Integer solicitanteId, Integer pagina, Integer tamanho) {
        return ResponseEntity.ok(
                reservaService.obterTodos(new ReservaFiltroDto(solicitanteId, new PaginacaoDataDto(pagina, tamanho))));
    }
}
