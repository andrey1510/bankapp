package com.exchangeservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateDto {
    private String title;
    private String currency;
    private BigDecimal value;
    private LocalDateTime timestamp;
}

