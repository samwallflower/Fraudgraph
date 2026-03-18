package com.fraudgraph.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerdictDetailDto {
    private String entityType;
    private String entityValue;
    private int occurrenceCount;
    private String explanation;
}
