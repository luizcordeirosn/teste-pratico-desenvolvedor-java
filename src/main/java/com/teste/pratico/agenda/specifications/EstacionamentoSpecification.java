package com.teste.pratico.agenda.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.teste.pratico.agenda.dtos.EstacionamentoFiltroDto;
import com.teste.pratico.agenda.entities.Estacionamento;
import com.teste.pratico.agenda.enums.StatusVagaEnum;

public class EstacionamentoSpecification {

    public static Specification<Estacionamento> filtrarEstacionamento(EstacionamentoFiltroDto dto) {
        return Specification
                .where(filtrarEstacionamentoPorTipo(dto.tipo()))
                .and(filtrarEstacionamentoPorStatus(dto.status()));
    }

    private static Specification<Estacionamento> filtrarEstacionamentoPorTipo(String tipo) {
        return (root, query, builder) -> {
            if (tipo == null) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get("tipo")), "%" + tipo.toLowerCase() + "%");
        };
    }

    private static Specification<Estacionamento> filtrarEstacionamentoPorStatus(StatusVagaEnum status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }
}
