package com.derster.cafesystem.controller;

import com.derster.cafesystem.pojo.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/bill")
public interface BillController {
    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap);

    @GetMapping("/getBills")
    ResponseEntity<List<Bill>> getBill();

    @PostMapping("/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap);

    @PostMapping("/delete/{id}")
    ResponseEntity<String> delete(@PathVariable Integer id);
}
