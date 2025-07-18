package com.cashservice.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class ClientsSecurityConfig {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository) {

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService(clientRegistrationRepository));

        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
        ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    @Qualifier("accountRestTemplate")
    public RestTemplate accountRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "account-service", builder);
    }

    @Bean
    @Qualifier("blockerRestTemplate")
    public RestTemplate blockerRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "blocker-service", builder);
    }

    @Bean
    @Qualifier("notificationRestTemplate")
    public RestTemplate notificationRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "notification-service", builder);
    }

    private RestTemplate createSecuredRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        String registrationId,
        RestTemplateBuilder builder
    ) {

        RestTemplate restTemplate = builder.build();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationId)
                .principal(registrationId)
                .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
            if (authorizedClient == null) {
                throw new IllegalStateException("Failed to authorize client: " + registrationId);
            }

            request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }

}