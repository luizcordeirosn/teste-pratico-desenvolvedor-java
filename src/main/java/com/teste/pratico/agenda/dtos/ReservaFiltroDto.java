package com.teste.pratico.agenda.dtos;

public record ReservaFiltroDto(
    Integer solicitanteId,
    Integer pagina, 
    Integer tamanho
) {
    
}
