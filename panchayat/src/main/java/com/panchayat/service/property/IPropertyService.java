package com.panchayat.service.property;

import java.util.List;

import com.panchayat.dto.PropertyDTO;

public interface IPropertyService {
	
	boolean saveProperty(PropertyDTO propertyDTO);
	boolean updateProperty(PropertyDTO propertyDTO);
	boolean deleteProperty(PropertyDTO propertyDTO);
	PropertyDTO getProprtyByOwnerName(String ownerName);
	PropertyDTO getProprtyByOwnerAndProperty(String ownerName, String propertyNo);
	List<PropertyDTO> getAllProperty();

}
