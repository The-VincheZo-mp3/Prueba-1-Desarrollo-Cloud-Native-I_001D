package Tienda.de.Video.Juegos.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Seguridad del backend con 2 user pools de Cognito:
 *  - Pool CLIENTES (frontend tienda)  -> rol CLIENTE  -> solo puede LEER el catalogo
 *  - Pool PERSONAL (frontend admin)   -> rol PERSONAL -> puede leer, crear, editar y borrar
 * El rol lo decide el pool que emitio el token (claim "iss").
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cognito.region:us-east-1}")
    private String region;

    @Value("${app.cognito.pool-clientes-id:}")
    private String poolClientesId;

    @Value("${app.cognito.client-id-clientes:}")
    private String clientIdClientes;

    @Value("${app.cognito.pool-personal-id:}")
    private String poolPersonalId;

    @Value("${app.cognito.client-id-personal:}")
    private String clientIdPersonal;

    @Value("${app.cors.origenes:http://localhost:5173,http://localhost:5174}")
    private String[] origenesPermitidos;

    // Elige con que pool validar el token segun su emisor (iss).
    // Si el token viene de un pool que no es ninguno de los dos, se rechaza (401).
    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        Map<String, AuthenticationManager> gestores = new HashMap<>();
        registrarPool(gestores, poolClientesId, clientIdClientes, "ROLE_CLIENTE");
        registrarPool(gestores, poolPersonalId, clientIdPersonal, "ROLE_PERSONAL");
        return new JwtIssuerAuthenticationManagerResolver(issuer -> gestores.get(issuer));
    }

    // Si un pool no esta configurado todavia se omite, asi la app igual arranca (y los tests pasan)
    private void registrarPool(Map<String, AuthenticationManager> gestores,
                               String poolId, String clientId, String rol) {
        if (poolId == null || poolId.isBlank() || clientId == null || clientId.isBlank()) {
            return;
        }
        String issuer = "https://cognito-idp." + region + ".amazonaws.com/" + poolId;
        gestores.put(issuer, gestorPara(issuer, clientId, rol));
    }

    // Valida 3 cosas: que no este vencido, que lo emitio el pool correcto y que
    // sea de nuestro App Client. La firma la verifica NimbusJwtDecoder con las
    // llaves publicas de Cognito (jwks.json).
    private AuthenticationManager gestorPara(String issuer, String clientIdEsperado, String rol) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(issuer + "/.well-known/jwks.json").build();

        OAuth2TokenValidator<Jwt> conVigencia = new JwtTimestampValidator();
        OAuth2TokenValidator<Jwt> conIssuer = new JwtIssuerValidator(issuer);
        OAuth2TokenValidator<Jwt> conClientId = token -> {
            // Cognito pone el App Client en "client_id" (access token), no en "aud"
            if (clientIdEsperado.equals(token.getClaimAsString("client_id"))) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                    "invalid_token", "El token no pertenece al App Client esperado", null));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(conVigencia, conIssuer, conClientId));

        // Todo token valido de este pool recibe el rol del pool
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(
                jwt -> List.<GrantedAuthority>of(new SimpleGrantedAuthority(rol)));

        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
        provider.setJwtAuthenticationConverter(convertidor);
        return provider::authenticate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationManagerResolver<HttpServletRequest> resolver) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // consola H2
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/error")).permitAll()
                .requestMatchers(antMatcher("/")).permitAll()
                .requestMatchers(antMatcher("/index.html")).permitAll()
                .requestMatchers(antMatcher("/app.js")).permitAll()
                // Leer el catalogo: clientes y personal
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/videojuegos/**")).hasAnyRole("CLIENTE", "PERSONAL")
                // Modificar el catalogo: solo personal
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/videojuegos/**")).hasRole("PERSONAL")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/videojuegos/**")).hasRole("PERSONAL")
                .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/videojuegos/**")).hasRole("PERSONAL")
                // Comprar (manda la orden a SQS): solo clientes
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/ordenes/**")).hasRole("CLIENTE")
                // Estado de las colas: solo personal
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/mensajeria/**")).hasRole("PERSONAL")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(resolver));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(origenesPermitidos));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}