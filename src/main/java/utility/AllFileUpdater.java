package utility;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.*;
import java.nio.file.StandardOpenOption;

public class AllFileUpdater {
    public static void updateFile(String filePath, String fileType, Object data, boolean updateRequired, boolean addNewSheet, String... sheetName) {
        if (!updateRequired) {
            System.out.println("No update was done in the file as no update was needed.");
            return;
        }
        try {
            switch (fileType.toLowerCase()) {
                case "csv":
                    updateCsvFile(filePath, (List<Map<String, String>>) data, addNewSheet);
                    break;
                case "excel":
                    updateExcelFile(filePath, (List<Map<String, String>>) data, addNewSheet, sheetName.length > 0 ? sheetName[0] : "Sheet1");
                    break;
                case "text":
                    updateTextFile(filePath, data.toString());
                    break;
                case "json":
                    updateJsonFile(filePath, (Map<String, Object>) data);
                    break;
                case "xml":
                    updateXmlFile(filePath, (List<Map<String, String>>) data);
                    break;
                case "word":
                    updateWordFile(filePath, data.toString());
                    break;
                case "pdf":
                    updatePdfFile(filePath, data.toString());
                    break;
                default:
                    System.out.println("Unsupported file type.");
            }
        } catch (Exception e) {
            System.err.println("Error updating file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateCsvFile(String filePath, List<Map<String, String>> data, boolean addNewSheet) throws IOException {
        if (addNewSheet) {
            filePath = filePath.replace(".csv", "_newSheet.csv");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            if (data.isEmpty()) return;
            List<String> headers = new ArrayList<>(data.get(0).keySet());
            for (Map<String, String> rowData : data) {
                String[] row = headers.stream().map(rowData::get).toArray(String[]::new);
                writer.writeNext(row);
            }
        }
    }

    private static void updateExcelFile(String filePath, List<Map<String, String>> data, boolean addNewSheet, String sheetName) throws IOException {
        boolean isXLS = filePath.toLowerCase().endsWith(".xls");
        Workbook workbook;
        File file = new File(filePath);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = isXLS ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
            }
        } else {
            workbook = isXLS ? new HSSFWorkbook() : new XSSFWorkbook();
        }

        Sheet sheet = addNewSheet ? workbook.createSheet(sheetName + "_New") : workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }

        int rowNum = sheet.getLastRowNum() + 1;
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            for (String key : rowData.keySet()) {
                row.createCell(cellNum++).setCellValue(rowData.get(key));
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static void updateTextFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), (content + "\n").getBytes(), StandardOpenOption.APPEND);
    }

    private static void updateJsonFile(String filePath, Map<String, Object> newData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> existingData = objectMapper.readValue(new File(filePath), Map.class);
        existingData.putAll(newData);
        objectMapper.writeValue(new File(filePath), existingData);
    }

    private static void updateXmlFile(String filePath, List<Map<String, String>> data) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));
        Element root = document.getDocumentElement();

        for (Map<String, String> map : data) {
            Element item = document.createElement("item");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Element element = document.createElement(entry.getKey());
                element.appendChild(document.createTextNode(entry.getValue()));
                item.appendChild(element);
            }
            root.appendChild(item);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(filePath)));
    }

    private static void updateWordFile(String filePath, String content) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(filePath));
        document.createParagraph().createRun().setText(content);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            document.write(fos);
        }
    }

    private static void updatePdfFile(String filePath, String content) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));
        PDPage page = document.getPage(0);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 750);
        contentStream.showText(content);
        contentStream.endText();
        contentStream.close();
        document.save(filePath);
        document.close();
    }
 public static void main(String[] args) {
        String csvPath = "file.csv";
        List<Map<String, String>> csvData = List.of(Map.of("ID", "3", "Name", "Bob", "Age", "28"));
        updateFile(csvPath, "csv", csvData, true, false);

        String excelPath = "file.xlsx";
        updateFile(excelPath, "excel", csvData, true, false, "Sheet1");

        String jsonPath = "file.json";
        Map<String, Object> jsonData = Map.of("city", "New York");
        updateFile(jsonPath, "json", jsonData, true, false);

        String textPath = "file.txt";
        updateFile(textPath, "text", "Appending new line to text file.\n", true, false);

        String pdfPath = "file.pdf";
        updateFile(pdfPath, "pdf", "This is an appended PDF text.", true, false);

        String xmlPath = "file.xml";
        updateFile(xmlPath, "xml", csvData, true, false);
    }
}
