package org.skypro.star.model.stat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StatUserTriggerRule {
    @JsonProperty("rule_id")
    private final UUID uuid;
    @JsonProperty("count")
    private final Integer incrementingTrigger;
}
