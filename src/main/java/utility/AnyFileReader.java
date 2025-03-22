package utility;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.*;

public class AnyFileReader {
    private static String workbook1;

    public static Object readFile(String filePath, String fileType, String... workbookName) {
        workbook1 = (workbookName.length > 0) ? workbookName[0] : "";

        try {
            switch (fileType.toLowerCase()) {
                case "xml":
                    return readXML(filePath);
                case "text":
                    return readTextFile(filePath);
                case "excel":
                    return readExcelFile(filePath, workbook1);
                case "word":
                    return readWordFile(filePath);
                case "pdf":
                    return readPdfFile(filePath);
                case "json":
                    return readJsonFile(filePath);
                case "csv":
                    return readCsvFile(filePath);
                default:
                    System.out.println("Unsupported file type.");
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }

    private static String readTextFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private static List<Map<String, String>> readExcelFile(String filePath, String workbookName) throws IOException {
        List<Map<String, String>> excelData = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = filePath.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(workbook1);

        if (sheet == null) {
            throw new IllegalArgumentException("Workbook/SHEET not found: " + workbook1);
        }

        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.toString());
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                rowData.put(headers.get(j), row.getCell(j).toString());
            }
            excelData.add(rowData);
        }
        workbook.close();
        return excelData;
    }

    private static String readWordFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        StringBuilder content = new StringBuilder();
        if (filePath.endsWith(".doc")) {
            HWPFDocument doc = new HWPFDocument(fis);
            content.append(doc.getDocumentText());
        } else if (filePath.endsWith(".docx")) {
            XWPFDocument doc = new XWPFDocument(fis);
            doc.getParagraphs().forEach(p -> content.append(p.getText()).append("\n"));
        }
        fis.close();
        return content.toString();
    }

    private static String readPdfFile(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        }
    }

    private static Map<String, Object> readJsonFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), HashMap.class);
    }

    private static List<Map<String, String>> readCsvFile(String filePath) throws IOException {
        List<Map<String, String>> csvData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            String[] headers = records.get(0);

            for (int i = 1; i < records.size(); i++) {
                Map<String, String> row = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    row.put(headers[j], records.get(i)[j]);
                }
                csvData.add(row);
            }
        } catch (CsvException e) {
            throw new IOException("Error reading CSV file: " + e.getMessage());
        }
        return csvData;
    }

    public static List<Map<String, String>> readXML(String filePath) {
        List<Map<String, String>> xmlDataList = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Map<String, String> dataMap = new HashMap<>();
                    extractData(node, dataMap, "");
                    xmlDataList.add(dataMap);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading XML file: " + e.getMessage());
        }
        return xmlDataList;
    }

    private static void extractData(Node node, Map<String, String> dataMap, String parentKey) {
        NodeList children = node.getChildNodes();
        String key = parentKey.isEmpty() ? node.getNodeName() : parentKey + "." + node.getNodeName();

        if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
            dataMap.put(key, node.getTextContent().trim());
        } else {
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    extractData(children.item(i), dataMap, key);
                }
            }
        }
    }
 /*    public static void main(String[] args) {
        String textPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.txt";
        System.out.println("Text file"+readFile(textPath, "text"));

        String jsonPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.json";
        System.out.println("json file"+ readFile(jsonPath, "json"));

        String excelPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file23.xlsx";
        String csvPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file1.csv";
        
        System.out.println("csv file"+readFile(csvPath, "csv", "Sheet1"));
        System.out.println("excel file"+readFile(excelPath, "excel", "Sheet1"));

        String wordPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.docx";
        System.out.println("word file"+readFile(wordPath, "word"));
        String pdfPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.pdf";
        System.out.println("pdf file"+readFile(pdfPath, "pdf"));
    }*/
    public static void extractTextFromImageInPDF(String pdfFilePath, String outputTextFilePath, String targetLanguage) {
        try {
            // Load the PDF document
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
    
            // Initialize Tesseract OCR
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // Set the path to tessdata folder
            tesseract.setLanguage("eng"); // Set the language to English
            StringBuilder extractedText = new StringBuilder();
    
            // Loop through each page in the PDF
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                try {
                // Render the page as an image with a good DPI
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300); // Render at 300 DPI
                // Skip very small images (avoid Tesseract errors)
                    if (image.getWidth() < 50 || image.getHeight() < 50) {
                        System.err.println("Skipping too small image on page: " + (page + 1));
                        continue;
                    }

                    // Resize only if the image is too small
                    if (image.getWidth() < 500 || image.getHeight() < 500) {
                        image = resizeImage(image, 800, 800); // Resize to at least 800x800
                       
 		System.out.println("Resized image on page: " + (page + 1));
		}
    
               // Convert to grayscale and apply thresholding for better OCR
                BufferedImage processedImage = preprocessImage(image);
    
                // Perform OCR on the processed image
                
                    String pageText = tesseract.doOCR(processedImage);

                    extractedText.append(pageText).append("\n\n");
    		// Free memory for the processed image
                processedImage.flush();
                image.flush();
                } catch (TesseractException e) {
                    System.err.println("Error performing OCR on page " + (page + 1) + ": " + e.getMessage());
 		} catch (IOException e) {
                System.err.println("Error processing image on page " + (page + 1) + ": " + e.getMessage());
           
                }
            }
    
            // Close the PDF document
            document.close();
    
		// Translate the extracted text

            String translatedText = translateText(extractedText.toString(), targetLanguage);
            // Write the extracted text to a text file
            try (FileWriter writer = new FileWriter(outputTextFilePath)) {
               writer.write(translatedText);
            }
    
            System.out.println("Translated text extracted and written to: " + outputTextFilePath);
    
        } catch (IOException e) {
            System.err.println("Error processing PDF file: " + e.getMessage());
        }
    }
    
	 private static String translateText(String text, String targetLanguage) {
 
        WebDriver driver = null;
        try {
        // Check if the text is empty
        if (text == null || text.trim().isEmpty()) {
            System.err.println("No text to translate.");
            return text; // Return the original text if it's empty
        }
            // LibreTranslate API endpoint
            String apiUrl = "https://libretranslate.com";
            
             // Initialize the WebDriver
            driver = Selenium.getDriver();
            driver.get(apiUrl);
             WebElement Translation;
    
             Translation = CommonFunctions.FluentWait(driver,20,PageObjectModel.Translation);
                Translation.sendKeys(text);
                WebElement TranslateLanguage;
                TranslateLanguage = CommonFunctions.FluentWait(driver,20,PageObjectModel.TranslateLanguage);
        // Use the Select class to select the desired language by its value
        Select languageDropdown = new Select(TranslateLanguage);
        languageDropdown.selectByValue(targetLanguage); // Select by value (e.g., "hi" for Hindi)

        // Submit the translation request
        TranslateLanguage.submit();

                WebElement Textcopied;
                Textcopied = CommonFunctions.FluentWait(driver,20,PageObjectModel.Textcopied);
                 Textcopied.click();
                 // Retrieve the copied text from the clipboard
                 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String   translatedText = (String) clipboard.getData(DataFlavor.stringFlavor);
                 
                return translatedText;
        } catch (Exception e) {
            System.err.println("Error during translation: " + e.getMessage());
            return text; // Return the original text if translation fails
        }
	  }
    private static BufferedImage preprocessImage(BufferedImage image) {
// Resize and convert the image to grayscale
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayscaleImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayscaleImage;

    }
	// Resize image while maintaining aspect ratio
private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = outputImage.createGraphics();
    g.drawImage(resultingImage, 0, 0, null);
    g.dispose();
    return outputImage;
}

    public static void main(String[] args) {
        // Input PDF file path
        String pdfFilePath = "C:/Users/kksuc/Downloads/Phone Link/image_pdf.pdf";

        // Output text file path
        String outputTextFilePath = "C:/Users/kksuc/Downloads/Phone Link/extracted_text.txt";

// Target language (e.g., "hi" for Hindi, "zh" for Chinese, "en" for English)
        String targetLanguage = "hi";
        // Extract, translate, and store text from the PDF
        extractTextFromImageInPDF(pdfFilePath, outputTextFilePath, targetLanguage);
    }
    
}
