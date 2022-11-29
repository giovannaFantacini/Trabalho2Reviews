package com.Review2_C.Review2_C.services;


import com.Review2_C.Review2_C.RabbitMQ.RabbitMQPublisher;
import com.Review2_C.Review2_C.repository.ProductRepository;
import com.Review2_C.Review2_C.repository.ReviewRepository;
import com.Review2_C.Review2_C.repository.VoteRepository;
import com.Review2_C.Review2_C.security.JwtUtils;
import com.Review2_C.Review2_C.model.Review;
import com.Review2_C.Review2_C.model.ReviewDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQPublisher jsonProducer;

    @Override
    public Review create(ReviewDTO rev) throws IOException, InterruptedException {
        boolean isPresent = productRepository.isPresent(rev.getSku());
        if(isPresent){
            Long userId = Long.valueOf(jwtUtils.getUserFromJwtToken(jwtUtils.getJwt()));
            final Review obj = Review.newFrom(rev,userId);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(obj);
            jsonProducer.sendJsonMessageToCreate(json);
            repository.save(obj);
            return obj;
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product doesn't exist");
        }

    }

    @Override
    public Boolean approveRejectReview(String reviewId, Boolean status){
        Review review = repository.getReviewById(reviewId);
        try {
            if (Objects.equals(review.getStatus(), "PENDING")) {
                if (status) {
                    review.setStatus("APPROVED");
                } else {
                    review.setStatus("REJECTED");
                }
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(review);
                jsonProducer.sendJsonMessageToChangeStatus(json);
                repository.save(review);
                return true;
            }else {
                return false;
            }
        }catch (NullPointerException e){
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public Boolean deleteReview(String reviewId) throws IOException, InterruptedException {

        var votes = voteRepository.ReviewIsVoted(reviewId);
        Long userId = Long.valueOf(jwtUtils.getUserFromJwtToken(jwtUtils.getJwt()));
        Review review = repository.getReviewById(reviewId);
        if(review == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review doesn't exist");
        }
        if (votes == false && Objects.equals(review.getUserId(), userId)) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(review);
            jsonProducer.sendJsonMessageToDelete(json);
            repository.delete(review);
            return true;
        }else
            return false;
    }

}



