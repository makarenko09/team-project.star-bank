package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatistic {
    private UUID ruleId;
    private Integer count;
}
