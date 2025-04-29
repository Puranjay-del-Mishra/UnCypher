package com.UnCypher.config;

import com.UnCypher.security.JwtHandshakeInterceptor;
import com.UnCypher.security.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final CustomHandshakeHandler customHandshakeHandler;
    @Value("#{'${cors.allowed.origins}'.split(',')}") List<String> allowedOrigins;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor,
                           CustomHandshakeHandler customHandshakeHandler) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
        this.customHandshakeHandler = customHandshakeHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(customHandshakeHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }
}

