//package com.example.myapp.service;
//import com.example.myapp.model.User;
//import com.example.myapp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//
//import com.example.myapp.model.User;
//import com.example.myapp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Buscar el usuario por su nombre de usuario en el repositorio
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//
//        // Convertir los roles del usuario a una lista de GrantedAuthority
//        List<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName()))  // Usa los nombres de los roles tal como están en la base de datos
//                .collect(Collectors.toList());
//
//        // Crear un objeto UserDetails que Spring Security pueda utilizar
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),  // La contraseña está codificada
//                authorities  // Pasa la lista de roles (GrantedAuthority)
//        );
//    }
//}


package com.example.myapp.service;

import com.example.myapp.model.User;
import com.example.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log para depurar el nombre de usuario
        System.out.println("Intentando cargar usuario con username: " + username);

        // Buscar el usuario por su nombre de usuario en el repositorio
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("No se encontró el usuario con username: " + username);  // Log si no se encuentra
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Log para verificar si el usuario fue encontrado
        System.out.println("Usuario encontrado: " + user.getUsername());

        // Convertir los roles del usuario a una lista de GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))  // Usa los nombres de los roles tal como están en la base de datos
                .collect(Collectors.toList());

        // Log para verificar los roles encontrados
        System.out.println("Roles del usuario: " + authorities);

        // Crear un objeto UserDetails que Spring Security pueda utilizar
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),  // La contraseña está codificada
                authorities  // Pasa la lista de roles (GrantedAuthority)
        );
    }
}
