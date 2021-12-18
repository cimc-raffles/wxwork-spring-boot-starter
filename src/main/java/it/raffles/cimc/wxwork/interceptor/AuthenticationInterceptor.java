package it.raffles.cimc.wxwork.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.raffles.cimc.wxwork.assistant.Constant;
import it.raffles.cimc.wxwork.config.WxworkProperty;
import it.raffles.cimc.wxwork.config.WxworkProperty.TokenProperty;
import it.raffles.cimc.wxwork.exception.CustomJWTVerificationException;
import it.raffles.cimc.wxwork.exception.UnauthorizedException;
import it.raffles.cimc.wxwork.service.WxworkTokenService;

public class AuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	private WxworkTokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println(null==tokenService);
		WxworkProperty properties = tokenService.getWxworkProperty();
		TokenProperty tokenProperty = properties.getToken();
		if (null == tokenProperty || null == tokenProperty.getEnable() || !tokenProperty.getEnable())
			return true;

		String header = request.getHeader(Constant.AUTHORIZATION_HEADER);

		if (!StringUtils.hasText(header))
			throw new UnauthorizedException();

		String token = header.replace(Constant.AUTHORIZATION_BEARER, "").trim();

		if (!StringUtils.hasText(token))
			throw new UnauthorizedException();

		try {
			DecodedJWT decodedJwt = tokenService.getDecodedJWT(token, tokenProperty);
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
