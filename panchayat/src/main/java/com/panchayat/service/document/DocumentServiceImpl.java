package com.panchayat.service.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.panchayat.dto.BuildingDTO;
import com.panchayat.dto.PropertyDTO;
import com.panchayat.dto.RateCardDTO;
import com.panchayat.entity.Building;
import com.panchayat.entity.Property;
import com.panchayat.entity.RateCard;
import com.panchayat.exception.ResourceNotFoundException;
import com.panchayat.repository.BuildingRepository;
import com.panchayat.repository.PropertyRepository;
import com.panchayat.repository.RateCardRepository;
import com.panchayat.utils.PanchayatConstant;
import com.panchayat.utils.PanchayatHelpher;

@Service
public class DocumentServiceImpl implements IDocumentService {
	
	@Autowired
	PropertyRepository propertyRepository;
	
	@Autowired
	BuildingRepository buildingRepository;
	
	@Autowired
	RateCardRepository cardRepository;
	
    @Value("${excel.file.path}")
    private String filePath;
    
    @Value("${excel1.file.path}")
    private String amongFilePath;

	@Override
	public ByteArrayInputStream downloadAssesment(PropertyDTO dto) throws IOException {
		
		if(dto == null) {
			throw new ResourceNotFoundException("Property", "", "");
		}
//		
		 Property property = propertyRepository.findByOwnerNameAndPropertyNo(dto.getOwnerName(), dto.getPropertyNo()).orElseThrow(
	                () -> new ResourceNotFoundException("Property", "OwnerName", dto.getOwnerName().toString())
			        );
		 PropertyDTO propertyDTO = PanchayatHelpher.convertProprtyEntity(property);
		 FileInputStream inputStream = new FileInputStream(new File(filePath));
	        Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet sheet = workbook.getSheetAt(0);
	        
	        Row rowFirst = sheet.getRow(0);
	        if(rowFirst == null) {
	        	rowFirst = sheet.createRow(0);
	        }
	        Cell cellFirst = rowFirst.getCell(0);
	        if(cellFirst == null) {
	        	cellFirst = rowFirst.createCell(0);
	        }
	        if(cellFirst.getCellType() == CellType.STRING) {
	        	String value = cellFirst.getStringCellValue();
	        	if(value.contains("१२/१०/२०२३")) {
	        		String currentDate = PanchayatHelpher.getCurrentDate();
	        		 String formattedString = PanchayatHelpher.convertToMarathiNumber(currentDate);
	        		 String updatedString = value.replace("१२/१०/२०२३", formattedString);
	        		 cellFirst.setCellValue(updatedString);
	        	}
	        	
	        }
	        
	        

	        // Modify the Excel file
	        Row rowThired = sheet.getRow(3);
;
	        if(rowThired == null) {
	        	rowThired = sheet.createRow(3);
	        }
	        Cell cellThired = rowThired.getCell(0);
	        if(cellThired == null) {
	        	cellThired = rowThired.createCell(0);
	        }
	        
	        if(cellThired.getCellType() == CellType.STRING) {
	        	String value = cellThired.getStringCellValue();
	        	
	        }
	        
	        Row rowEight = sheet.getRow(8);
	        if(rowEight != null) {
	        	 Iterator<Cell> cellIterator = rowEight.cellIterator();
	             while (cellIterator.hasNext()) {
	                 Cell cell = cellIterator.next();
	                 processCell(cell);
	             }
	        }
	       

	        // Write the modified Excel file to a new file
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();

	        // Return the modified file as ByteArrayInputStr
	        
	        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
	        
	        // new file path 
	        String userDir = System.getProperty("user.dir");
	        String newFilePath = userDir + "/downloadedFile3.xlsx";
	        saveModifiedExcel(arrayInputStream, newFilePath);
	        return arrayInputStream;
	}
	
	
	public ByteArrayInputStream downloadAmong(String namuna) throws IOException {
	    List<Property> properties = propertyRepository.findAll();
	    
	   RateCard rateCard = cardRepository.findById(1l).orElseThrow(() -> new ResourceNotFoundException("Rate Card", "", ""));
	    
	   RateCardDTO cardDTO = PanchayatHelpher.convertRateCardEntity(rateCard);
	    if (properties == null || properties.isEmpty()) {
	        throw new ResourceNotFoundException("Property", "", "");
	    }

	    try (FileInputStream inputStream = new FileInputStream(new File(amongFilePath));
	         Workbook workbook = new XSSFWorkbook(inputStream);
	         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

	        Sheet sheet =  null;
	        if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_EIGHT)) {
	        	sheet = workbook.getSheetAt(0);
	        }else if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_NINE)) {
	        	sheet = workbook.getSheetAt(4);
	        }
	        
	        if (sheet == null) {
	            throw new ResourceNotFoundException("Sheet not found", "", "");
	        }
	        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
	        
	        if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_EIGHT)) {
	        	downloadNamunaEightProcess(sheet, properties, cardDTO, formulaEvaluator);
	        }else if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_NINE)) {
	        	downloadNamunaNineProcess(sheet, properties, cardDTO, formulaEvaluator);
	        }
	        

	        workbook.write(outputStream);

	        // Save the modified Excel file (if needed)
	        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
	        saveModifiedExcel(arrayInputStream, getNewFilePath());

	        return arrayInputStream;
	    } 
	}

	private void processRow(Row row, PropertyDTO propertyDTO, BuildingDTO buildingDTO, FormulaEvaluator formulaEvaluator, RateCardDTO cardDTO) {
	    int cellIndex = 0;

	    for (Cell cell : row) {
	        switch (cellIndex) {
	            case 0:
	                setNumericCellValue(cell, propertyDTO.getAnuKra());
	                break;
	            case 1:
	                setNumericCellValue(cell, propertyDTO.getPlotNo());
	                break;
	            case 2:
	                setStringCellValue(cell, buildingDTO.getBuildingForm());
	                break;
	            case 3:
	                setStringCellValue(cell, buildingDTO.getBuildingType());
	                break;
	            case 4:
	                setStringCellValue(cell, propertyDTO.getOwnerName());
	                break;
	            case 5:
	                setStringCellValue(cell, propertyDTO.getRenteeName());
	                break;
	            case 6:
	                setNumericCellValue(cell, propertyDTO.getOwnerWifeName());
	                break;
	            case 9:
	                setNumericCellValue(cell, buildingDTO.getBuildingAreaLength() * buildingDTO.getBuildingAreaWidth());
	                break;
	            case 10:
	                setNumericCellValue(cell, buildingDTO.getBuildingAreaLength());
	                break;
	            case 11:
	                setNumericCellValue(cell, buildingDTO.getBuildingAreaWidth());
	                break;
	            case 12:
	                setNumericCellValue(cell, buildingDTO.getBuildingAreaLength() * buildingDTO.getBuildingAreaWidth());
	                break;
	            case 13:
	            case 14:
	            case 15:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	                break;
	            case 26:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	            	break;
	            case 27:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	            	break;
	            case 28:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	            	break;
	            case 29:
	            	setNumericCellValue(cell, buildingDTO.getElectTaxCurnt());
	            	break;
	            case 30:
	            	setNumericCellValue(cell, buildingDTO.getHealthTaxCurnt());
	            	break;
	            case 33:
	                formulaEvaluator.evaluateFormulaCell(cell);
	                break;
	            case 17:
	                cell.setCellValue(1); // Hardcoded value
	            	break;
	            case 16:
	                cell.setCellValue(1); // Hardcoded value
	            case 21:
	            	cell.setCellValue(1); // Hardcoded value
	                break;
	            case 24:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	                break;
	            case 18:
	                setNumericCellValue(cell, buildingDTO.getBuildingAge());
	                break;
	            case 19:
	            	setNumericCellValue(cell, cardDTO.getGaavthaan());
	            	break;
	            case 20:
	            	setNumericCellValue(cell, cardDTO.getGaavthaanKhaaliJaaga());
	            	break;
	            case 22:
	            	setNumericCellValue(cell, cardDTO.getKarachaDarEmarat());
	            	break;
	            case 23:
	            	setNumericCellValue(cell, cardDTO.getKarachaDarKhaaliJaaga());
	                break;
	            case 25:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	                break;
	            case 31:
	                setNumericCellValue(cell, buildingDTO.getWaterTaxCurnt());
	                break;
	            case 32:
	                setNumericCellValue(cell, buildingDTO.getSpWaterTaxCurnt());
	                break;
	        }
	        cellIndex++;
	    }
	}

	private void setNumericCellValue(Cell cell, String value) {
	    try {
	        cell.setCellValue(Double.parseDouble(value));
	    } catch (NumberFormatException e) {
	        cell.setCellValue(0);
	    }catch (Exception e) {
	        cell.setCellValue(0);
	    }
	}
	
	private void setNumericCellValue(Cell cell, double value) {
	    try {
	        cell.setCellValue(value);
	    } catch (NumberFormatException e) {
	        cell.setCellValue(0);
	    }catch (Exception e) {
	        cell.setCellValue(0);
	    }
	}

	private void setStringCellValue(Cell cell, String value) {
	    cell.setCellValue(value != null ? value : "");
	}

	private String getNewFilePath() {
	    String userDir = System.getProperty("user.dir");
	    return userDir + "/downloadedFileAmong.xlsx";
	}

	private void saveModifiedExcel(ByteArrayInputStream arrayInputStream, String newFilePath) throws IOException {
	    try (FileOutputStream fileOut = new FileOutputStream(newFilePath)) {
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = arrayInputStream.read(buffer)) != -1) {
	            fileOut.write(buffer, 0, length);
	        }
	    }
	}

	
	
	@Override
	public byte[] generatePdf(Long anukramank, Long id) {
		
		Property property = propertyRepository.findById(anukramank).orElseThrow(() -> new ResourceNotFoundException(anukramank + "", "", ""));
		
		List<Building> buildings =  property.getBuildings();
		
		Building building =null;
		
		for(Building building2 : buildings) {
			if(building2.getId() == id) {
				building = building2;
			}
		}
		
		String content = "<html lang=\"en\">"
			    + "<head>"
			    + "<meta charset=\"UTF-8\"/>"
			    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>"
			    + "<title>Document</title>"
			    + "<style>"
//			    +"@font-face {"
//			    +"font-family: 'Mangal';"
//			    +"src: url('path/to/Mangal.ttf') format('truetype');"
			    + "body {"
			    + "    margin: 0;"
			    + "    padding: 0px;"
			    + "    background-color: white;"
			    +"font-family: Kokila, sans-serif;\""
			    + "}"
			    + ".document {"
			    + "    display: flex;"
			    + "    justify-content: space-between;"
			    + "}"
			    + ".page {"
			    + "    width: 100%;"
			    + "    padding: 20px;"
			    + "    border: 2px solid #000;"
			    + "    background-color: white;"
			    + "}"
			    + ".header {"
			    + "    text-align: center;"
			   // + "    margin-bottom: 10px;"
			    + "    font-weight: bold;"
			    + "}"
			    + ".content {"
			    + "    margin-bottom: 20px;"
			    + "}"
			    + ".content p {"
			    + "    margin: 2px 0;"
			    + "}"
			    + "table {"
			    + "    width: 100%;"
			    + "    border-collapse: collapse;"
			    + "    margin-bottom: 20px;"
			    + "}"
			    + "th, td {"
			    + "    border: 1px solid #000;"
			    + "    padding: 8px;"
			    + "    text-align: left;"
			    + "}"
			    + "th {"
			    + "    background-color: white;"
			    + "}"
			    + "tfoot {"
			    + "    font-weight: bold;"
			    + "}"
			    + ".footer {"
			    + "    text-align: center;"
			    + "    margin-top: 20px;"
			    + "}"
			    + ".footer p {"
			    + "    margin: 5px 0;"
			    + "}"
			    + ".signatures {"
			    + "    display: flex;"
			    + "    justify-content: space-between;"
			    + "    margin-top: 10px;"
			    + "}"
			    + ".signature {"
			    + "    width: 45%;"
			    + "    height: 40px;"
			    + "    border-top: 1px solid #000;"
			    + "    text-align: center;"
			    + "    line-height: 40px;"
			    + "}"
			    + "</style>"
			    + "</head>"
			    + "<body>"
			    + "<div class=\"document\">"
			    + "    <!-- Left Side of the Document -->"
			    + "    <div class=\"page\">"
			    + "        <div class=\"header\">"
			    + "            <p>कराच्या मागणीचे बील</p>"
			    + "            <p>बिल नं. २०३</p>"
			    + "            <p>कार्यालय ग्रामपंचायत किरमोटी (भा) मौजा टेंभरी</p>"
			    + "            <p>पंचायत समिती हिंगणा जि. नागपूर</p>"
			    + "            <p>( मुंबई ग्रा. पं. कायदा १९५८ कालम १२९ अन्वये)</p>"
			    + "        </div>"
			    + "        <div class=\"content\">"
			    + "            <p> " + property.getOwnerName() + "</p>"
			    + "            <p>यांचेकडुन</p>"
			    + "            <p>वडगाव</p>"
			    + "            <p>वार्ड नं   " + property.getWardNo() + "  घर नं" + property.getPlotNo() +"     खसरा नं.- "+ " " + "सोसायटीचे नाव मौजा टेंभरी याबद्दल</p>"
			    + "            <p>सन 2024 - 2025 करीता पुढे नमुद केलेल्या रक्कमा</p>"
			    + "        </div>"
			    + "        <table>"
			    + "            <thead>"
			    + "                <tr>"
			    + "                    <th>कराची नावे</th>"
			    + "                    <th>सन 2023-24 मागील बाकी रुपये</th>"
			    + "                    <th>सन 2024-25 चालू वर्ष रुपये</th>"
			    + "                    <th>एकूण रक्कम</th>"
			    + "                </tr>"
			    + "            </thead>"
			    + "            <tbody>"
			    + "                <tr>"
			    + "                    <td>घरावरील कर</td>"
			    + "                    <td>" + building.getHomeTaxPrev() + "</td>"
			    + "                    <td>" + building.getHomeTaxCurnt() + "</td>"
			    + "					   <td>" + (building.getHomeTaxCurnt() + building.getHomeTaxPrev())+ "</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>खाली जागेचा कर</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>दिवाबत्ती कर</td>"
			    + "                    <td>" + building.getElectTaxPrev() + "</td>"
			    + "                    <td>" + building.getElectTaxCurnt() + "</td>"
			    + "					   <td>" + (building.getElectTaxPrev() + building.getElectTaxCurnt())+ "</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>आरोग्य कर</td>"
			    + "                    <td>" + building.getHealthTaxPrev() + "</td>"
			    + "                    <td>" + building.getHealthTaxCurnt() + "</td>"
			    + "					   <td>" + (building.getHealthTaxPrev() + building.getHealthTaxCurnt())+ "</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>सफाई कर</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>सामान्य पाणी कर</td>"
			    + "                    <td>" + building.getWaterTaxPrev() + "</td>"
			    + "                    <td>" + building.getWaterTaxCurnt() + "</td>"
			    + "					   <td>" + (building.getWaterTaxPrev() + building.getWaterTaxCurnt())+ "</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>विशेष पाणी कर</td>"
			    + "                    <td>" + building.getSpWaterTaxPrev() + "</td>"
			    + "                    <td>" + building.getSpWaterTaxCurnt() + "</td>"
			    + "					   <td>" + (building.getSpWaterTaxPrev() + building.getSpWaterTaxCurnt())+ "</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>उशीरा कर</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>नोटीस फी</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>अधिकाऱ्याची (वारंटी फी)</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "                <tr>"
			    + "                    <td>इतर</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                    <td>0</td>"
			    + "                </tr>"
			    + "            </tbody>"
			    + "            <tfoot>"
			    + "                <tr>"
			    + "                    <td>एकूण</td>"
			    + "                    <td>"+ (building.getHomeTaxPrev() + building.getElectTaxPrev() + building.getHealthTaxPrev() + building.getWaterTaxPrev() + building.getSpWaterTaxPrev()) +"</td>"
			    + "                    <td>"+ (building.getHomeTaxCurnt() + building.getElectTaxCurnt() + building.getHealthTaxCurnt() + building.getWaterTaxCurnt() + building.getSpWaterTaxCurnt()) +"</td>"
			    + "                    <td>"+ ((building.getHomeTaxPrev() + building.getElectTaxPrev() + building.getHealthTaxPrev() + building.getWaterTaxPrev() + building.getSpWaterTaxPrev())+(building.getHomeTaxCurnt() + building.getElectTaxCurnt() + building.getHealthTaxCurnt() + building.getWaterTaxCurnt() + building.getSpWaterTaxCurnt())) +"</td>"
			    + "                </tr>"
			    + "            </tfoot>"
			    + "        </table>"
			    + "        <div class=\"footer\">"
			    + "            <p>वरील बिलात नमूद केलेल्या करदाखल्याच्या रकमा दि ____ आत पा. प. चे कार्यालयात पटवून पावती घ्यावी. असे न केल्यास आपले वर योग्य कारवाई करण्यात येईल.</p>"
			    + "            <div class=\"signatures\">"
			    + "                <p class=\"signature\">मागणी बिल मिळाल्याचाबत स्वाक्षरी</p></br>"
			    + "                <p class=\"signature\">दिनांक:</p>"
			    + "                <p class=\"signature\">सरपंच/सचिव</p><br />"
			    + "                <p class=\"signature\">ग्रामपंचायत किरमोटी</p>"
			    + "            </div></br></br>"
			    + "            <div>टिप: महाराष्ट्र ग्रामपंचाय करव की मुधारणा) नियम २०१८ नुसार ३० सप्टेम्बर पर्यंत कर भरल्यास ५० सूट देण्यात येईल</div>"
			    + "        </div>"
			    + "    </div>"
			    + "</div>"
			    + "</body>"
			    + "</html>";
		
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        	 String userDir = System.getProperty("user.dir");
        	String fontPath = userDir + "/kokila.ttf";
        	

        	 PdfWriter writer = new PdfWriter(byteArrayOutputStream);
             PdfDocument pdfDocument = new PdfDocument(writer);
            
             PdfFont font = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
             ConverterProperties converterProperties = new ConverterProperties();
             FontProvider fontProvider = new FontProvider();
             fontProvider.addFont(userDir + "/kokila.ttf");
             fontProvider.addFont(userDir + "/kokilabi.ttf");
             fontProvider.addFont(userDir + "/kokilai.ttf");
             fontProvider.addFont(userDir + "/kokilab.ttf");
//             fontProvider.addFont(userDir + "/Kokila Regular.ttf");
//             fontProvider.addFont(userDir + "/Mangal Regular.ttf");

             converterProperties.setFontProvider(fontProvider);
             converterProperties.setCharset("UTF-8");


             // Convert HTML to PDF
             HtmlConverter.convertToPdf(content, pdfDocument, converterProperties);


             pdfDocument.close();
            
	        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

	        String newFilePath = userDir + "/downloadedFileAmong.pdf";
	        
	        saveModifiedExcel(arrayInputStream, newFilePath);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
    }
	
	// upload excel report started;
	
	@Override
	public boolean uploadAmongExcel(MultipartFile file , String namuna) throws Exception {
		boolean fileUploded = false;
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = null;

	        if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_EIGHT)) {
	        	sheet = workbook.getSheetAt(0);
	        }else if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_NINE)) {
	        	sheet = workbook.getSheetAt(4);
	        }
	        
	        if (sheet == null) {
	            throw new ResourceNotFoundException("Sheet not found", "", "");
	        }
	        
           
            List<Property> properties = new ArrayList<>();
            List<Building> buildings = new ArrayList<>();
            
            if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_EIGHT)) {
            	uploadNanumaEightProcess(sheet, properties, buildings);
	        }else if(namuna.equalsIgnoreCase(PanchayatConstant.NAMUNA_NINE)) {
	        	uploadNanumaNineProcess(sheet, properties, buildings);
	        }
          
            workbook.close();
            Map<Long, List<Building>> propertyIdToBuildingsMap = buildings.stream()
                    .filter(buildingDTO -> buildingDTO.getProperty() != null && buildingDTO.getProperty().getAnuKra() != null)
                    .collect(Collectors.groupingBy(buildingDTO -> buildingDTO.getProperty().getAnuKra()));
                    
            properties.forEach(propertyDTO -> {
                List<Building> associatedBuildings = propertyIdToBuildingsMap.get(propertyDTO.getAnuKra());
                if (associatedBuildings != null) {
                    propertyDTO.setBuildings(associatedBuildings); // Assuming PropertyDTO has a setBuildings method
                }
            });
            
            propertyRepository.saveAll(properties);
           // buildingRepository.saveAll(buildings);
            fileUploded = true;
        } catch (Exception e) {
            e.printStackTrace();
            fileUploded = false;
        }
		return fileUploded;
    }
	
