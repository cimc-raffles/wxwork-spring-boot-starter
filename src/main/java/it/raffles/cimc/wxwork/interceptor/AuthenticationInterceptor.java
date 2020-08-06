package it.raffles.cimc.wxwork.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.raffles.cimc.wxwork.assistant.Constant;
import it.raffles.cimc.wxwork.config.WxworkProperty;
import it.raffles.cimc.wxwork.config.WxworkProperty.TokenProperty;
import it.raffles.cimc.wxwork.exception.CustomJWTVerificationException;
import it.raffles.cimc.wxwork.exception.UnauthorizedException;

public class AuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	private WxworkProperty properties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		TokenProperty tokenProperty = this.properties.getToken();
		if(null == tokenProperty || null == tokenProperty.getEnable() || ! tokenProperty.getEnable())
			return true;

		String header = request.getHeader(Constant.AUTHORIZATION_HEADER);

		if (StringUtils.isEmpty(header))
			throw new UnauthorizedException();

		String token = header.replace(Constant.AUTHORIZATION_BEARER, "").trim();

		if (StringUtils.isEmpty(token))
			throw new UnauthorizedException();

		try {
			String secret = tokenProperty.getSecret();
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJwt = verifier.verify(token);
			List<String> audiences = decodedJwt.getAudience();

			if (CollectionUtils.isEmpty(audiences))
				throw new UnauthorizedException();

			request.setAttribute(Constant.ATTRIBUTE_USER, audiences.get(0));

		} catch (JWTVerificationException exception) {
			// Invalid signature/claims
			throw new CustomJWTVerificationException(exception);
		}

		return true;
	}

}
