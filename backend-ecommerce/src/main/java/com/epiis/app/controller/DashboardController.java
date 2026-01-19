package com.epiis.app.controller;

import com.epiis.app.business.DashboardBusiness;
import com.epiis.app.dto.DtoDashboardSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardBusiness dashboardBusiness;

    @GetMapping("/summary")
    public ResponseEntity<DtoDashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardBusiness.getDashboardSummary());
    }
}
