package com.genesys.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.aspectj.weaver.ast.Not;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


//catches here in the start if OAuth secrets are null
@ConfigurationProperties(prefix = "genesys")
@Validated
public record GenesysProperties(@NotBlank String clientId,
                                @NotBlank String clientSecret,
                                @NotBlank String region,
                                @NotBlank String division) {



}
