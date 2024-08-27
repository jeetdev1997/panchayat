package com.panchayat.service.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.panchayat.dto.PropertyDTO;

public interface IDocumentService {
	
	ByteArrayInputStream downloadAssesment(PropertyDTO dto) throws IOException;
	byte[] generatePdf(Long anukramank, Long id) throws IOException;
	//ByteArrayInputStream downloadExcelAmong() throws IOException;
	ByteArrayInputStream downloadAmong(String namuna) throws IOException;
	boolean uploadAmongExcel(MultipartFile file, String namuna) throws Exception;

}
