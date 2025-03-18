package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.FieldDocument.Field.Xpath;
import org.checkerframework.checker.units.qual.s;
import org.apache.poi.ss.util.CellRangeAddress;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.github.javaparser.utils.Log;

import io.cucumber.java.en.Given;

public class xmltoExcelreprtCreation {
    
        Map<String, String> invoiceData = new HashMap<>();
	// Map to store extracted data
        public static Map<String,String> data = new HashMap<>();
// Set of elements to exclude from comparison
        private static final Set<String> EXCLUDED_ELEMENTS = new HashSet<>(Arrays.asList(
            "customerId", "sourceSystemId", "individualId", "membershipNo", 
            "invoiceDate", "coveragePeriod", "dueDate", "scanLine",
            "transactionRequestDttm", "batchNbr", "batchTransactionRequestDttm"));

  /*  @Given("Validate Invoice")
    public void validate_Invoice2() throws Throwable {
        try {
            // Define file paths and test bill ID
        File baseFile = new File("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/base.xml");
        File secondFile = new File("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/second.xml");
        String testBillID = "155365497059";

        // Parse XML files
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document baseDoc = dBuilder.parse(baseFile);
        Document secondDoc = dBuilder.parse(secondFile);
        baseDoc.getDocumentElement().normalize();
        secondDoc.getDocumentElement().normalize();

        System.out.println("Base XML Root Element: " + baseDoc.getDocumentElement().getNodeName());
        System.out.println("Second XML Root Element: " + secondDoc.getDocumentElement().getNodeName());

        // Get root elements
        Element baseRoot = baseDoc.getDocumentElement();
        Element secondRoot = getSpecificBillElement(secondDoc, testBillID);

        if (secondRoot != null) {
            // Create Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Comparison Report");
setupSheet(sheet);
// Create header row
                createHeaderRow(sheet, workbook);
 // Compare elements and write to Excel
                compareElements(baseRoot, secondRoot, sheet, workbook);
 // Write the output to a file
                try (FileOutputStream fileOut = new FileOutputStream("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/ComparisonReport.xlsx")) {
                    workbook.write(fileOut);
                }
                workbook.close();
            } else {
                System.out.println("Bill ID " + testBillID + " not found in the second XML file.");
            }
        } catch (Exception ex) {
            Log.error("Error in Opening ISB application. Exception: " + ex.getMessage());
        }
    
            } */
 
public static void main(String[] args) {
    try {
        // Define file paths and test bill ID
        File baseFile = new File("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/base.xml");
        File secondFile = new File("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/second.xml");
        String testBillID = "155365497059";

        // Parse XML files
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document baseDoc = dBuilder.parse(baseFile);
        Document secondDoc = dBuilder.parse(secondFile);
        baseDoc.getDocumentElement().normalize();
        secondDoc.getDocumentElement().normalize();

        System.out.println("Base XML Root Element: " + baseDoc.getDocumentElement().getNodeName());
        System.out.println("Second XML Root Element: " + secondDoc.getDocumentElement().getNodeName());

        // Get root elements
        Element baseRoot = baseDoc.getDocumentElement();
        Element secondRoot = getSpecificBillElement(secondDoc, testBillID);

        if (secondRoot != null) {
            // Create Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Comparison Report");
setupSheet(sheet);
// Create header row
                createHeaderRow(sheet, workbook);
 // Compare elements and write to Excel
                compareElements(baseRoot, secondRoot, sheet, workbook);
 // Write the output to a file
                try (FileOutputStream fileOut = new FileOutputStream("C:/Users/kkumaS46/IdeaProjects/ISB-Billing-Automation/ComparisonReport.xlsx")) {
                    workbook.write(fileOut);
                }
                workbook.close();
            } else {
                System.out.println("Bill ID " + testBillID + " not found in the second XML file.");
            }
        } catch (Exception ex) {
            Log.error("Error in Opening ISB application. Exception: " + ex.getMessage());
        }
    }
 /**
     * Retrieves the specific bill element from the XML document based on the test bill ID.
     */
    private static Element getSpecificBillElement(Document document, String testBillID) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String xpathExpression = "//root/documentCreationBatchRq[sourceSystemId='" + testBillID + "']";
        XPathExpression expr = xpath.compile(xpathExpression);
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        System.out.println("Number of nodes found: " + nodeList.getLength());

