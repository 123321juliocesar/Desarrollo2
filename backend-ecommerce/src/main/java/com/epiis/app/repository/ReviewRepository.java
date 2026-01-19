package com.epiis.app.repository;

import com.epiis.app.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByProduct_IdProduct(String idProduct);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.idProduct = :idProduct")
    Double getAverageRating(String idProduct);
}
