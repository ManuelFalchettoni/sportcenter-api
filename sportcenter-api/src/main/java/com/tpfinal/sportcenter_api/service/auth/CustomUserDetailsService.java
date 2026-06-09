package com.tpfinal.sportcenter_api.service.auth;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.repository.user.JpaUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga el usuario desde la base para Spring Security. Lo usa JwtFilter en
 * cada request autenticada: así el rol y la existencia del usuario se
 * verifican contra la DB y no contra lo que diga el token. Un usuario
 * borrado o degradado pierde acceso al instante, sin esperar la expiración.
 *
 * <p>Declarar este bean también desactiva el usuario en memoria con password
 * autogenerada que Spring Boot crea por defecto.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    public CustomUserDetailsService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca por username, que es el subject de nuestros JWT.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
