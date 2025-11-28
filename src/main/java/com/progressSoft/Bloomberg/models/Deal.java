package com.progressSoft.Bloomberg.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Deal {

    @Id
    @Column(name = "deal_unique_id", nullable = false, length = 50)
    private String dealUniqueId; // clé primaire

    @Column(name = "deal_timestamp", nullable = false)
    private LocalDateTime dealTimestamp;

    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

    @Column(name = "deal_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal dealAmount;
}
