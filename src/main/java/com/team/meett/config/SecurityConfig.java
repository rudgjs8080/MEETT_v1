package com.team.meett.config;

import com.team.meett.service.impl.UserServiceImplV1;
import lombok.RequiredArgsConstructor;
//import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
//import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImplV1 userServiceImplV1;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public LayoutDialect layoutDialect() {
//        return new LayoutDialect();
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**"); // static 인증없이
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors().and().httpBasic().disable().
//                csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll()
                .and().formLogin().loginPage("/user/login").defaultSuccessUrl("/home").permitAll()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")).logoutSuccessUrl("/user/login").invalidateHttpSession(true);

        //session
        http.sessionManagement()
                .maximumSessions(1).maxSessionsPreventsLogin(true).expiredUrl("/user/login");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImplV1).passwordEncoder(passwordEncoder());
    }
}
