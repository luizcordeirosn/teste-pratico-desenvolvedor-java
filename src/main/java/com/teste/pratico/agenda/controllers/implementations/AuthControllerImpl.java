package com.teste.pratico.agenda.controllers.implementations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.teste.pratico.agenda.controllers.AuthController;
import com.teste.pratico.agenda.dtos.CredenciaisDto;
import com.teste.pratico.agenda.dtos.LoginValidadoDto;
import com.teste.pratico.agenda.dtos.SalvarUsuarioDto;
import com.teste.pratico.agenda.dtos.UsuarioSemSenhaDto;
import com.teste.pratico.agenda.entities.Usuario;
import com.teste.pratico.agenda.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{

    private final UsuarioService service;

    @Override
    public ResponseEntity<Usuario> salvar(SalvarUsuarioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @Override
    public ResponseEntity<LoginValidadoDto> validarLogin(CredenciaisDto dto) {
        return ResponseEntity.ok(service.validarLogin(dto));
    }

    @Override
    public ResponseEntity<UsuarioSemSenhaDto> obterUsuarioLogado() {
        return ResponseEntity.ok(service.obterPorToken());
    }
    
}
