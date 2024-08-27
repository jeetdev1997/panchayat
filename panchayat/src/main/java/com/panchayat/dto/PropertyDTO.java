package com.panchayat.dto;

import java.util.List;

import lombok.Data;

@Data
public class PropertyDTO {
	
	private Long id;
	private Long anuKra;
    private String wardNo;
    private String propertyNo;
    private String measlesNo;
    private String plotNo;
    private String mobileNo;

    private String ownerName;
    private String ownerWifeName;
    private String ownerAddress;
    private String renteeName;

    List<BuildingDTO> buildings;

    private String openPlotType;
    private double areaLengthSqFt;
    private double areaWidthSqFt;
    private double floorSpaceSqFt;
    private double floorSpaceSqMt;

    private boolean isPersonalTaps;
    private boolean isPublicTaps;
    private boolean isWaterOtherSource;

    private boolean isToiletAvailable;

    

}
