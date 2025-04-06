package com.teste.pratico.agenda.dtos;

import com.teste.pratico.agenda.enums.StatusVagaEnum;

public record EstacionamentoFiltroDto(
        String tipo,
        StatusVagaEnum status,
        PaginacaoDataDto paginacao) {

}
