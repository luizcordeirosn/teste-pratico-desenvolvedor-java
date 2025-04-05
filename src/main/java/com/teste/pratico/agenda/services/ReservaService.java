package com.teste.pratico.agenda.services;

import org.springframework.data.domain.Page;

import com.teste.pratico.agenda.dtos.EncerrarReservaDto;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Reserva;

public interface ReservaService {
    
    public Reserva salvar(SalvarReservaDto dto);

    public Reserva encerrarReserva(Integer id, EncerrarReservaDto dto);

    public Reserva obterPorId(Integer id);

    public Page<Reserva> obterTodos(ReservaFiltroDto dto);
}
