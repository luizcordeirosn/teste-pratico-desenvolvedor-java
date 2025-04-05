package com.teste.pratico.agenda.dtos;

import com.teste.pratico.agenda.enums.StatusVagaEnum;

public record AtualizarEstacionamentoDto(
        StatusVagaEnum status) {

}