// upload excel report end;
	
	
	
	private String processCell(Cell cell) {
		
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                // Cell contains a string value
            	return PanchayatConstant.CELL_TYPE_STRING;
            case NUMERIC:
            	return PanchayatConstant.CELL_TYPE_NUMERIC;
            case BOOLEAN:
                // Cell contains a boolean value
            	return PanchayatConstant.CELL_TYPE_BOOLEAN;
            case FORMULA:
                // Cell contains a formula
            	return PanchayatConstant.CELL_TYPE_FORMULA;
            default:
            	return PanchayatConstant.CELL_TYPE_BLANK;
        }
       
    }
	
	private boolean isRowEmpty(Row row) {
	    if (row == null) {
	        return true;
	    }
	    for (Cell cell : row) {
	        if (cell != null && cell.getCellType() != CellType.BLANK) {
	            return false;
	        }
	    }
	    return true;
	}
	
	
	private void uploadNanumaEightProcess(Sheet sheet, List<Property> properties, List<Building> buildings) {
		 Iterator<Row> rows = sheet.iterator();
        int rowIndex = 0;
        while (rows.hasNext()) {
        	Row row = rows.next();
        	if(isRowEmpty(row)) {
        		continue;
        	}
        	if(row != null && rowIndex > 1) {
        		Iterator<Cell> cellIterator = row.cellIterator();
        		int cellIndex = 0;
        		Property property = new Property();
        		Building building = new Building();
        		 while (cellIterator.hasNext()) {
        			 Cell cell = cellIterator.next();
        			 String cellType = processCell(cell);
        			 if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 0) {
        				 property.setAnuKra((long) cell.getNumericCellValue()); 
        			 }
        			 else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 1) {
	                	 property.setPropertyNo((String.valueOf(cell.getNumericCellValue())));
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 2) {
	                	 building.setBuildingForm(cell.getStringCellValue());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 3) {
	                	 building.setBuildingForm(cell.getStringCellValue());
	                	// cell.setCellValue(buildingDTO.getBuildingType());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 4) {
	                	 property.setOwnerName(cell.getStringCellValue());
	                	 //cell.setCellValue(propertyDTO.getOwnerName());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 5) {
	                	 property.setRenteeName(cell.getStringCellValue());
	                	 //cell.setCellValue(propertyDTO.getRenteeName());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 6) {
	                	 property.setOwnerWifeName(cell.getStringCellValue());
	                	 //cell.setCellValue(Integer.parseInt(propertyDTO.getOwnerWifeName()));
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 9) {
	                	// cell.setCellValue((buildingDTO.getBuildingAreaLength() * buildingDTO.getBuildingAreaWidth()));
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 10) {
	                	 building.setBuildingAreaLength(cell.getNumericCellValue());
	                	 //cell.setCellValue(buildingDTO.getBuildingAreaLength());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 11) {
	                	 building.setBuildingAreaWidth(cell.getNumericCellValue());
	                	 //cell.setCellValue(buildingDTO.getBuildingAreaWidth());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 12) {
	                	// cell.setCellValue((buildingDTO.getBuildingAreaLength() * buildingDTO.getBuildingAreaWidth()));
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 13) {
	                	 //cell.setCellValue(0);// This cell = 9-12
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 14) {
//	                	 cell.setCellValue(0);// This cell = 12 / 10.764
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 15) {
	                	// cell.setCellValue(0);// This cell = 13 / 10.764
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 16) {
	                	 //cell.setCellValue(1);//This cell = 1 hardcoded
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 17) {
	                	 //cell.setCellValue(1);//This cell = 1 hardcoded
	                	// formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 18) {
	                	 building.setBuildingAge(cell.getNumericCellValue());
	                	 //cell.setCellValue(buildingDTO.getBuildingAge());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 19) {
	                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 20) {
	                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 21) {
	                	 //cell.setCellValue(1);// This cell = 1 Hardcoded
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 22) {
	                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 23) {
	                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 24) {
	                	 //cell.setCellValue(1);//This cell = 1 hardcoded
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 25) {
	                	 //cell.setCellValue(0);// This cell = Formula
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 26) {
	                	// cell.setCellValue(0);// This cell = Formula
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 27) {
	                	 //cell.setCellValue(0);// This cell = Formula
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 28) {
	                	// cell.setCellValue(0);// This cell = Formula
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 29) {
	                	 //cell.setCellValue(buildingDTO.getElectTaxCurnt());
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 30) {
	                	 //cell.setCellValue(buildingDTO.getHealthTaxCurnt());
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 31) {
	                	 building.setWaterTaxCurnt(cell.getNumericCellValue());
	                	 //cell.setCellValue(buildingDTO.getWaterTaxCurnt());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 32) {
	                	 building.setSpWaterTaxCurnt(cell.getNumericCellValue());
	                	 //cell.setCellValue(buildingDTO.getSpWaterTaxCurnt());
	                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 33) {
	                	 //formulaEvaluator.evaluateFormulaCell(cell);
	                 }
        			 //building.setId(property.getAnuKra());
        			 building.setProperty(property);
        			 
        			cellIndex++;
        		 } 
        		 if(!properties.contains(property)) {
        			 properties.add(property);
        			 
        		 }
        		 buildings.add(building);
        	}
        	rowIndex++;
 
        }
		
	}
	
	private void uploadNanumaNineProcess(Sheet sheet, List<Property> properties, List<Building> buildings) {
		
		 Iterator<Row> rows = sheet.iterator();
	        int rowIndex = 0;
	        while (rows.hasNext()) {
	        	Row row = rows.next();
	        	if(isRowEmpty(row)) {
	        		continue;
	        	}
	        	if(row != null && rowIndex > 3) {
	        		Iterator<Cell> cellIterator = row.cellIterator();
	        		int cellIndex = 0;
	        		Property property = new Property();
	        		Building building = new Building();
	        		 while (cellIterator.hasNext()) {
	        			 Cell cell = cellIterator.next();
	        			 String cellType = processCell(cell);
	        			 if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 0) {
	        				 property.setAnuKra((long) cell.getNumericCellValue()); 
	        			 }
	        			 else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 1) {
		                	 property.setPropertyNo((String.valueOf(cell.getNumericCellValue())));
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 2) {
		                	 building.setBuildingForm(cell.getStringCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 3) {
		                	 building.setBuildingForm(cell.getStringCellValue());
		                	// cell.setCellValue(buildingDTO.getBuildingType());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 4) {
		                	 property.setOwnerName(cell.getStringCellValue());
		                	 //cell.setCellValue(propertyDTO.getOwnerName());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 5) {
		                	 property.setRenteeName(cell.getStringCellValue());
		                	 //cell.setCellValue(propertyDTO.getRenteeName());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_STRING) && cellIndex == 6) {
		                	 property.setOwnerWifeName(cell.getStringCellValue());
		                	 //cell.setCellValue(Integer.parseInt(propertyDTO.getOwnerWifeName()));
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 7) {
		                	 building.setHomeTaxPrev(cell.getNumericCellValue());
		                	 //cell.setCellValue(Integer.parseInt(propertyDTO.getOwnerWifeName()));
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 8) {
		                	 building.setHomeTaxCurnt(cell.getNumericCellValue());
		                	 //cell.setCellValue(Integer.parseInt(propertyDTO.getOwnerWifeName()));
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 9) {
		                	 building.setElectTaxPrev(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 10) {
		                	 building.setElectTaxCurnt(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 11) {
		                	 building.setHealthTaxPrev(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 12) {
		                	 building.setHealthTaxCurnt(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 13) {
		                	 building.setWaterTaxPrev(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 14) {
		                	 building.setWaterTaxCurnt(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 15) {
		                	 building.setSpWaterTaxPrev(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 16) {
		                	 building.setSpWaterTaxCurnt(cell.getNumericCellValue());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 17) {
		                	 
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 18) {
		                	 //building.setBuildingAge(cell.getNumericCellValue());
		                	 //cell.setCellValue(buildingDTO.getBuildingAge());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 19) {
		                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 20) {
		                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 21) {
		                	 //cell.setCellValue(1);// This cell = 1 Hardcoded
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 22) {
		                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 23) {
		                	 //cell.setCellValue(0);// This cell = Pick from Master Rate
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 24) {
		                	 //cell.setCellValue(1);//This cell = 1 hardcoded
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 25) {
		                	 //cell.setCellValue(0);// This cell = Formula
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 26) {
		                	// cell.setCellValue(0);// This cell = Formula
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 27) {
		                	 //cell.setCellValue(0);// This cell = Formula
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 28) {
		                	// cell.setCellValue(0);// This cell = Formula
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 29) {
		                	 //cell.setCellValue(buildingDTO.getElectTaxCurnt());
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 30) {
		                	 //cell.setCellValue(buildingDTO.getHealthTaxCurnt());
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 31) {
		                	 building.setWaterTaxCurnt(cell.getNumericCellValue());
		                	 //cell.setCellValue(buildingDTO.getWaterTaxCurnt());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_NUMERIC) && cellIndex == 32) {
		                	 building.setSpWaterTaxCurnt(cell.getNumericCellValue());
		                	 //cell.setCellValue(buildingDTO.getSpWaterTaxCurnt());
		                 }else if(cellType.equalsIgnoreCase(PanchayatConstant.CELL_TYPE_FORMULA) && cellIndex == 33) {
		                	 //formulaEvaluator.evaluateFormulaCell(cell);
		                 }
	        			 //building.setId(property.getAnuKra());
	        			 building.setProperty(property);
	        			 
	        			cellIndex++;
	        		 } 
	        		 if(!properties.contains(property)) {
	        			 
	        			 properties.add(property);
	        			 
	        		 }
	        		 buildings.add(building);
	        	}
	        	rowIndex++;
	 
	        }
		
	}
	
	private void downloadNamunaEightProcess(Sheet sheet, List<Property> properties, RateCardDTO cardDTO, FormulaEvaluator formulaEvaluator) {
		 int startIndex = 3; // Starting row index in Excel

	        for (Property property : properties) {
	            PropertyDTO propertyDTO = PanchayatHelpher.convertProprtyEntity(property);
	            List<BuildingDTO> buildingDTOs = propertyDTO.getBuildings();
	            
	            for (BuildingDTO buildingDTO : buildingDTOs) {
	                Row row = sheet.getRow(startIndex);
	                if (row != null) {
	                    processRow(row, propertyDTO, buildingDTO, formulaEvaluator, cardDTO);
	                    startIndex++;
	                }
	            }
	        }
	}
	
	private void downloadNamunaNineProcess(Sheet sheet, List<Property> properties, RateCardDTO rateCard, FormulaEvaluator formulaEvaluator) {
		int startIndex = 4; // Starting row index in Excel

        for (Property property : properties) {
            PropertyDTO propertyDTO = PanchayatHelpher.convertProprtyEntity(property);
            List<BuildingDTO> buildingDTOs = propertyDTO.getBuildings();
            
            for (BuildingDTO buildingDTO : buildingDTOs) {
                Row row = sheet.getRow(startIndex);
                if (row != null) {
                    processRowNine(row, propertyDTO, buildingDTO, formulaEvaluator, rateCard);
                    startIndex++;
                }
            }
        }
	}
	
	private void processRowNine(Row row, PropertyDTO propertyDTO, BuildingDTO buildingDTO, FormulaEvaluator formulaEvaluator, RateCardDTO cardDTO) {
	    int cellIndex = 0;

	    for (Cell cell : row) {
	        switch (cellIndex) {
	            case 0:
	                setNumericCellValue(cell, propertyDTO.getAnuKra());
	                break;
	            case 1:
	                setNumericCellValue(cell, propertyDTO.getPropertyNo());
	                break;
	            case 2:
	                setStringCellValue(cell, buildingDTO.getBuildingForm());
	                break;
	            case 3:
	                setStringCellValue(cell, buildingDTO.getBuildingType());
	                break;
	            case 4:
	                setStringCellValue(cell, propertyDTO.getOwnerName());
	                break;
	            case 5:
	                setStringCellValue(cell, propertyDTO.getRenteeName());
	                break;
	            case 6:
	                setNumericCellValue(cell, propertyDTO.getOwnerWifeName());
	                break;
	            case 7:
	                setNumericCellValue(cell, buildingDTO.getHealthTaxPrev());
	                break;
	            case 8:
	            	setNumericCellValue(cell, buildingDTO.getHealthTaxCurnt());
	                break;
	     	    case 9:
	            	setNumericCellValue(cell, buildingDTO.getElectTaxPrev());
	                break;
	            case 10:
	            	setNumericCellValue(cell, buildingDTO.getElectTaxCurnt());
	                break;
	            case 11:
	            	setNumericCellValue(cell, buildingDTO.getHealthTaxPrev());
	                break;
	            case 12:
	            	setNumericCellValue(cell, buildingDTO.getHealthTaxCurnt());
	                break;
	            case 13:
	            	setNumericCellValue(cell, buildingDTO.getWaterTaxPrev());
	                break;
	            case 14:
	            	setNumericCellValue(cell, buildingDTO.getWaterTaxCurnt());
	                break;
	            case 15:
	            	setNumericCellValue(cell, buildingDTO.getSpWaterTaxPrev());
	                break;
//	            case 26:
//	            	formulaEvaluator.evaluateFormulaCell(cell);
//	            	break;
//	            case 27:
//	            	formulaEvaluator.evaluateFormulaCell(cell);
//	            	break;
//	            case 28:
//	            	formulaEvaluator.evaluateFormulaCell(cell);
//	            	break;
//	            case 29:
//	            	setNumericCellValue(cell, buildingDTO.getElectTaxCurnt());
//	            	break;
//	            case 30:
//	            	setNumericCellValue(cell, buildingDTO.getHealthTaxCurnt());
//	            	break;
//	            case 33:
//	                formulaEvaluator.evaluateFormulaCell(cell);
//	                break;
	            case 17:
	            	formulaEvaluator.evaluateFormulaCell(cell);
	                break;
	            case 16:
	            	setNumericCellValue(cell, buildingDTO.getSpWaterTaxCurnt());
	                break;
//	            case 21:
//	            	cell.setCellValue(1); // Hardcoded value
//	                break;
//	            case 24:
//	            	formulaEvaluator.evaluateFormulaCell(cell);
//	                break;
//	            case 18:
//	                setNumericCellValue(cell, buildingDTO.getBuildingAge());
//	                break;
//	            case 19:
//	            	setNumericCellValue(cell, cardDTO.getGaavthaan());
//	            	break;
//	            case 20:
//	            	setNumericCellValue(cell, cardDTO.getGaavthaanKhaaliJaaga());
//	            	break;
//	            case 22:
//	            	setNumericCellValue(cell, cardDTO.getKarachaDarEmarat());
//	            	break;
//	            case 23:
//	            	setNumericCellValue(cell, cardDTO.getKarachaDarKhaaliJaaga());
//	                break;
//	            case 25:
//	            	formulaEvaluator.evaluateFormulaCell(cell);
//	                break;
//	            case 31:
//	                setNumericCellValue(cell, buildingDTO.getWaterTaxCurnt());
//	                break;
//	            case 32:
//	                setNumericCellValue(cell, buildingDTO.getSpWaterTaxCurnt());
//	                break;
	        }
	        cellIndex++;
	    }
	}




}
