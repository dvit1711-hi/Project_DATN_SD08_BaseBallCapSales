package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("select coalesce(avg(r.rating), 0) from Review r")
    Double averageRating();

    @Query("select r.productID.id, avg(r.rating), count(r) from Review r group by r.productID.id order by avg(r.rating) desc")
    List<Object[]> ratingByProduct();

    @Query("select r.rating, count(r) from Review r group by r.rating order by r.rating desc")
    List<Object[]> distributionByStar();
}
