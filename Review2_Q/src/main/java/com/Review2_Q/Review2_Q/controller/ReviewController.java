package com.Review2_Q.Review2_Q.controller;

import com.Review2_Q.Review2_Q.model.Review;
import com.Review2_Q.Review2_Q.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService service;

    @GetMapping(value = "/{reviewId}")
    public ResponseEntity<Review> findOne(@PathVariable("reviewId") final String reviewId){
        Review review = service.getReviewById(reviewId);
        if (review==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found");
        }
        return ResponseEntity.ok().body(review);
    }

    @GetMapping(value = "/")
    public List<Review> getAllReviews(){
        return service.getAllReviews();
    }

    @GetMapping(value = "/myReviews")
    public Iterable<Review> getAllMyReviews() {
        return service.getAllMyReviews();
    }

    @GetMapping(value = "/{sku}/product")
    public List<Review> getAllReviewsBySku(@PathVariable("sku")final String sku){
        return service.getAllReviewsBySku(sku);
    }


    @GetMapping(value = "/{skuProducts}/votes")
    public Iterable<Review> getReviewsByProductOrderByVotes(@PathVariable("skuProducts") final String skuProducts) throws IOException, InterruptedException {
        return service.getReviewsByProductOrderByVotes(skuProducts);
    }

    @GetMapping(value = "/pending")
    public Iterable<Review> getAllPendingReviews(){
        return service.getAllPendingReviews();
    }

    @GetMapping(value = "/status/{reviewId}")
    public String getStatus(@PathVariable ("reviewId") final String reviewId){
        return service.getStatus(reviewId);
    }
}
