package com.panchayat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panchayat.dto.RateCardDTO;
import com.panchayat.dto.ResponseDTO;
import com.panchayat.service.ratecard.IRateCardService;
import com.panchayat.utils.PanchayatConstant;

@RestController
@RequestMapping("/api/ratecard")
@CrossOrigin(origins = "*")
public class RateCardController {
	
	@Autowired
	IRateCardService cardService;
	 @PostMapping
	    public ResponseEntity<ResponseDTO> saveRateCard(@RequestBody RateCardDTO rateCardDTO) {
		 
	        boolean isSaved = cardService.save(rateCardDTO);
	        
	        if(isSaved) {
	        	 return ResponseEntity
	                     .status(HttpStatus.OK)
	                     .body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_201_RATE));
	        }else {
	        	return ResponseEntity
	                    .status(HttpStatus.EXPECTATION_FAILED)
	                    .body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_ERROR_SAVED));
	        }
	    }
	 
	 @GetMapping
	    public ResponseEntity<?> getRateCard() {
	        RateCardDTO cardDTO = cardService.getAll();
	        
	        if (cardDTO == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No record found");
	        } else {
	            return ResponseEntity.status(HttpStatus.OK).body(cardDTO);
	        }
	    }

}
