package com.teste.pratico.agenda.services.implementations;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.teste.pratico.agenda.dtos.CredenciaisDto;
import com.teste.pratico.agenda.dtos.LoginValidadoDto;
import com.teste.pratico.agenda.dtos.SalvarUsuarioDto;
import com.teste.pratico.agenda.dtos.UsuarioSemSenhaDto;
import com.teste.pratico.agenda.entities.Solicitante;
import com.teste.pratico.agenda.entities.Usuario;
import com.teste.pratico.agenda.exceptions.UnauthorizedException;
import com.teste.pratico.agenda.mappers.UsuarioMapper;
import com.teste.pratico.agenda.repositories.UsuarioRepository;
import com.teste.pratico.agenda.security.JwtUtil;
import com.teste.pratico.agenda.services.SolicitanteService;
import com.teste.pratico.agenda.services.UsuarioService;
import com.teste.pratico.agenda.utils.SenhaUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository repository;
    private final SolicitanteService solicitanteService;
    private final JwtUtil jwtUtil;

    @Override
    public Usuario salvar(SalvarUsuarioDto dto) {
        logger.info("salvar(email: {}, solicitanteId: {})", dto.email(), dto.solicitanteId());

        if (obterPorEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Usuário já cadastrado na base.");
        }

        Solicitante solicitante = solicitanteService.obterPorId(dto.solicitanteId());

        Usuario usuario = UsuarioMapper.dtoSalvarParaEntidade(solicitante, dto);

        return repository.save(usuario);
    }

    @Override
    public LoginValidadoDto validarLogin(CredenciaisDto dto) {
        logger.info("validarLogin: {}", dto.email());

        Optional<Usuario> optionalUsuario = obterPorEmail(dto.email());

        if (optionalUsuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário ainda não cadastrado na base.");
        }

        Boolean senhaValida = SenhaUtil.validarSenha(dto.senha(), optionalUsuario.get().getSenha());

        if (Boolean.FALSE.equals(senhaValida)) {
            throw new UnauthorizedException("Credenciais inválidas. Acesso negado.");
        }

        return new LoginValidadoDto(jwtUtil.generateToken(optionalUsuario.get().getEmail(),
                optionalUsuario.get().getSolicitante().getNome()));
    }

    @Override
    public Optional<Usuario> obterPorEmail(String email) {
        logger.info("obterPorEmail: {}", email);

        return repository.findByEmail(email);
    }

    @Override
    public UsuarioSemSenhaDto obterPorToken() {
        logger.info("obterPorToken");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Usuario> optionalUsuario = obterPorEmail(email);

        if (optionalUsuario.isEmpty()) {
            throw new UnauthorizedException("Credenciais inválidas. Acesso negado.");
        }

        return UsuarioMapper.entidadeParaDtoSemSenha(optionalUsuario.get());
    }
}
