package com.teste.pratico.agenda.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SenhaUtil {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public static boolean validarSenha(String senha, String senhaCriptografada) {
        return passwordEncoder.matches(senha, senhaCriptografada);
    }
}
