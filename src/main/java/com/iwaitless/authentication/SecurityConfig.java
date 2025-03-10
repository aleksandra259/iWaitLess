package com.iwaitless.authentication;

import com.iwaitless.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Value("${spring.datasource.url}")
    public String databaseUrl;
    @Value("${spring.datasource.username}")
    public String databaseUsername;
    @Value("${spring.datasource.password}")
    public String databasePass;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        AntPathRequestMatcher
                                .antMatcher(HttpMethod.GET, "/images/*.png"))
                .permitAll());

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        driverManagerDataSource.setUrl(databaseUrl);
        driverManagerDataSource.setUsername(databaseUsername);
        driverManagerDataSource.setPassword(databasePass);
        driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return driverManagerDataSource;
    }

    @Bean
    public UserDetailsService users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
}
