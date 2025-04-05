package com.teste.pratico.agenda.dtos;

import com.teste.pratico.agenda.enums.StatusVagaEnum;

public record EstacionamentoFiltroDto(
        String tipo,
        StatusVagaEnum status,
        Integer pagina,
        Integer tamanho) {
            
    public EstacionamentoFiltroDto {
        if (pagina == null)
            pagina = 0;
        if (tamanho == null)
            tamanho = 10;
        if (status == null)
            status = StatusVagaEnum.DISPONIVEL;
    }
}
