package com.panchayat.service.property;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.panchayat.dto.BuildingDTO;
import com.panchayat.dto.PropertyDTO;
import com.panchayat.repository.BuildingRepository;
import com.panchayat.repository.PropertyRepository;
import com.panchayat.utils.PanchayatHelpher;

import jakarta.transaction.Transactional;

import com.panchayat.entity.Building;
import com.panchayat.entity.Property;
import com.panchayat.exception.ResourceNotFoundException;

@Service
public class PropertyServiceImpl implements IPropertyService {

    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean saveProperty(PropertyDTO propertyDTO) {
    	boolean isSaved = false;
        Property property = PanchayatHelpher.convertProprtyDTO(propertyDTO);
        Property savedProperty =  propertyRepository.save(property);
        
        List<BuildingDTO> buildingDTOs = propertyDTO.getBuildings();
        
        List<Building> buildings = buildingDTOs.stream()
                .map(PanchayatHelpher::convertBuildingDTO)
                .collect(Collectors.toList());
        
        buildings.forEach(building -> building.setProperty(savedProperty));
        
       List<Building> savedBuildings =  buildingRepository.saveAll(buildings);
       if(savedBuildings != null && savedBuildings.size() > 0) {
    	   isSaved = true;
       }
        return isSaved;
    }

    @Override
    public boolean updateProperty(PropertyDTO propertyDTO) {
    	boolean isUpdate = false;
        Property property = PanchayatHelpher.convertProprtyDTO(propertyDTO);
        Property updatedProperty =  propertyRepository.save(property);
        
        List<BuildingDTO> buildingDTOs = propertyDTO.getBuildings();
        
        List<Building> buildings = buildingDTOs.stream()
                .map(PanchayatHelpher::convertBuildingDTO)
                .collect(Collectors.toList());
        
        buildings.forEach(building -> building.setProperty(updatedProperty));
        
        List<Building> updatedBuildings = buildingRepository.saveAll(buildings);
        
        if(updatedBuildings != null && updatedBuildings.size() > 0) {
     	   isUpdate = true;
        }
        return isUpdate;
    }

    @Override
    public boolean deleteProperty(PropertyDTO propertyDTO) {
    	boolean isDeleted = false;
    	
    	Property property =	propertyRepository.findByOwnerNameAndPropertyNo(propertyDTO.getOwnerName(), propertyDTO.getPropertyNo()).orElseThrow(
    			() -> new ResourceNotFoundException("Property -> ", propertyDTO.getPropertyNo(), propertyDTO.getOwnerName()));
        
        propertyRepository.delete(property);
        isDeleted = true;
        return isDeleted;
    }

    @Override
    public PropertyDTO getProprtyByOwnerName(String ownerName) {
        Property property = propertyRepository.findByOwnerName(ownerName).orElseThrow(
    			() -> new ResourceNotFoundException("Property -> ", ownerName, ""));
        return PanchayatHelpher.convertProprtyEntity(property);
    }

    @Override
    public PropertyDTO getProprtyByOwnerAndProperty(String ownerName, String propertyNo) {
    	Property property =	propertyRepository.findByOwnerNameAndPropertyNo(ownerName, propertyNo).orElseThrow(
    			() -> new ResourceNotFoundException("Property -> ", propertyNo, ownerName));
        
        return PanchayatHelpher.convertProprtyEntity(property);
    }

    @Override
    public List<PropertyDTO> getAllProperty() {
        // Step 1: Fetch all properties and buildings
        List<Property> properties = propertyRepository.findAll();
        List<Building> buildings = buildingRepository.findAll();        
        Map<Long, List<Building>> propertyIdToBuildingsMap = buildings.stream()
                .filter(buildingDTO -> buildingDTO.getProperty() != null && buildingDTO.getProperty().getAnuKra() != null)
                .collect(Collectors.groupingBy(buildingDTO -> buildingDTO.getProperty().getAnuKra()));
                
        properties.forEach(propertyDTO -> {
            List<Building> associatedBuildings = propertyIdToBuildingsMap.get(propertyDTO.getAnuKra());
            if (associatedBuildings != null) {
                propertyDTO.setBuildings(associatedBuildings); // Assuming PropertyDTO has a setBuildings method
            }
        });
        
        List<PropertyDTO> propertyDTOs = properties.stream()
                .map(PanchayatHelpher::convertProprtyEntity) // Assuming this method converts Property to PropertyDTO
                .collect(Collectors.toList());
        return propertyDTOs;
    }

}
