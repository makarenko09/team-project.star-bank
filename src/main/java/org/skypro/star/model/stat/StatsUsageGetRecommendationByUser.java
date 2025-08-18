package org.skypro.star.model.stat;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsUsageGetRecommendationByUser {
    @JsonUnwrapped
private final StatUserTriggerRule[] stats;

}
