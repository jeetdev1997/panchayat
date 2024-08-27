//package com.panchayat.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//import com.panchayat.entity.AppUser;
//import com.panchayat.exception.ResourceNotFoundException;
//import com.panchayat.repository.AppUserRepository;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers("/public/**").permitAll()
//                        .anyRequest().authenticated())
//                .formLogin(login -> login
//                        .loginPage("/login")
//                        .permitAll())
//                .logout(logout -> logout
//                        .permitAll());
//        
//        return http.build();
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    InMemoryUserDetailsManager inMemoryUserDetailsManager() {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//        
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("USER", "ADMIN")
//                .build();
//        
//        return new InMemoryUserDetailsManager(user, admin);
//    }
//    
//    @Bean
//    public UserDetailsService databaseUserDetailsService(AppUserRepository appUserRepository) {
//        return username -> {
//            AppUser appUser = appUserRepository.findByUserName(username);
//            if (appUser == null) {
//                throw new ResourceNotFoundException("User", "username", username);
//            }
//            return org.springframework.security.core.userdetails.User.withUsername(appUser.getUserName())
//                .password(appUser.getPassword())
//                .roles(appUser.getRole())
//                .build();
//        };
//    }
//}
