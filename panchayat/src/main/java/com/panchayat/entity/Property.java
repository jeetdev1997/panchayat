package com.panchayat.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "properties")
public class Property {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anu_kra", nullable = false)
    private Long anuKra;

    @Column(name = "ward_no")
    private String wardNo;

    @Column(name = "property_no")
    private String propertyNo;

    @Column(name = "measles_no")
    private String measlesNo;

    @Column(name = "plot_no")
    private String plotNo;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_wife_name")
    private String ownerWifeName;

    @Column(name = "owner_address")
    private String ownerAddress;

    @Column(name = "rentee_name")
    private String renteeName;

    

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Building> buildings;

    @Column(name = "open_plot_type")
    private String openPlotType;

    @Column(name = "area_length_sq_ft")
    private double areaLengthSqFt;

    @Column(name = "area_width_sq_ft")
    private double areaWidthSqFt;

    @Column(name = "floor_space_sq_ft")
    private double floorSpaceSqFt;

    @Column(name = "floor_space_sq_mt")
    private double floorSpaceSqMt;

    @Column(name = "is_personal_taps")
    private boolean isPersonalTaps;

    @Column(name = "is_public_taps")
    private boolean isPublicTaps;

    @Column(name = "is_water_other_source")
    private boolean isWaterOtherSource;

    @Column(name = "is_toilet_available")
    private boolean isToiletAvailable;
    
    
    
    @Override
    public String toString() {
        return "Property{" +
                "anuKra=" + anuKra +
                ", wardNo='" + wardNo + '\'' +
                ", propertyNo='" + propertyNo + '\'' +
                ", measlesNo='" + measlesNo + '\'' +
                ", plotNo='" + plotNo + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerWifeName='" + ownerWifeName + '\'' +
                ", ownerAddress='" + ownerAddress + '\'' +
                ", renteeName='" + renteeName + '\'' +
                ", openPlotType='" + openPlotType + '\'' +
                ", areaLengthSqFt=" + areaLengthSqFt +
                ", areaWidthSqFt=" + areaWidthSqFt +
                ", floorSpaceSqFt=" + floorSpaceSqFt +
                ", floorSpaceSqMt=" + floorSpaceSqMt +
                ", isPersonalTaps=" + isPersonalTaps +
                ", isPublicTaps=" + isPublicTaps +
                ", isWaterOtherSource=" + isWaterOtherSource +
                ", isToiletAvailable=" + isToiletAvailable +
                '}';
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return anuKra == property.getAnuKra() && Objects.equals(anuKra, property.anuKra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anuKra);
    }
}