        if (nodeList.getLength() > 0) {
            return (Element) nodeList.item(0);
        }
        return null;
    }
 /**
     * Sets up the Excel sheet with default column widths and gridline settings.
     */
    private static void setupSheet(Sheet sheet) {
            sheet.setColumnWidth(0, 9000);
            sheet.setColumnWidth(1, 9000);
            sheet.setColumnWidth(2, 9000);
            sheet.setColumnWidth(3, 4000);
            sheet.setDisplayGridlines(false);
 }

    /**
     * Creates the header row in the Excel sheet.
     */
    private static void createHeaderRow(Sheet sheet, Workbook workbook) {
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            CellStyle headerStyle = workbook.createCellStyle();
            for (int i = 0; i < 4; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            }
            headerCell.setCellValue("Scenario 1");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));

            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerCell.setCellStyle(headerStyle);

            // Create sub-header row
            Row subHeaderRow = sheet.createRow(1);
            String[] subHeaders = {"Element", "Base", "Current", "Status"};
            CellStyle subHeaderStyle = workbook.createCellStyle();
            subHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            subHeaderStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
            subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            subHeaderStyle.setBorderTop(BorderStyle.THIN);
            subHeaderStyle.setBorderBottom(BorderStyle.THIN);
            subHeaderStyle.setBorderLeft(BorderStyle.THIN);
            subHeaderStyle.setBorderRight(BorderStyle.THIN);

            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setBold(true);
            subHeaderStyle.setFont(subHeaderFont);

            for (int i = 0; i < subHeaders.length; i++) {
                Cell cell = subHeaderRow.createCell(i);
                cell.setCellValue(subHeaders[i]);
                cell.setCellStyle(subHeaderStyle);
            }

          
}
/**

   
* Compares two XML elements and writes the comparison results to the Excel sheet.
     */

    private static void compareElements(Element elem1, Element elem2, Sheet sheet, Workbook workbook) {
        if (!elem1.getNodeName().equals(elem2.getNodeName())) {
            System.out.println("Different elements: " + elem1.getNodeName() + " vs " + elem2.getNodeName());
            return;
        }
        if ("custbmerlnfo".equals(elem1.getNodeName()) || "fulfilimentInfo".equals(elem1.getNodeName()))
         {
            Row row = sheet.createRow( sheet.getLastRowNum() + 1);

            CellStyle style = workbook.createCellStyle();
            // Apply border style to merged region
            
             for (int i =0; i <= 3; i++) {
            
            Cell mergedCell = row.createCell(i);
             mergedCell.setCellStyle(style);
            }
            Cell cell = row.createCell( 0);

            cell.setCellValue(elem1.getNodeName());

            style.setFillForegroundColor(IndexedColors.BLUE.getIndex());

            style.setFillPattern(FillPatternType.SOLID_FOREGROUND) ;

            style.setBorderTop(BorderStyle.THIN) ; 

            style.setBorderBottom(BorderStyle. THIN) ;

            style.setBorderLeft(BorderStyle. THIN) ;

            style.setBorderRight(BorderStyle. THIN) ;

            Font font = workbook.createFont();

            font.setBold(true);

            font.setColor(IndexedColors.WHITE.getIndex());

            style.setFont(font);

            cell.setCellStyle(style);

            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0,3));
        }

        NodeList children1 = elem1.getChildNodes();

        NodeList children2= elem2.getChildNodes();


        int length1 = children1.getLength();

        int length2 = children2.getLength();

        int i=0, j=0;


        while (i < length1 && j < length2) {

            Node child1 = children1.item(i);

            Node child2 = children2.item(j);

            if (child1.getNodeType() != Node.ELEMENT_NODE) {

                i++;

                continue;

            }

            if (child2.getNodeType() != Node.ELEMENT_NODE) {

                j++;

                continue;

            }


            System.out.println("Comparing child elements: " + child1.getNodeName()+ "vs " + child2.getNodeName());


            compareElements ((Element) child1, (Element) child2, sheet, workbook);


            i++;

            j++;

        }

        // Compare text content of elements
        String text1 = elem1.getTextContent().trim();
        String text2 = elem2.getTextContent().trim();
        String status = text1.equals(text2) ? "PASS" : "FAIL";

        if (!text1.isEmpty() || !text2.isEmpty()&& elem1 !=null && !EXCLUDED_ELEMENTS.contains(elem1.getNodeName())) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            createCellWithBorder(row,0,elem1.getNodeName(), workbook);
            createCellWithBorder(row,1,text1, workbook);
            createCellWithBorder(row,2,text2, workbook);
           Cell statusCell = createCellWithBorder(row,3,status, workbook);

            CellStyle passStyle = workbook.createCellStyle();
            Font passFont = workbook.createFont();
            passFont.setColor(IndexedColors.GREEN.getIndex());
            passStyle.setFont(passFont);

            passStyle.setBorderTop(BorderStyle.THIN);

            passStyle.setBorderBottom(BorderStyle.THIN);

            passStyle.setBorderLeft(BorderStyle.THIN);

            passStyle.setBorderRight (BorderStyle.THIN) ;

            CellStyle failStyle = workbook.createCellStyle();

            Font failFont = workbook.createFont();

            failFont.setColor(IndexedColors.RED.getIndex());

            failStyle.setFont(failFont);

            failStyle.setBorderTop(BorderStyle.THIN);

            failStyle.setBorderBottom(BorderStyle. THIN) ;

            failStyle.setBorderLeft(BorderStyle.THIN);

            failStyle.setBorderRight(BorderStyle. THIN);


            if ("PASS".equals(status)) {
                statusCell.setCellStyle(passStyle);
            } else if ("FAIL".equals(status)) {
                statusCell.setCellStyle(failStyle);
            }
            if (text1.equals(text2)) {

                System.out.println("Different text content in element " + elem1.getNodeName() + ": " + text1 + " vs " + text2);
            
	    }else {
                
                System.out.println("Text content in element " + elem1.getNodeName() + " is same in'both XMLs: " + text1);
            }

        }
                else if (text1 !="" || text2!="") {
                Row row = sheet.createRow( sheet.getLastRowNum() + 1);
                createCellWithBorder(row, 0, elem2.getNodeName(), workbook) ;
                createCellWithBorder(row, 1, text1, workbook);
                createCellWithBorder(row, 2, text2, workbook);
                Cell statusCell = createCellWithBorder(row,  3, status, workbook);
                
                // Apply styles based on status
                CellStyle passStyle = workbook.createCellStyle();
                Font passFont = workbook.createFont();
                passFont.setColor(IndexedColors.GREEN.getIndex());
                passStyle.setFont(passFont);
                
                passStyle.setBorderTop(BorderStyle. THIN);
                passStyle.setBorderBottom(BorderStyle. THIN);
                passStyle.setBorderLeft(BorderStyle.THIN);
                passStyle.setBorderRight (BorderStyle. THIN) ;
                CellStyle failStyle = workbook.createCellStyle();
                Font failFont = workbook.createFont();
                failFont.setColor(IndexedColors.RED.getIndex());
                failStyle.setFont(failFont);
                failStyle.setBorderTop(BorderStyle.THIN);
                failStyle.setBorderBottom(BorderStyle.THIN);
                failStyle.setBorderLeft(BorderStyle.THIN);
                failStyle.setBorderRight(BorderStyle.THIN);
                
                if ("PASS".equals(status)) 
                {
                statusCell.setCellStyle(passStyle);
                }
                 else if ("FAIL".equals(status)) {
                statusCell.setCellStyle(failStyle);
                 }
                System.out.println("Different text content in element "+ elem1.getNodeName() + ": " + text1 + " vs " + text2);
                }
                
                else
                {
                // Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                // row.createCell(0).setCellValue(elem2. getNodeName());
                
                // row.createcell(2).setCelZValue(text1);
                // row.createtell(3).setCellValue(text2);
                 System.out.println("Header" + elem1.getNodeName());
                }
                }
    /**
     * Creates a cell with a border and sets its value.
     */                
                private static Cell createCellWithBorder (Row row, int column, String value, Workbook workbook) {
                Cell cell = row.createCell(column);
                cell.setCellValue(value);
                
                CellStyle style = workbook.createCellStyle();
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(style);
                return cell;
                }

                private static String getTextContentExcludingChildren(Element element) {
                NodeList children = element.getChildNodes(); 
                StringBuilder textContent = new StringBuilder();
                for (int i = 0; i < children.getLength(); i++) {
                
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                textContent.append(child.getTextContent());
                }
            }
                return textContent.toString();
                
            }
                
                
      /*                        @Given("Validate InvoiceTM")
                public void validate_Invoice() throws Throwable {
                try {
                 // Validate invoice data and store the result in the invoiceData map
                invoiceData = validateInvoiceData(".xml", "currentBillId");
                
                
            
               } catch (Exception ex) {
                // Log an error message if an exception occurs
                Log.error("Error in Opening ISB application. Exception :" + ex.getMessage());
                
                }
            }

            
            //  Validates invoice data by parsing the XML file and extracting relevant information.
            //  @param filepath   The path to the XML file.
            //  @param testBillID The ID of the bill to validate.
            //  @return A map containing extracted invoice data.
            
             public static Map<String, String> validateInvoiceData(String filepath, String TestBillID) {
                try {

                    // Clear the data map before processing
                data.clear();

                // Parse the XML document
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(filepath));
                document.getDocumentElement().normalize(); 

                // Use XPath to locate the relevant nodes
                XPath xpath = XPathFactory.newInstance().newXPath();
                String xpathExpression = "//root/documentCreationBatchRq";
                XPathExpression expr = xpath.compile(xpathExpression);
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);


        // Process the nodes if they exist
                if (nodeList.getLength() > 0) {
                
                for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                Element eElement = (Element) nNode;

                // Extract the bill ID and compare it with the testBillID
                String billid = eElement.getElementsByTagName("sourcesystemld").item( 0).getTextContent();
                if (TestBillID.equalsIgnoreCase(billid)) {

                    // Construct XPath for the specific bill
                String documentCreationRq = "//root/documentCreationBatchRq[" + (i +1) +"]";
                
                NodeList nodeList1 = (NodeList) xpath.compile(documentCreationRq).evaluate(document, XPathConstants.NODESET);
                

                // Extract values from the matched node
                Node nNode2 = nodeList1.item(0);
                Element eElement2 = (Element) nNode2;
              
                extractValues(eElement2, xpath,""); 
              
                break;
                
                }
            }
                // if (count > 0) {
                // HtmlReport.AddSteps("Validate Bill Details in the extracted letter”,
                // else {
                // HtmlReport.AddSteps("Validate Bill Details in the extracted letter®
                // }                
        }
                
                return data;
                } catch (Exception e) {

                    // Log the exception and return an empty map
        Log.error("Error while validating invoice data: " + e.getMessage());
        
               

        return new HashMap<>();
 
                }
            }

            
            //  Recursively extracts values from an XML element and stores them in the data map.
            //  @param element    The XML element to process.
            //  @param xpath      The XPath object for evaluating expressions.
            //  @param parentPath The parent path for constructing keys in the map.
            
            private static void extractValues (Element element, XPath xpath, String parentPath) throws Exception{
                NodeList children = element.getChildNodes();
                String parentTagName = element.getNodeName();
                for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                // Process only element nodes
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String tagName = childElement.getNodeName();
                String fullPath = parentPath.isEmpty() ? tagName : parentPath + "_" +tagName;
 
                // Check if the child element contains only text
                if (childElement.getChildNodes().getLength()== 1&& childElement.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                String value = childElement.getTextContent().trim();
 
                // Handle duplicate keys by appending the parent tag name
                if (data.containsKey(fullPath)){
                    fullPath = parentPath.isEmpty() ? tagName : parentPath +"_"+ parentTagName +"_"+ tagName;
                    data.put(fullPath, value);
                    System.out.println("Extracted value: " + fullPath + " = " + value);
                }
             

                // Store the extracted value in the map
                    data.put(fullPath, value);
                    System.out.println("Extracted value: " + fullPath + " = " + value);
                
                
                

                }else {

                    // Recursively process child elements
                    extractValues(childElement, xpath, fullPath);
                } 
            }
        }
    }*/
}