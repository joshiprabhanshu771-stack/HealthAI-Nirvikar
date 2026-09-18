package com.healthai.entity;

import com.healthai.constants.bmi.BMICategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bmi_records",
        indexes = {
                @Index(
                        name = "idx_bmi_user_date",
                        columnList = "user_id, calculated_at"
                )
        }
)
public class BMIRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bmi_user")
    )
    private User user;

    @Column(
            name = "height_cm",
            nullable = false,
            precision = 6,
            scale = 2
    )
    private BigDecimal heightCm;

    @Column(
            name = "weight_kg",
            nullable = false,
            precision = 6,
            scale = 2
    )
    private BigDecimal weightKg;

    @Column(
            name = "bmi",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal bmi;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "bmi_category",
            nullable = false,
            length = 30
    )
    private BMICategory bmiCategory;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public BMIRecord() {
    }

    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public BMICategory getBmiCategory() {
        return bmiCategory;
    }

    public void setBmiCategory(BMICategory bmiCategory) {
        this.bmiCategory = bmiCategory;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}