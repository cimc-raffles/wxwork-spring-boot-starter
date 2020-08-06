package it.raffles.cimc.wxwork.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.raffles.cimc.wxwork.config.WxworkProperty.TokenProperty;
import it.raffles.cimc.wxwork.interceptor.AuthenticationInterceptor;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

	@Autowired
	private WxworkProperty properties;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration registration = registry.addInterceptor(authenticationInterceptor());
		registration.addPathPatterns("/**");
		if (null != this.properties && null != this.properties.getToken())
			registration.excludePathPatterns(this.properties.getToken().getExclude());
	}

	@Bean
	public HandlerInterceptor authenticationInterceptor() {
		TokenProperty tokenProperty = this.properties.getToken();
		return null == tokenProperty || null == tokenProperty.getEnable() || !tokenProperty.getEnable()
				? new NonInterceptor()
				: new AuthenticationInterceptor();
	}
}

class NonInterceptor implements HandlerInterceptor {
}