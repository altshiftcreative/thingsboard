package com.asc.bluewaves.lwm2m.security.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider {

	@Value("${security.jwt.secret}")
	private String secret;

	private static final String SCOPES = "scopes";
//    private static final String USER_ID = "userId";
//    private static final String FIRST_NAME = "firstName";
//    private static final String LAST_NAME = "lastName";
//    private static final String ENABLED = "enabled";
//    private static final String IS_PUBLIC = "isPublic";
//    private static final String TENANT_ID = "tenantId";
//    private static final String CUSTOMER_ID = "customerId";

	private Key key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		List<String> scopes = claims.get(SCOPES, List.class);
		if (scopes == null || scopes.isEmpty()) {
			throw new IllegalArgumentException("JWT Token doesn't have any scopes");
		}

		Collection<? extends GrantedAuthority> authorities = scopes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		User principal = new User(claims.getSubject(), "", authorities);

		// TODO: Add user details
		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.info("Invalid JWT token.");
			log.trace("Invalid JWT token trace.", e);
		}
		return false;
	}
}
