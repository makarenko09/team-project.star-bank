package org.skypro.star.model.stat;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class StatsUser {
    @JsonUnwrapped
private final StatsUserTriggerRule stats;
}
