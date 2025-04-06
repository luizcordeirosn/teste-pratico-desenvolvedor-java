package com.teste.pratico.agenda.mappers;

import com.teste.pratico.agenda.dtos.SalvarUsuarioDto;
import com.teste.pratico.agenda.dtos.UsuarioSemSenhaDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.entities.Usuario;
import com.teste.pratico.agenda.utils.SenhaUtil;

public class UsuarioMapper {

    public static Usuario dtoSalvarParaEntidade(Solicitante solicitante, SalvarUsuarioDto dto) {
        return new Usuario(dto.email(), SenhaUtil.criptografarSenha(dto.senha()), solicitante);
    }

    public static UsuarioSemSenhaDto entidadeParaDtoSemSenha(Usuario usuario) {
        return new UsuarioSemSenhaDto(usuario.getId(), usuario.getEmail(), usuario.getSolicitante());
    }
}
