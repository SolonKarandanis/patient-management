package com.pm.authservice.infrastructure.config.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

// Runs before the Spring Security filter chain so every downstream filter/interceptor sees the wrapped request/response.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CachingHttpFilter extends OncePerRequestFilter {

	// Caps how many request bytes get buffered in memory; bodies past this limit are simply not cached.
	private static final int MAX_CACHED_BODY_BYTES = 10 * 1024;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_CACHED_BODY_BYTES);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
		try {
			filterChain.doFilter(wrappedRequest, wrappedResponse);
		} finally {
			// copyBodyToResponse() must run even on exceptions, otherwise the buffered body never reaches the client.
			wrappedResponse.copyBodyToResponse();
		}
	}
}
