package com.panchayat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rate_cards")
public class RateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ga")
    private double ga;

    @Column(name = "gb")
    private double gb;

    @Column(name = "gc")
    private double gc;

    @Column(name = "gd")
    private double gd;

    @Column(name = "electric_rate")
    private double electricRate;

    @Column(name = "health_rate")
    private double healthRate;

    @Column(name = "normal_water_rate")
    private double normalWaterRate;

    @Column(name = "special_water_rate")
    private double specialWaterRate;

    @Column(name = "ra")
    private double ra;

    @Column(name = "rb")
    private double rb;

    @Column(name = "rc")
    private double rc;

    @Column(name = "rd")
    private double rd;

    @Column(name = "khadki")
    private double khadki;

    @Column(name = "mandwa")
    private double mandwa;

    @Column(name = "lakhmapur")
    private double lakhmapur;
    
    @Column(name = "bhansoli")
    private double bhansoli;
    
    @Column(name = "kinhi")
    private double kinhi;

    @Column(name = "previous_year")
    private double previousYear;

    @Column(name = "current_year")
    private double currentYear;
    
    @Column(name = "gaav_khaali_jagah")
    private double gaavthaanKhaaliJaaga;
    
    @Column(name = "gaavthaan")
    private double gaavthaan;
    
    @Column(name = "karacha_dar_emarat")
    private double karachaDarEmarat;
    
    @Column(name = "karacha_dar_khaali_jaaga")
    private double karachaDarKhaaliJaaga;
    
    
    
    
}
