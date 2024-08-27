package com.panchayat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "buildings")
public class Building {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "building_form")
    private String buildingForm;

    @Column(name = "building_floor")
    private String buildingFloor;

    @Column(name = "building_type")
    private String buildingType;

    @Column(name = "building_age")
    private double buildingAge;

    @Column(name = "building_area_length")
    private double buildingAreaLength;

    @Column(name = "building_area_width")
    private double buildingAreaWidth;

    @Column(name = "building_built_area_sq_ft")
    private double buildingBuiltAreaSqFt;

    @Column(name = "building_built_area_sq_mt")
    private double buildingBuiltAreaSqMt;
    
    @Column(name = "elect_tax_prev")
    private double electTaxPrev;
    @Column(name = "elect_tax_current")
    private double electTaxCurnt;                      
    @Column(name = "health_tax_prev")
    private double healthTaxPrev; 
    @Column(name = "health_tax_current")
    private double healthTaxCurnt;                     
    @Column(name = "water_tax_prev")
    private double waterTaxPrev; 
    @Column(name = "water_tax_current")
    private double waterTaxCurnt;                      
    @Column(name = "sp_water_tax_prev")
    private double spWaterTaxPrev;
    @Column(name = "sp_water_tax_current")
    private double spWaterTaxCurnt;  

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anu_kra_id", referencedColumnName = "anu_kra")
    private Property property;
    
    @Column(name = "home_tax_prev")
    private double homeTaxPrev; 
    @Column(name = "home_tax_current")
    private double homeTaxCurnt;
    
    
    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", buildingForm='" + buildingForm + '\'' +
                ", buildingFloor='" + buildingFloor + '\'' +
                ", buildingType='" + buildingType + '\'' +
                ", buildingAge=" + buildingAge +
                ", buildingAreaLength=" + buildingAreaLength +
                ", buildingAreaWidth=" + buildingAreaWidth +
                ", buildingBuiltAreaSqFt=" + buildingBuiltAreaSqFt +
                ", buildingBuiltAreaSqMt=" + buildingBuiltAreaSqMt +
                ", electTaxPrev=" + electTaxPrev +
                ", electTaxCurnt=" + electTaxCurnt +
                ", healthTaxPrev=" + healthTaxPrev +
                ", healthTaxCurnt=" + healthTaxCurnt +
                ", waterTaxPrev=" + waterTaxPrev +
                ", waterTaxCurnt=" + waterTaxCurnt +
                ", spWaterTaxPrev=" + spWaterTaxPrev +
                ", spWaterTaxCurnt=" + spWaterTaxCurnt +
                ",property =>" + property.toString() +
                '}';
    }
}
