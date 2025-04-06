package com.teste.pratico.agenda.dtos;

import com.teste.pratico.agenda.entities.Solicitante;

public record UsuarioSemSenhaDto(
    Integer id,
    String email,
    Solicitante solicitante
) {
    
}
