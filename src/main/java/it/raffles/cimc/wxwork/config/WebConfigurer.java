package it.raffles.cimc.wxwork.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.raffles.cimc.wxwork.interceptor.AuthenticationInterceptor;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

	@Autowired
	private WxworkProperty properties;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration registration = registry.addInterceptor(authenticationInterceptor());
		registration.addPathPatterns("/**");
		if (null != this.properties && null != this.properties.getToken()
				&& null != this.properties.getToken().getExclude()
				&& 0 < this.properties.getToken().getExclude().length)
			registration.excludePathPatterns(this.properties.getToken().getExclude());
	}

	@Bean
	HandlerInterceptor authenticationInterceptor() {
		return new AuthenticationInterceptor();
	}
}

class NonInterceptor implements HandlerInterceptor {
}