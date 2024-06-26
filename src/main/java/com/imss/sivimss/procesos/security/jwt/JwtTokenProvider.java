package com.imss.sivimss.procesos.security.jwt;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.imss.sivimss.procesos.model.request.UsuarioDto;
import com.imss.sivimss.procesos.utils.AppConstantes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

 	@Value("${jwt.secretkey-flujo}")
	private String jwtSecretFlujo;

	@Value("${jwt.expiration-milliseconds}")
	private String expiration;

	public String createToken(String subject) {
		Gson gson = new Gson();
		UsuarioDto usuario = gson.fromJson(subject, UsuarioDto.class);
		String datosUsuario = "{" + "\"idVelatorio\":'" + usuario.getIdVelatorio() + "',"
				+ "\"idRol\":'" + usuario.getIdRol() + "'," + "\"desRol\":'" + usuario.getDesRol() + "',"
				+ "\"idOficina\":'" + usuario.getIdOficina() + "'," + "\"idUsuario\":'" + usuario.getIdUsuario() + "',"
				+ "\"cveUsuario\":'" + usuario.getCveUsuario() + "'," + "\"cveMatricula\":'" + usuario.getCveMatricula()
				+ "',"
				+ "\"nombre\":'" + usuario.getNombre() + "'," + "\"curp\":'" + usuario.getCurp() + "',"
				+ "\"idDelegacion\":'" + usuario.getIdDelegacion() + "'}";

		Map<String, Object> claims = Jwts.claims().setSubject(datosUsuario);

		Date now = new Date();
		Date exp = new Date(now.getTime() + Long.parseLong(expiration) * 1000);
		return Jwts.builder().setHeaderParam("sistema", "sivimss").setClaims(claims).setIssuedAt(now).setExpiration(exp)
				.signWith(SignatureAlgorithm.HS512, jwtSecretFlujo).compact();
	}

	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecretFlujo).parseClaimsJws(token).getBody();

		return Long.parseLong(claims.getSubject());
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecretFlujo).parseClaimsJws(token).getBody();

		return claims.getSubject();
	}


	public boolean validateToken(String authToken, HttpServletRequest request) {
		try {

			Jwts.parser().setSigningKey(jwtSecretFlujo).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.MALFORMEDJWTEXCEPTION);
			return false;
		} catch (UnsupportedJwtException e) {
			logger.error("token no soportado");
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.UNSUPPORTEDJWTEXCEPTION);
			return false;
		} catch (ExpiredJwtException e) {
			logger.error("token expirado");
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.EXPIREDJWTEXCEPTION);
			return false;
		} catch (IllegalArgumentException e) {
			logger.error("token vacío {}", e.getMessage());
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.ILLEGALARGUMENTEXCEPTION);
			return false;
		} catch (SignatureException e) {
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.SIGNATUREEXCEPTION);
			return false;
		} catch (Exception e) {
			request.setAttribute(AppConstantes.STATUSEXCEPTION, AppConstantes.FORBIDDENEXCEPTION);
			return false;
		}
	}

}
