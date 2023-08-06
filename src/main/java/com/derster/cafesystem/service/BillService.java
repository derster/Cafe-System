package com.derster.cafesystem.service;

import com.derster.cafesystem.pojo.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {
    ResponseEntity<String> generateReport(Map<String, Object> requestMap);

    ResponseEntity<List<Bill>> getBill();

    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);

    ResponseEntity<String> delete(Integer id);
}
