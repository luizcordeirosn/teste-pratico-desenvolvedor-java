package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.teste.pratico.agenda.controllers.ApiController;
import com.teste.pratico.agenda.dtos.AtualizarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.services.EstacionamentoService;
import com.teste.pratico.agenda.services.ReservaService;

@RestController
public class ApiControllerImpl implements ApiController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private ReservaService reservaService;

    @Override
    public ResponseEntity<Estacionamento> salvarEstacionamento(SalvarEstacionamentoDto dto) {
        Estacionamento estacionamentoSalvo = estacionamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(estacionamentoSalvo);
    }

    @Override
    public ResponseEntity<Estacionamento> atualizarEstacionamento(Integer id, AtualizarEstacionamentoDto dto) {
        Estacionamento estacionamentoAtualizado = estacionamentoService.atualizar(id, dto);
        return ResponseEntity.ok(estacionamentoAtualizado);
    }

    @Override
    public ResponseEntity<Boolean> deletarEstacionamento(Integer id) {
        Boolean resultado = estacionamentoService.deletar(id);
        return ResponseEntity.ok(resultado);
    }

    @Override
    public ResponseEntity<Page<Estacionamento>> obterTodosEstacionamentos(EstacionamentoFiltroDto filtroDto) {
        Page<Estacionamento> estacionamentos = estacionamentoService.obterTodos(filtroDto);
        return ResponseEntity.ok(estacionamentos);
    }

    @Override
    public ResponseEntity<Reserva> salvarReserva(SalvarReservaDto dto) {
        Reserva reservaSalva = reservaService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaSalva);
    }

    @Override
    public ResponseEntity<Reserva> encerrarReserva(Integer id, EncerrarReservaDto dto) {
        Reserva reservaEncerrada = reservaService.encerrarReserva(id, dto);
        return ResponseEntity.ok(reservaEncerrada);
    }

    @Override
    public ResponseEntity<Page<Reserva>> obterTodosReservas(ReservaFiltroDto dto) {
        Page<Reserva> reservas = reservaService.obterTodos(dto);
        return ResponseEntity.ok(reservas);
    }
}
