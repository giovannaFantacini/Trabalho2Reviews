package com.Review2_Q.Review2_Q.services;

import com.Review2_Q.Review2_Q.model.Review;
import java.io.IOException;
import java.util.List;

public interface ReviewService {

    Review getReviewById(String reviewId);

    List<Review> getAllReviews() ;

    List<Review> getAllMyReviews();

    List<Review> getAllReviewsBySku(String sku);

    List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException;

    List<Review> getAllPendingReviews() ;

    String getStatus(String reviewId);

}
