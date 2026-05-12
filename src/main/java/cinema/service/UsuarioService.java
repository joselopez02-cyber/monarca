package cinema.monarca.service;

import cinema.monarca.model.Usuario;
import cinema.monarca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new User(
                u.getUsername(),
                u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name())));
    }

    public Usuario registrar(String username, String email, String rawPassword, String rol) {
        return registrar(username, email, rawPassword, rol, null, null, null, null, null);
    }

    public Usuario registrar(String username, String email, String rawPassword, String rol,
                             String nombreCompleto, String cedula, String telefono,
                             String direccion, String fechaNacimiento) {
        if (usuarioRepo.existsByUsername(username))
            throw new RuntimeException("El username '" + username + "' ya está en uso.");
        if (usuarioRepo.existsByEmail(email))
            throw new RuntimeException("El email '" + email + "' ya está registrado.");
        if (cedula != null && !cedula.isBlank() && usuarioRepo.existsByCedula(cedula))
            throw new RuntimeException("La cédula '" + cedula + "' ya está registrada.");

        return usuarioRepo.save(Usuario.builder()
                .username(username)
                .email(email)
                .passwordHash(encoder.encode(rawPassword))
                .rol(rol != null && rol.equalsIgnoreCase("ADMIN") ? Usuario.Rol.ADMIN : Usuario.Rol.USER)
                .activo(true)
                .nombreCompleto(nombreCompleto)
                .cedula(cedula)
                .telefono(telefono)
                .direccion(direccion)
                .fechaNacimiento(fechaNacimiento)
                .build());
    }

    public List<Usuario> obtenerTodos() { return usuarioRepo.findAll(); }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public void cambiarRol(Long id, String nuevoRol) {
        Usuario u = obtenerPorId(id);
        u.setRol(nuevoRol.equalsIgnoreCase("ADMIN") ? Usuario.Rol.ADMIN : Usuario.Rol.USER);
        usuarioRepo.save(u);
    }

    public void actualizarPerfil(Long id, java.util.Map<String, String> datos) {
        Usuario u = obtenerPorId(id);
        if (datos.containsKey("nombreCompleto")) u.setNombreCompleto(datos.get("nombreCompleto"));
        if (datos.containsKey("cedula"))         u.setCedula(datos.get("cedula"));
        if (datos.containsKey("telefono"))       u.setTelefono(datos.get("telefono"));
        if (datos.containsKey("direccion"))      u.setDireccion(datos.get("direccion"));
        if (datos.containsKey("fechaNacimiento")) u.setFechaNacimiento(datos.get("fechaNacimiento"));
        if (datos.containsKey("email"))          u.setEmail(datos.get("email"));
        usuarioRepo.save(u);
    }

    public void desactivar(Long id) {
        Usuario u = obtenerPorId(id);
        u.setActivo(false);
        usuarioRepo.save(u);
    }
}
