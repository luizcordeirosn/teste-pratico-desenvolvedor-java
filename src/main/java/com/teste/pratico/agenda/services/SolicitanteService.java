package com.teste.pratico.agenda.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import com.teste.pratico.agenda.dtos.SalvarSolicitanteDto;
import com.teste.pratico.agenda.entities.Solicitante;

public interface SolicitanteService {

    public Solicitante salvar(SalvarSolicitanteDto dto);

    public Boolean deletar(Integer id);

    public Solicitante obterPorId(Integer id);

    public Optional<Solicitante> obterPorCpf(String cpf);

    public Page<Solicitante> obterTodos(Integer pagina, Integer tamanho);
}
