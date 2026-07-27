package com.pm.authservice.infrastructure.config.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

// Relies on CachingHttpFilter having already wrapped the request/response so the bodies are readable here.
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(HttpLoggingInterceptor.class);

	private static final Pattern SENSITIVE_FIELD_PATTERN = Pattern.compile(
			"\"(password|token|accessToken|refreshToken|secret)\"\\s*:\\s*\"[^\"]*\"",
			Pattern.CASE_INSENSITIVE
	);
	private static final String REDACTED_REPLACEMENT = "\"$1\":\"***\"";

	@Override
	public void afterCompletion(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull Object handler,
			@Nullable Exception ex
	) {
		if (!log.isDebugEnabled()) {
			return;
		}
		String requestBody = redact(bodyOf(request));
		String responseBody = redact(bodyOf(response));
		log.debug(
				"{} {} -> {} | request: {} | response: {}",
				request.getMethod(),
				request.getRequestURI(),
				response.getStatus(),
				requestBody,
				responseBody
		);
	}

	private String bodyOf(HttpServletRequest request) {
		if (request instanceof ContentCachingRequestWrapper wrapper) {
			return new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
		}
		return "";
	}

	private String bodyOf(HttpServletResponse response) {
		if (response instanceof ContentCachingResponseWrapper wrapper) {
			return new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
		}
		return "";
	}

	private String redact(String body) {
		return SENSITIVE_FIELD_PATTERN.matcher(body).replaceAll(REDACTED_REPLACEMENT);
	}
}
