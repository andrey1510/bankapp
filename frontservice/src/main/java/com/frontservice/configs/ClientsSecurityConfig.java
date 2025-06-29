package com.frontservice.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

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
    @Qualifier("exchangeRestTemplate")
    public RestTemplate exchangeRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "exchange-service", builder);
    }

    @Bean
    @Qualifier("transferRestTemplate")
    public RestTemplate transferRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "transfer-service", builder);
    }

    @Bean
    @Qualifier("cashRestTemplate")
    public RestTemplate cashRestTemplate(
        OAuth2AuthorizedClientManager authorizedClientManager,
        RestTemplateBuilder builder
    ) {
        return createSecuredRestTemplate(authorizedClientManager, "cash-service", builder);
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
