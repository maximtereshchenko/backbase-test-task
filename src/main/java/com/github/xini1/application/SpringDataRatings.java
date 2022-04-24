package com.github.xini1.application;

import com.github.xini1.application.entity.RatingEntity;
import com.github.xini1.port.Ratings;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Maxim Tereshchenko
 */
final class SpringDataRatings implements Ratings {

    private final RatingsRepository ratingsRepository;

    SpringDataRatings(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    @Override
    public void tryAdd(String apiToken, String imdbId, int rating) {
        var ratingEntity = new RatingEntity();
        ratingEntity.setApiToken(apiToken);
        ratingEntity.setImdbId(imdbId);
        ratingEntity.setRating(rating);
        ratingsRepository.save(ratingEntity);
    }

    @Override
    public Map<String, Integer> top10() {
        return ratingsRepository.sortedByRating(PageRequest.of(0, 10))
                .stream()
                .collect(toMap());
    }

    @Override
    public int average(String imdbId) {
        return ratingsRepository.average(imdbId)
                .map(Math::round)
                .orElse(0);
    }

    private Collector<RatingsRepository.TopRatedProjection, ?, Map<String, Integer>> toMap() {
        return Collectors.toMap(
                RatingsRepository.TopRatedProjection::getImdbId,
                topRatedProjection -> Math.round(topRatedProjection.getRating())
        );
    }
}