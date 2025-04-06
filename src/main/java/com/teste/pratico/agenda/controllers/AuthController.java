package com.teste.pratico.agenda.controllers;

import com.teste.pratico.agenda.dtos.CredenciaisDto;
import com.teste.pratico.agenda.dtos.LoginValidadoDto;
import com.teste.pratico.agenda.dtos.SalvarUsuarioDto;
import com.teste.pratico.agenda.dtos.UsuarioSemSenhaDto;
import com.teste.pratico.agenda.entities.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Autenticação", description = "Endpoints para gerenciar autenticação de usuários")
@RequestMapping("api/v1/auth")
public interface AuthController {

    @Operation(summary = "Salvar um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados"),
            @ApiResponse(responseCode = "404", description = "Solicitante não encontrado")
    })
    @PostMapping
    ResponseEntity<Usuario> salvar(@RequestBody SalvarUsuarioDto dto);

    @Operation(summary = "Validar login do usuário", description = "Valida as credenciais e retorna o token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login validado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    ResponseEntity<LoginValidadoDto> validarLogin(@RequestBody CredenciaisDto dto);

    @Operation(summary = "Obter o usuário autenticado via token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @GetMapping("/info")
    ResponseEntity<UsuarioSemSenhaDto> obterUsuarioLogado();
}