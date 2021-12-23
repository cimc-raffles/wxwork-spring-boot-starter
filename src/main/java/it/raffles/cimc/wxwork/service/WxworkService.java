package it.raffles.cimc.wxwork.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import it.raffles.cimc.wxwork.config.WxworkProperty;
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

	@PostConstruct
	public void init() {
		services = this.properties.getApps().stream().map(app -> {
			WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
			configStorage.setCorpId(this.properties.getId());
			configStorage.setAgentId(app.getId().intValue());
			configStorage.setCorpSecret(app.getSecret());
			configStorage.setAesKey(app.getAesKey());
			configStorage.setOauth2redirectUri(app.getRedirectUrl());
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
}