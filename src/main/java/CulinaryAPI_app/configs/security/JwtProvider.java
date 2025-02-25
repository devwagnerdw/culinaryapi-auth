package CulinaryAPI_app.configs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;



@Component
public class JwtProvider {
    @Value("${culinary.auth.jwt-secret}")
    private String jwtSecret;

    @Value("${culinary.auth.jwt-expiration-ms}")
    private int jwtExpirationMs;

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    public String generateJwt(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        final String roles = userPrincipal.getAuthorities().stream()
                .map(role -> {
                    return role.getAuthority();
                }).collect(Collectors.joining(","));
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject((userPrincipal.getUserId().toString()))
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getSubjectJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwt(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.warn("Token inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("Tipo de token não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Token ausente ou malformado: {}", e.getMessage());
        }
        return false;
    }



}
