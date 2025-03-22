package utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.*;
import java.nio.file.StandardOpenOption;

public class AllFileWriter {

    public static void writeFile(String filePath, String fileType, Object data, String... sheetName) {
        try {
            switch (fileType.toLowerCase()) {
                case "xml":
                    writeXML(filePath, (List<Map<String, String>>) data);
                    break;
                case "text":
                    writeTextFile(filePath, data.toString());
                    break;
                case "excel":
                    writeExcelFile(filePath, (List<Map<String, String>>) data, sheetName.length > 0 ? sheetName[0] : "Sheet1");
                    break;
                case "word":
                    writeWordFile(filePath, data.toString());
                    break;
                case "pdf":
                    writePdfFile(filePath, data.toString());
                    break;
                case "json":
                    writeJsonFile(filePath, (Map<String, Object>) data);
                    break;
                case "csv":
                    writeCsvFile(filePath, (List<Map<String, String>>) data);
                    break;
                default:
                    System.out.println("Unsupported file type.");
            }
        } catch (Exception e) {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeTextFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    private static void writeExcelFile(String filePath, List<Map<String, String>> data, String sheetName) throws IOException {
        boolean isXLS = filePath.toLowerCase().endsWith(".xls");
        Workbook workbook;
        File file = new File(filePath);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = isXLS ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
            }
            // Remove existing sheet if present
            int sheetIndex = workbook.getSheetIndex(sheetName);
            if (sheetIndex != -1) {
                workbook.removeSheetAt(sheetIndex);
            }
        } else {
            workbook = isXLS ? new HSSFWorkbook() : new XSSFWorkbook();
        }

        // Create new sheet
        Sheet sheet = workbook.createSheet(sheetName);

        if (!data.isEmpty()) {
            // Create header row
            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>(data.get(0).keySet());

            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // Populate data rows
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> rowData = data.get(i);
                for (int j = 0; j < headers.size(); j++) {
                    row.createCell(j).setCellValue(rowData.get(headers.get(j)));
                }
            }
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        // Close workbook
        workbook.close();
    }

    private static void writeWordFile(String filePath, String content) throws IOException {
        if (filePath.endsWith(".doc")) {
            HWPFDocument document = new HWPFDocument(new FileInputStream(filePath));
            document.getRange().insertAfter(content);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                document.write(fos);
            }
        } else if (filePath.endsWith(".docx")) {
            XWPFDocument document = new XWPFDocument();
            document.createParagraph().createRun().setText(content);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                document.write(fos);
            }
        }
    }

    private static void writePdfFile(String filePath, String content) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
               contentStream.setFont(PDType1Font.HELVETICA, 12); // Set the font and size
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 750);

            String[] lines = content.split("\n");
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            contentStream.endText();
        }

        document.save(filePath);
        document.close();
    }

    private static void writeJsonFile(String filePath, Map<String, Object> data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), data);
    }

    private static void writeCsvFile(String filePath, List<Map<String, String>> data) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            if (data.isEmpty()) return;
            List<String> headers = new ArrayList<>(data.get(0).keySet());
            writer.writeNext(headers.toArray(new String[0]));

            for (Map<String, String> rowData : data) {
                String[] row = headers.stream().map(rowData::get).toArray(String[]::new);
                writer.writeNext(row);
            }
        }
    }

    public static void writeXML(String filePath, List<Map<String, String>> data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("root");
            document.appendChild(root);

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
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

        } catch (Exception e) {
            System.err.println("Error writing XML file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String textPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.txt";
        writeFile(textPath, "text", "Sample text file content.");

        String jsonPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.json";
        Map<String, Object> jsonData = Map.of("name", "John Doe", "age", 30);
        writeFile(jsonPath, "json", jsonData);

        String excelPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file23.xlsx";
String csvPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file1.csv";
        List<Map<String, String>> csvData = List.of(
            Map.of("ID", "1", "Name", "John", "Age", "30"),
            Map.of("ID", "2", "Name", "Alice", "Age", "25")
        );
        writeFile(csvPath, "csv", csvData);
        writeFile(excelPath, "excel", csvData, "Sheet1");

String wordPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.docx";
        writeFile(wordPath, "word", "This is a sample Word document.");
        String pdfPath = "C:\\Users\\kksuc\\OneDrive\\Desktop\\file.pdf";
        writeFile(pdfPath, "pdf", "This is a sample PDF file.");
    }
}
