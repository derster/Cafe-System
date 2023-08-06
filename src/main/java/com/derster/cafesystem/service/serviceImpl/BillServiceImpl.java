package com.derster.cafesystem.service.serviceImpl;

import com.derster.cafesystem.constants.CafeConstants;
import com.derster.cafesystem.dao.BillDao;
import com.derster.cafesystem.jwt.JwtFilter;
import com.derster.cafesystem.pojo.Bill;
import com.derster.cafesystem.pojo.Category;
import com.derster.cafesystem.service.BillService;
import com.derster.cafesystem.utils.CafeUtils;
import com.google.common.base.Strings;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
    JwtFilter jwtFilter;
    BillDao billDao;

    public BillServiceImpl(JwtFilter jwtFilter, BillDao billDao) {
        this.jwtFilter = jwtFilter;
        this.billDao = billDao;
    }

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            String fileName;
            
            if (validateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate"))
                {
                    fileName = (String) requestMap.get("uuid");
                }else{
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                String data = "Name:"+requestMap.get("name") +"\n"+"Contact Number: "+requestMap.get("contactNumber")+
                        "\n"+"Email: "+requestMap.get("email")+ "\n"+"Payment method: "+requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION+"/"+fileName+".pdf"));
                document.open();
                setRectangularInPdf(document);
                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data+"\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));

                for (int i=0; i<jsonArray.length(); i++){
                    addRow(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : "+requestMap.get("totalAmount")+ "\n"
                +"Thank you for your visit. Please visit again !!", getFont("Data"));
                document.add(footer);
                document.close();

                return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity(CafeConstants.REQUIRED_DATA_NOT_FOUND, HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBill() {

        List<Bill> list = new ArrayList<>();

        try {

            if (jwtFilter.isAdmin()){
               list = billDao.getAllBills();
            }else{
                list =  billDao.getBillByCreatedBy(jwtFilter.getCurrentUser());
            }

            return new ResponseEntity<>(list, HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {

        try {
           byte[] byteArray = new byte[0];
           if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
               return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
           String filePath = CafeConstants.STORE_LOCATION+"/"+requestMap.get("uuid")+".pdf";
           if (CafeUtils.isFileExist(filePath)){
               byteArray = getByteArray(filePath);
           }else {
               requestMap.put("isGenearte", false);
               generateReport(requestMap);
               byteArray = getByteArray(filePath);
           }
            return new ResponseEntity<>(byteArray, HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> delete(Integer id) {
        try {
             if (jwtFilter.isAdmin()){
                 Optional optional = billDao.findById(id);

                 if (optional.isPresent()){
                     billDao.deleteById(id);
                     return CafeUtils.getResponseEntity("Bill deleted successfully", HttpStatus.OK);
                 }else{
                     return CafeUtils.getResponseEntity("Bill not exist", HttpStatus.OK);
                 }
             }
        }catch (Exception e){
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("inside addRow");

        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));

    }

    private void addTableHeader(PdfPTable table) {
        log.info("inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    //header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("inside getFont");

        switch (type){
            case "Header":
                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                    headerFont.setStyle(Font.BOLD);
                    return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();

        }
    }

    private void setRectangularInPdf(Document document) throws DocumentException {
        log.info("inside setRectangularInPdf");

        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotalAmount(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }
}
