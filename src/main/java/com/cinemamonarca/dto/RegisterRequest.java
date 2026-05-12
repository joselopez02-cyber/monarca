package com.cinemamonarca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 6) private String password;

    // Perfil obligatorio en autoregistro
    private String nombreCompleto;
    private String cedula;
    private String telefono;
    private String direccion;
    private String fechaNacimiento; // yyyy-MM-dd

    // Rol siempre USER en registro; sólo admin lo cambia luego
    // Se mantiene el campo para uso interno en UsuarioController (crear desde admin)
    private String rol;
}

