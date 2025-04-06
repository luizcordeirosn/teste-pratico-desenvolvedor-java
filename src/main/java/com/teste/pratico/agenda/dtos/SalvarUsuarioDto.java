package com.teste.pratico.agenda.dtos;

public record SalvarUsuarioDto(
    String email,
    String senha,
    Integer solicitanteId
) {
    
}
