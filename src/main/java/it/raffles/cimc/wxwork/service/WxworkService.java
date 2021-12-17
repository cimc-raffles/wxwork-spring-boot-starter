package it.raffles.cimc.wxwork.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import it.raffles.cimc.wxwork.config.WxworkProperty;
import it.raffles.cimc.wxwork.config.WxworkProperty.TokenProperty;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

@Configuration
@EnableConfigurationProperties(WxworkProperty.class)
public class WxworkService {

	@Autowired
	private WxworkProperty properties;

	private static Map<Integer, WxCpMessageRouter> routers = new HashMap<>();
	private static Map<Integer, WxCpService> services = new HashMap<>();

	public static Map<Integer, WxCpMessageRouter> getRouters() {
		return routers;
	}

	public static WxCpService getServices(Integer agentId) {
		return services.get(agentId);
	}

	public static WxCpService getService() {
		return services.keySet().size() > 0 ? services.get(services.keySet().iterator().next()) : null;
	}

	public WxworkProperty getWxworkProperty() {
		return properties;
	}

	public TokenService getTokenService() {
		return new TokenService();
	}

	@PostConstruct
	public void init() {
		services = this.properties.getApps().stream().map(app -> {
			WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
			configStorage.setCorpId(this.properties.getId());
			configStorage.setAgentId(app.getId().intValue());
			configStorage.setCorpSecret(app.getSecret());
			configStorage.setAesKey(app.getAesKey());
			WxCpServiceImpl service = new WxCpServiceImpl();
			service.setWxCpConfigStorage(configStorage);
			routers.put(configStorage.getAgentId(), this.newRouter(service));
			return service;
		}).collect(Collectors.toMap(service -> service.getWxCpConfigStorage().getAgentId(), app -> app));
	}

	private WxCpMessageRouter newRouter(WxCpService wxCpService) {
		final WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);
		return newRouter;
	}

	public class TokenService {

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
	}
}