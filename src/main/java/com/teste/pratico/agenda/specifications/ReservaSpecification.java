package com.teste.pratico.agenda.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.teste.pratico.agenda.dtos.ReservaFiltroDto;
import com.teste.pratico.agenda.entities.Reserva;

public class ReservaSpecification {

    public static Specification<Reserva> filtrarReserva(ReservaFiltroDto dto) {
        return Specification.where(filtrarReservaPorSolicitanteId(dto.solicitanteId()));
    }

    private static Specification<Reserva> filtrarReservaPorSolicitanteId(Integer solicitanteId) {
        return (root, query, builder) -> {
            if (solicitanteId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("solicitante").get("id"), solicitanteId);
        };
    }
}
