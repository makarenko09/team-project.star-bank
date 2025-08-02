package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


public record Recommendation(String name, UUID id, String text) {
}