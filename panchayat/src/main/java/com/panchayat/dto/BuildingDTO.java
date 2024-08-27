package com.panchayat.dto;


import lombok.Data;

@Data
public class BuildingDTO {
	private long id;
	private String buildingForm;
    private String buildingFloor;
    private String buildingType;
    private double buildingAge;
    private double buildingAreaLength;
    private double buildingAreaWidth;
    private double buildingBuiltAreaSqFt;
    private double buildingBuiltAreaSqMt;
    private double homeTaxPrev;                        
    private double homeTaxCurnt;                      
    private double electTaxPrev;                       
    private double electTaxCurnt;                      
    private double healthTaxPrev;                      
    private double healthTaxCurnt;                     
    private double waterTaxPrev;                   
    private double waterTaxCurnt;                      
    private double spWaterTaxPrev;                     
    private double spWaterTaxCurnt;
    
    //private Property property;


}
