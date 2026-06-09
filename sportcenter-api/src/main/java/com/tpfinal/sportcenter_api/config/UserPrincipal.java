package com.tpfinal.sportcenter_api.config;

import com.tpfinal.sportcenter_api.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre nuestra entidad User y el contrato UserDetails de
 * Spring Security. Envuelve la entidad en vez de hacer que User implemente
 * la interfaz, para que la capa de persistencia no dependa de seguridad.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    /** Entidad completa, para cuando el controller necesita email, rol, etc. */
    public User getUser() {
        return user;
    }

    /** Atajo para expresiones de ownership: #id == principal.id */
    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefijo ROLE_ obligatorio para que hasRole('ADMIN') matchee.
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
}
