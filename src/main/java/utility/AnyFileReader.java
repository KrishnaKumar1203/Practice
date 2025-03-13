package utility;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.apache.pdfbox.pdmodel.PDDocument;
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
    
}
