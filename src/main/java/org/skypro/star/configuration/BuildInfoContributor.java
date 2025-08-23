package org.skypro.star.configuration;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuildInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("build", Map.of(
                "name", "STAR Recommendation Service",
                "version", "0.0.1-SNAPSHOT"
        ));
    }
}