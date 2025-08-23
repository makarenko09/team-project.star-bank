package org.skypro.star.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.skypro.star.model.RuleStatistic;

import java.util.List;

@Data
@AllArgsConstructor
public class RuleStatsResponse {
    @JsonProperty("stats")
    private List<RuleStatistic> statistics;
}
