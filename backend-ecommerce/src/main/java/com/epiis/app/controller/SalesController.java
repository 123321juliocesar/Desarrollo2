package com.epiis.app.controller;


import com.epiis.app.business.SalesBusiness;
import com.epiis.app.dto.DtoSalesReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {

    @Autowired
    private SalesBusiness salesBusiness;

    @GetMapping("/report")
    public ResponseEntity<?> getSalesReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Date start = parseDate(startDate, false);
            Date end = parseDate(endDate, true);
            DtoSalesReport report = salesBusiness.getSalesReport(start, end, page, size);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<?> exportPdf(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            Date start = parseDate(startDate, false);
            Date end = parseDate(endDate, true);
            byte[] pdfBytes = salesBusiness.generatePdfReport(start, end);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "sales_report_" + startDate + "_to_" + endDate + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error generating PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

     @Autowired
    private com.epiis.app.business.DashboardBusiness dashboardBusiness;

    /* 
    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboardSummary() {
        return ResponseEntity.ok(dashboardBusiness.getDashboardSummary());
    }*/
   @GetMapping("/dashboard-summary")
    public ResponseEntity<java.util.Map<String, Object>> getDashboardSummary() {
        com.epiis.app.dto.DtoDashboardSummary dto = dashboardBusiness.getDashboardSummary();
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("totalUsers", dto.getTotalUsers());
        map.put("todaysRevenue", dto.getTodaysRevenue());
        map.put("pendingOrders", dto.getPendingOrders());
        map.put("totalProducts", dto.getTotalProducts());
        map.put("debug_message", "Returned via Map");

        System.out.println("DEBUG DASHBOARD: Users=" + dto.getTotalUsers() + ", Products=" + dto.getTotalProducts());
        return ResponseEntity.ok(map);
    }


    private Date parseDate(String dateStr, boolean endOfDay) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateStr);
        if (endOfDay) {
            // Set to end of day 23:59:59.999
            date.setTime(date.getTime() + (24 * 60 * 60 * 1000) - 1);
        }
        return date;
    }
}
