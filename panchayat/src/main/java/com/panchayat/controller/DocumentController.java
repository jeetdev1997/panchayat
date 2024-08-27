package com.panchayat.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.panchayat.dto.PropertyDTO;
import com.panchayat.dto.ResponseDTO;
import com.panchayat.service.document.IDocumentService;
import com.panchayat.utils.PanchayatConstant;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = "*")
public class DocumentController {
	
	@Autowired
	IDocumentService documentService;
	
	@GetMapping("/download/assesment")
    public ResponseEntity<InputStreamResource> downloadExcel(@RequestBody PropertyDTO propertyDTO) throws IOException {
		 ByteArrayInputStream modifiedExcel = documentService.downloadAssesment(propertyDTO);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=assesment.xlsx");

	        ByteArrayInputStream newFileInputStream = new ByteArrayInputStream(modifiedExcel.readAllBytes());

	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(new InputStreamResource(newFileInputStream));
    }
	
	@GetMapping("/download/namuna/eight")
    public ResponseEntity<InputStreamResource> downloadNamunaEight() throws IOException {
		 ByteArrayInputStream modifiedExcel = documentService.downloadAmong(PanchayatConstant.NAMUNA_EIGHT);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=NamunaEight.xlsx");
	        
	        boolean hasConetent = hasContent(modifiedExcel);
	        System.out.println("has data ==> " + hasConetent);
	        
	        
	       

	        //ByteArrayInputStream newFileInputStream = new ByteArrayInputStream(modifiedExcel.readAllBytes());

	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(new InputStreamResource(modifiedExcel));
    }
	
	@GetMapping("download/namuna/nine")
    public ResponseEntity<InputStreamResource> downloadNamunaNine() throws IOException {
		 ByteArrayInputStream modifiedExcel = documentService.downloadAmong(PanchayatConstant.NAMUNA_NINE);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=NamunaNine.xlsx");

	        ByteArrayInputStream newFileInputStream = new ByteArrayInputStream(modifiedExcel.readAllBytes());

	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(new InputStreamResource(newFileInputStream));
    }
	
	@GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam Long anuKramank, @RequestParam Long id) throws IOException {
        // If no content is provided, use a default value or an empty string.

        byte[] pdfBytes = documentService.generatePdf(anuKramank, id);

        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    
    // for uploading excel 
    
    @PostMapping("/upload/namuna/eight")
    public ResponseEntity<ResponseDTO> uploadExcelNamunaEight(@RequestParam("file") MultipartFile file) {
    	if(file == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(PanchayatConstant.BAD_REQUEST, PanchayatConstant.FILE_UPLOAD_ERROR));
    	}
    	
    	try {
    		boolean isUpload =  documentService.uploadAmongExcel(file, PanchayatConstant.NAMUNA_EIGHT);
    		if(isUpload) {
    			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_200_FILE_UPLOAD));
    		}else {
        		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_417_FILE_UPLOAD));
    		}
            
        } catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(PanchayatConstant.STATUS_500, PanchayatConstant.MESSAGE_500));
        }
    }
    
    @PostMapping("/upload/namuna/nine")
    public ResponseEntity<ResponseDTO> uploadExcelNamunaNine(@RequestParam("file") MultipartFile file) {
    	if(file == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(PanchayatConstant.BAD_REQUEST, PanchayatConstant.FILE_UPLOAD_ERROR));
    	}
    	
    	try {
    		System.out.println("inside controller for uploading file");
    		boolean isUpload =  documentService.uploadAmongExcel(file, PanchayatConstant.NAMUNA_NINE);
    		if(isUpload) {
    			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(PanchayatConstant.STATUS_200, PanchayatConstant.MESSAGE_200_FILE_UPLOAD));
    		}else {
        		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(PanchayatConstant.STATUS_417, PanchayatConstant.MESSAGE_417_FILE_UPLOAD));
    		}
            
        } catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(PanchayatConstant.STATUS_500, PanchayatConstant.MESSAGE_500));
        }
    }
    
    public static boolean hasContent(ByteArrayInputStream stream) throws IOException {
        // Save the current position
        int mark = stream.available();

        // Read the stream to check if it has content
        boolean hasContent = mark > 0;

        // Reset the stream to the saved position
        stream.reset();

        return hasContent;
    }
	
}
