package com.epiis.app.controller;

import com.epiis.app.business.ReviewBusiness;
import com.epiis.app.dto.DtoReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewBusiness reviewBusiness;

    @PostMapping("/insert")
    public void insert(@RequestBody DtoReview dtoReview) {
        reviewBusiness.insert(dtoReview);
    }

    @GetMapping("/getbyproduct/{idProduct}")
    public List<DtoReview> getByProduct(@PathVariable String idProduct) {
        return reviewBusiness.getByProduct(idProduct);
    }

    @GetMapping("/check-eligibility")
    public boolean checkEligibility(@RequestParam String idUser, @RequestParam String idProduct) {
        return reviewBusiness.checkEligibility(idUser, idProduct);
    }
}
