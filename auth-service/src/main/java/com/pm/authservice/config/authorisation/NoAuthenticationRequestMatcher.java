package com.pm.authservice.config.authorisation;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class NoAuthenticationRequestMatcher implements RequestMatcher {
    private final Logger log = LoggerFactory.getLogger(NoAuthenticationRequestMatcher.class);
    private final RequestMappingHandlerMapping           requestMappingHandlerMapping;
    private final Map<RequestMappingInfo, HandlerMethod> noAuthenticationRequests;

    @Autowired
    public NoAuthenticationRequestMatcher(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        log.info("----------------->NoAuthenticationRequestMatcher");
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        Map<RequestMappingInfo, HandlerMethod> tmpSet = new HashMap<>();
        this.requestMappingHandlerMapping.getHandlerMethods().forEach((info, method) -> {
            if (method.hasMethodAnnotation(NoAuthentication.class)) {
                log.info("Method {} has no authentication requirements", info);
                tmpSet.put(info, method);
            }
        });
        noAuthenticationRequests = Collections.unmodifiableMap(tmpSet);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        // Spring 5.3+ requires the lookup path to be cached in request instead of resolving everytime in UrlPathHelper#getResolvedLookupPath
        this.requestMappingHandlerMapping.getUrlPathHelper().resolveAndCacheLookupPath(request);
        if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
            ServletRequestPathUtils.parseAndCache(request);
        }

        for (Map.Entry<RequestMappingInfo, HandlerMethod> noAuthenticationRequest : noAuthenticationRequests
                .entrySet()) {
            RequestMappingInfo matchingCondition = noAuthenticationRequest.getKey().getMatchingCondition(request);
            if (matchingCondition != null) {
                try {
                    Object handler = this.requestMappingHandlerMapping.getHandler(request).getHandler();
                    if (handler instanceof HandlerMethod) {
                        // Apart for the matching condition, we also make sure that it is the same method, e.g. in case
                        // url path can look alike but be handled by different methods
                        if (((HandlerMethod) handler).getMethod().equals(
                                noAuthenticationRequest.getValue().getMethod())) {
                            return true;
                        }
                    }
                }
                catch (Exception e) {
                    log.error("Could not get handler for request {}", request, e);
                }
            }
        }
        return false;
    }
}
