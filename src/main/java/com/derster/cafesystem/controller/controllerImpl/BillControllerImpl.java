package com.derster.cafesystem.controller.controllerImpl;

import com.derster.cafesystem.constants.CafeConstants;
import com.derster.cafesystem.controller.BillController;
import com.derster.cafesystem.pojo.Bill;
import com.derster.cafesystem.service.BillService;
import com.derster.cafesystem.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class BillControllerImpl implements BillController {
    private final BillService billService;

    public BillControllerImpl(BillService billService) {
        this.billService = billService;
    }

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {

        try {
            return billService.generateReport(requestMap);

        }catch (Exception e){
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBill() {
        try {
            return billService.getBill();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> delete(Integer id) {
        try {
            return billService.delete(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
