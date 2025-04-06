package com.teste.pratico.agenda.dtos;

public record SalvarReservaDto(
        Integer estacionamentoId,
        Integer solicitanteId,
        String dataInicio) {

}
