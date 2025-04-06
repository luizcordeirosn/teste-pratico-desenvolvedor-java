package com.teste.pratico.agenda.services;

import java.util.Optional;
import com.teste.pratico.agenda.dtos.CredenciaisDto;
import com.teste.pratico.agenda.dtos.LoginValidadoDto;
import com.teste.pratico.agenda.dtos.SalvarUsuarioDto;
import com.teste.pratico.agenda.dtos.UsuarioSemSenhaDto;
import com.teste.pratico.agenda.entities.Usuario;

public interface UsuarioService {
 
    public Usuario salvar(SalvarUsuarioDto dto);

    public LoginValidadoDto validarLogin(CredenciaisDto dto);

    public UsuarioSemSenhaDto obterPorToken();

    public Optional<Usuario> obterPorEmail(String email);
}
