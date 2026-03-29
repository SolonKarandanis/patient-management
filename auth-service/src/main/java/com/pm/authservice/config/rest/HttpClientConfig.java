package com.pm.authservice.config.rest;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.HeaderElement;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * - Supports both HTTP and HTTPS
 * - Uses a connection pool to re-use connections and save overhead of creating connections.
 * - Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)
 * - Starts an idle connection monitor to continuously clean up stale connections.
 */
@Configuration
public class HttpClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);

    // Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT = 30000;

    // The timeout when requesting a connection from the connection manager.
    private static final int REQUEST_TIMEOUT = 30000;

    // The timeout for waiting for data
    private static final int SOCKET_TIMEOUT = 60000;

    private static final int MAX_TOTAL_CONNECTIONS = 50;
    //    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> chain.length == 1)
                    .build();

            return PoolingHttpClientConnectionManagerBuilder.create()
                    .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext))
                    .setMaxConnTotal(MAX_TOTAL_CONNECTIONS)
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setConnectTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT))
                            .setSocketTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT))
                            .build())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Pooling Connection Manager Initialisation failure because of {}", e.getMessage(), e);
            return PoolingHttpClientConnectionManagerBuilder.create().build();
        }
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(){
        return new ConnectionKeepAliveStrategy() {
            @Override
            public TimeValue getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                final Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);
                while (it.hasNext()) {
                    final HeaderElement he = it.next();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return TimeValue.ofSeconds(Long.parseLong(value));
                        } catch(final NumberFormatException ignore) {
                            LOGGER.error("NumberFormatException ");
                        }
                    }
                }
                final HttpClientContext clientContext = HttpClientContext.cast(context);
                final RequestConfig requestConfig = clientContext.getRequestConfig();
                return requestConfig.getConnectionKeepAlive();
            }
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(REQUEST_TIMEOUT))
                .setResponseTimeout(Timeout.ofMilliseconds(SOCKET_TIMEOUT))
                .build();

        return HttpClients.custom()
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (connectionManager != null) {
                        LOGGER.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                        connectionManager.closeExpired();
                        connectionManager.closeIdle(TimeValue.ofSeconds(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS));
                    } else {
                        LOGGER.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}", e.getMessage(), e);
                }
            }
        };
    }

}
