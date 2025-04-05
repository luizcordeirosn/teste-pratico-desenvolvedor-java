package com.teste.pratico.agenda.mappers;

import java.sql.Timestamp;
import com.teste.pratico.agenda.dtos.SalvarReservaDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.entities.Reserva;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.utils.DataUtil;

public class ReservaMapper {

    public static Reserva dtoSalvarParaEntidade(Estacionamento estacionamento, Solicitante solicitante,
            SalvarReservaDto dto) {

        return new Reserva(estacionamento, solicitante,
                dto.dataInicio() == null ? new Timestamp(System.currentTimeMillis())
                        : DataUtil.stringParaTimestamp(dto.dataInicio()));
    }
}
