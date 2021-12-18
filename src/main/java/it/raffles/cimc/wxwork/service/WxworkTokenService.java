package it.raffles.cimc.wxwork.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.raffles.cimc.wxwork.config.WxworkProperty;
import it.raffles.cimc.wxwork.config.WxworkProperty.TokenProperty;

@Configuration
@EnableConfigurationProperties(WxworkProperty.class)
public class WxworkTokenService {

	@Autowired
	private WxworkProperty properties;

	public DecodedJWT getDecodedJWT(String token, TokenProperty tokenProperty) {
		String secret = tokenProperty.getSecret();
		Algorithm algorithm = Algorithm.HMAC256(secret);
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJwt = verifier.verify(token);
		return decodedJwt;
	}

	public DecodedJWT getDecodedJWT(String token) {
		TokenProperty tokenProperty = getTokenProperty();
		return null == tokenProperty ? null : getDecodedJWT(token, tokenProperty);
	}

	public String encode(String... userIds) {
		TokenProperty tokenProperty = getTokenProperty();
		if (null == tokenProperty)
			return null;
		String secret = tokenProperty.getSecret();
		Algorithm algorithm = Algorithm.HMAC256(secret);
		return JWT.create().withIssuedAt(new Date()).withAudience(userIds).sign(algorithm);
	}

	private TokenProperty getTokenProperty() {
		TokenProperty tokenProperty = properties.getToken();
		if (null == tokenProperty)
			return null;
		return tokenProperty;
	}

	public WxworkProperty getWxworkProperty() {
		return properties;
	}
}
