package com.herokuapp.erpmesbackend.erpmesbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }

    @Bean
    public JwtAuthFilter authTokenFilterBean() throws Exception {
        return new JwtAuthFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers(HttpMethod.POST, "/generate-token").permitAll()
                .antMatchers(HttpMethod.GET, "/employees").permitAll()
                .antMatchers(HttpMethod.POST, "/employees").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/employees/{id}").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/profiles/{id}/contract")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ACCOUNTANT")
                .antMatchers("/reports", "/reports/{id}")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ACCOUNTANT")
                .antMatchers("/current-report", "/current-report/recommended-recalculations")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ACCOUNTANT")
                .antMatchers(HttpMethod.GET, "/employees/{managerId}/subordinates/holiday-requests")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ADMIN_ANALYST", "ADMIN_WAREHOUSE")
                .antMatchers(HttpMethod.POST, "/employees/{managerId}/subordinates/{subordinateId}/holidays")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ADMIN_ANALYST", "ADMIN_WAREHOUSE")
                .antMatchers("/deliveries", "/deliveries/{id}", "/deliveries/recommended-delivery")
                    .hasAnyAuthority("ADMIN", "ADMIN_WAREHOUSE", "WAREHOUSE")
                .antMatchers("/items", "/items/{id}", "/items/{id}/supply", "/items/{id}/buy")
                    .hasAnyAuthority("ADMIN", "ADMIN_WAREHOUSE", "WAREHOUSE")
                .antMatchers("/set-special-offer", "/cancel-special-offer")
                    .hasAuthority("ADMIN")
                .antMatchers("/daily-plan", "/scheduled-orders", "/special-plan")
                    .hasAnyAuthority("ADMIN", "ANALYST", "ADMIN_ANALYST")
                .antMatchers("/orders", "/orders/{id}")
                    .hasAnyAuthority("ADMIN", "ADMIN_WAREHOUSE", "WAREHOUSE")
                .antMatchers(HttpMethod.GET, "/suggestions", "/suggestions/{id}",
                        "/employees/{id}/suggestions")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ADMIN_ANALYST", "ADMIN_WAREHOUSE")
                .antMatchers(HttpMethod.POST, "/suggestions").permitAll()
                .antMatchers(HttpMethod.PATCH, "/suggestions/{id}")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ADMIN_ANALYST", "ADMIN_WAREHOUSE")
                .antMatchers(HttpMethod.GET,"/tasks", "/tasks/{id}", "/employees/{id}/tasks").permitAll()
                .antMatchers(HttpMethod.POST,"/tasks")
                    .hasAnyAuthority("ADMIN", "ADMIN_ACCOUNTANT", "ADMIN_ANALYST", "ADMIN_WAREHOUSE")
                .antMatchers(HttpMethod.PUT,"/tasks/{id}").permitAll()
                .antMatchers(HttpMethod.PATCH,"/tasks/{id}").permitAll()
                .antMatchers(HttpMethod.DELETE, "/tasks/{id}").permitAll()
                .antMatchers("/notifications", "/notifications/{id}", "/employees/{id}/notifications")
                    .hasAnyAuthority("ADMIN", "ADMIN_WAREHOUSE", "WAREHOUSE")
                .antMatchers("/channels", "/channels/{id}", "/employees/{id}/channels", "/messages/{id}")
                    .permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilterBefore(authTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
