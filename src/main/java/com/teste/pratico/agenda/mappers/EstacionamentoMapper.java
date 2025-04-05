package com.teste.pratico.agenda.mappers;

import com.teste.pratico.agenda.dtos.SalvarEstacionamentoDto;
import com.teste.pratico.agenda.entities.Estacionamento;

public class EstacionamentoMapper {

    public static Estacionamento dtoSalvarParaEntidade(SalvarEstacionamentoDto dto) {
        return new Estacionamento(dto.numero(), dto.tipo().toLowerCase(), dto.valorPorHora());
    }
}
