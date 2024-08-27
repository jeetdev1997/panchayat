package com.panchayat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.panchayat.dto.PropertyDTO;
import com.panchayat.dto.ResponseDTO;
import com.panchayat.service.property.IPropertyService;
import com.panchayat.utils.PanchayatConstant;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private IPropertyService propertyService;

    @PostMapping
    public ResponseEntity<ResponseDTO> saveProperty(@RequestBody PropertyDTO propertyDTO) {
    	System.out.println(propertyDTO.toString());
        boolean isSaved = propertyService.saveProperty(propertyDTO);
        
        if(isSaved) {
        	 return ResponseEntity
                     .status(HttpStatus.OK)
                     .body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_201));
        }else {
        	return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_ERROR_SAVED));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateProperty(@RequestBody PropertyDTO propertyDTO) {
        boolean isUpdated = propertyService.updateProperty(propertyDTO);
        if(isUpdated) {
       	 return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_200));
       }else {
       	return ResponseEntity
                   .status(HttpStatus.EXPECTATION_FAILED)
                   .body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_417_UPDATE));
       }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteProperty(@RequestBody PropertyDTO propertyDTO) {
        boolean isDeleted = propertyService.deleteProperty(propertyDTO);
        if(isDeleted) {
          	 return ResponseEntity
                       .status(HttpStatus.OK)
                       .body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_200));
          }else {
          	return ResponseEntity
                      .status(HttpStatus.EXPECTATION_FAILED)
                      .body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_417_DELETE));
          }
    }

    @GetMapping("/owner/{ownerName}")
    public ResponseEntity<PropertyDTO> getPropertyByOwnerName(@PathVariable String ownerName) {
        PropertyDTO propertyDTO = propertyService.getProprtyByOwnerName(ownerName);
        return ResponseEntity.status(HttpStatus.OK).body(propertyDTO);
    }

    @GetMapping("/owner")
    public ResponseEntity<PropertyDTO> getPropertyByOwnerAndProperty(@RequestParam String ownerName, @RequestParam String proprtyNo) {
        PropertyDTO propertyDTO = propertyService.getProprtyByOwnerAndProperty(ownerName, proprtyNo);
        return ResponseEntity.status(HttpStatus.OK).body(propertyDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllProperties() {
        List<PropertyDTO> properties = propertyService.getAllProperty();
        
        if (properties.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No record found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(properties);
        }
    }
}
