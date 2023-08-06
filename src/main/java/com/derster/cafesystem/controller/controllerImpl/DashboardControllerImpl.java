package com.derster.cafesystem.controller.controllerImpl;

import com.derster.cafesystem.controller.DashboardController;
import com.derster.cafesystem.service.serviceImpl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardControllerImpl implements DashboardController {

    DashboardService dashboardService;

    public DashboardControllerImpl(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
