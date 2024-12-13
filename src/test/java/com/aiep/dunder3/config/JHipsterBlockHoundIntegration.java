package com.aiep.dunder3.config;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public class JHipsterBlockHoundIntegration implements BlockHoundIntegration {

    @Override
    public void applyTo(BlockHound.Builder builder) {
        builder.allowBlockingCallsInside("org.springframework.validation.beanvalidation.SpringValidatorAdapter", "validate");
        builder.allowBlockingCallsInside("com.aiep.dunder3.service.MailService", "sendEmailFromTemplate");
        builder.allowBlockingCallsInside("com.aiep.dunder3.security.DomainUserDetailsService", "createSpringSecurityUser");
        builder.allowBlockingCallsInside("io.r2dbc.mssql.client.ssl.TdsSslHandler", "createSslHandler");
        builder.allowBlockingCallsInside("org.springframework.web.reactive.result.method.InvocableHandlerMethod", "invoke");
        builder.allowBlockingCallsInside("org.springdoc.core.service.OpenAPIService", "build");
        builder.allowBlockingCallsInside("org.springdoc.core.service.AbstractRequestService", "build");
        // jhipster-needle-blockhound-integration - JHipster will add additional gradle plugins here
    }
}
