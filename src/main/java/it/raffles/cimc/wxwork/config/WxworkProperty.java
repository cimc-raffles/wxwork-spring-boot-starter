package it.raffles.cimc.wxwork.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wxwork")
public class WxworkProperty {

	private String id;

	private TokenProperty token;

	private List<AppProperty> apps;

	public static class TokenProperty {

		private Boolean enable;

		private String secret;

		private String[] exclude;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String[] getExclude() {
			return exclude;
		}

		public void setExclude(String[] exclude) {
			this.exclude = exclude;
		}
	}

	public static class AppProperty {

		private Long id;

		private String name;

		private String secret;

		private String aesKey;

		private String redirectUrl;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String getAesKey() {
			return aesKey;
		}

		public void setAesKey(String aesKey) {
			this.aesKey = aesKey;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TokenProperty getToken() {
		return token;
	}

	public void setToken(TokenProperty token) {
		this.token = token;
	}

	public List<AppProperty> getApps() {
		return apps;
	}

	public void setApps(List<AppProperty> apps) {
		this.apps = apps;
	}

}