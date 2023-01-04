package com.yapp.itemfinder.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityFilterConfig {
    companion object {
        private const val ADMIN_ROLE_NAME = "ADMIN"
    }

    @Value("\${admin.username:#{'default'}}")
    lateinit var adminUserName: String

    @Value("\${admin.password:#{'default'}}")
    lateinit var adminPassword: String

    // swagger 결과는 어드민 아이디/비번으로 로그인해야 접근 가능, 나머지 Endpoint는 모두 접근 가능
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeRequests()
            .antMatchers("/api-docs/**").hasAuthority(ADMIN_ROLE_NAME)
            .and()
            .httpBasic()
            .and()
            .csrf()
            .disable()

        return http.build()
    }

    @Profile("!prod")
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Profile("!prod")
    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val user: UserDetails = User.withUsername(adminUserName)
            .password(passwordEncoder.encode(adminPassword))
            .authorities(SimpleGrantedAuthority(ADMIN_ROLE_NAME))
            .build()
        return InMemoryUserDetailsManager(user)
    }
}
