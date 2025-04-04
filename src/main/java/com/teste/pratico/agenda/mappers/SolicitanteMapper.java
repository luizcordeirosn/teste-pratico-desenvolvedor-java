package com.teste.pratico.agenda.mappers;

import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;

public class SolicitanteMapper {
    
    public static Solicitante dtoSalvarParaEntidade(SalvarSolicitanteDto dto) {
        return new Solicitante(dto.nome(), dto.cpf().replaceAll("\\D", ""));
    }
}
