package com.pm.authservice.infrastructure.config.web;

import com.pm.authservice.infrastructure.config.web.interceptor.HttpLoggingInterceptor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final HttpLoggingInterceptor httpLoggingInterceptor;

	public WebMvcConfig(HttpLoggingInterceptor httpLoggingInterceptor) {
		this.httpLoggingInterceptor = httpLoggingInterceptor;
	}

	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		registry.addInterceptor(httpLoggingInterceptor);
	}
}
