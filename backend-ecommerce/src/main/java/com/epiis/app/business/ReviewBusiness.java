package com.epiis.app.business;

import com.epiis.app.dto.DtoReview;
import com.epiis.app.entity.Product;
import com.epiis.app.entity.Review;
import com.epiis.app.entity.User;
import com.epiis.app.repository.OrderRepository;
import com.epiis.app.repository.ProductRepository;
import com.epiis.app.repository.ReviewRepository;
import com.epiis.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewBusiness {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public boolean checkEligibility(String idUser, String idProduct) {
        //return orderRepository.existsByUserIdAndProductId(idUser, idProduct);
        boolean exists = orderRepository.existsByUserIdAndProductId(idUser, idProduct);
        System.out.println("Checking eligibility for User: " + idUser + ", Product: " + idProduct);
        System.out.println("Result: " + exists);
        return exists;
    }

    @Transactional
    public void insert(DtoReview dtoReview) {
        // Validation: Verify if the user has purchased the product
        boolean hasPurchased = orderRepository.existsByUserIdAndProductId(dtoReview.getIdUser(),
                dtoReview.getIdProduct());
        if (!hasPurchased) {
            throw new RuntimeException("Solo puedes reseñar productos que has comprado y han sido entregados.");
        }
        Review review = new Review();
        review.setIdReview(UUID.randomUUID().toString());

        User user = userRepository.findById(dtoReview.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(dtoReview.getIdProduct())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setUser(user);
        review.setProduct(product);
        review.setRating(dtoReview.getRating());
        review.setComment(dtoReview.getComment());
        //review.setCreatedAt(new java.sql.Timestamp(dtoReview.getCreatedAt().getTime()));
        //review.setUpdatedAt(new java.sql.Timestamp(dtoReview.getUpdatedAt().getTime()));
        review.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        review.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));


        reviewRepository.save(review);

        // Update Product Average Rating
        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        Double avg = reviewRepository.getAverageRating(product.getIdProduct());
        if (avg != null) {
            product.setAverageRating(BigDecimal.valueOf(avg));
        } else {
            product.setAverageRating(BigDecimal.ZERO);
        }

        // Update review count as well
        List<Review> reviews = reviewRepository.findByProduct_IdProduct(product.getIdProduct());
        product.setReviewCount(reviews.size());

        productRepository.save(product);
    }

    public List<DtoReview> getByProduct(String idProduct) {
        List<Review> reviews = reviewRepository.findByProduct_IdProduct(idProduct);
        List<DtoReview> dtoReviews = new ArrayList<>();

        for (Review review : reviews) {
            DtoReview dto = new DtoReview();
            dto.setIdReview(review.getIdReview());
            dto.setIdUser(review.getUser().getIdUser());
            dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
            dto.setIdProduct(review.getProduct().getIdProduct());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setUpdatedAt(review.getUpdatedAt());

            dtoReviews.add(dto);
        }

        return dtoReviews;
    }
}
