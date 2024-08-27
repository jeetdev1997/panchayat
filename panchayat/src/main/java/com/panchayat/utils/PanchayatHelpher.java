package com.panchayat.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;

import com.panchayat.dto.BuildingDTO;
import com.panchayat.dto.PropertyDTO;
import com.panchayat.dto.RateCardDTO;
import com.panchayat.entity.Building;
import com.panchayat.entity.Property;
import com.panchayat.entity.RateCard;

public class PanchayatHelpher {
	
	public static PropertyDTO convertProprtyEntity(Property property) {
		System.out.println("Inside convertProprtyEntity");
		PropertyDTO propertyDTO = new PropertyDTO();
//		List<Building> buildings = property.getBuildings();
//		System.out.println("buildings Inside convertProprtyEntity ==" + buildings);
		
		List<BuildingDTO> buildings = property.getBuildings().stream()
                .map(PanchayatHelpher::convertBuildingEntity)
                .collect(Collectors.toList());
		
        BeanUtils.copyProperties(property, propertyDTO);
        propertyDTO.setBuildings(buildings);
        return propertyDTO;
    }
	
	public static Property convertProprtyDTO(PropertyDTO propertyDTO) {
		Property property = new Property();
        BeanUtils.copyProperties(propertyDTO, property);
        return property;
    }
	
	public static Building convertBuildingDTO(BuildingDTO buildingDTO) {
		Building building = new Building();
        BeanUtils.copyProperties(buildingDTO, building);
        return building;
    }
	
	public static BuildingDTO convertBuildingEntity(Building building) {
		BuildingDTO buildingDTO = new BuildingDTO();
        BeanUtils.copyProperties(building, buildingDTO);
        return buildingDTO;
    }
	
	public static RateCard convertRateCardDTO(RateCardDTO rateCardDTO) {
		RateCard rateCard = new RateCard();
        BeanUtils.copyProperties(rateCardDTO, rateCard);
        return rateCard;
    }
	
	public static RateCardDTO convertRateCardEntity(RateCard rateCard) {
		RateCardDTO rateCardDTO = new RateCardDTO();
        BeanUtils.copyProperties(rateCard, rateCardDTO);
        return rateCardDTO;
    }
	
	public static String convertToMarathiNumber(String englishNumber) {
        // Mapping from English digits to Marathi digits
        String[] englishDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] marathiDigits = {"०", "१", "२", "३", "४", "५", "६", "७", "८", "९"};
        
        // Convert each English digit to its Marathi equivalent
        StringBuilder marathiNumber = new StringBuilder();
        for (char ch : englishNumber.toCharArray()) {
            if (Character.isDigit(ch)) {
                int digit = Character.getNumericValue(ch);
                marathiNumber.append(marathiDigits[digit]);
            } else {
                // If the character is not a digit, just append it as is
                marathiNumber.append(ch);
            }
        }
        
        return marathiNumber.toString();
    }
	public static String getCurrentDate() {
		 LocalDate currentDate = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        String formattedDate = currentDate.format(formatter);
	        return formattedDate;
	}

}
