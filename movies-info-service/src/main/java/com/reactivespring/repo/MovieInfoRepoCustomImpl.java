package com.reactivespring.repo;

import com.reactivespring.domain.MovieInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class MovieInfoRepoCustomImpl implements MovieInfoRepoCustom {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<Long> getCountByYear(Integer year) {
        MatchOperation matchStage = match(new Criteria("year").is(year));
        GroupOperation groupStage = group("movieInfoId").count()
                .as("totalMovies");
        ProjectionOperation projectStage = project("totalMovies").andExclude("_id");
        Aggregation aggregation = newAggregation(matchStage, groupStage, projectStage);
        return reactiveMongoTemplate.aggregate(aggregation, "movieInfo", MovieProjection.class)
                .map(MovieProjection::getTotalMovies).log();
    }

    @Override
    public Flux<MovieInfo> filterMovies(String name, Integer year, String cast, LocalDate releaseStartDate, LocalDate releaseEndDate) {
        Aggregation aggregation = newAggregation(
                buildMatchStage(name, year, cast, releaseStartDate, releaseEndDate),
                new ProjectionOperation().andExclude("_id") // Optional: Exclude _id in the response
        );

        return reactiveMongoTemplate.aggregate(aggregation, "movieInfo", MovieInfo.class);
    }

    private MatchOperation buildMatchStage(String name, Integer year, String cast, LocalDate releaseStartDate, LocalDate releaseEndDate) {
        Criteria criteria = new Criteria();

        // Apply filters dynamically based on provided query parameters
        if (name != null && !name.isEmpty()) {
            criteria.and("name").regex(name, "i");  // Case-insensitive search
        }

        if (year != null) {
            criteria.and("year").is(year);
        }

        if (cast != null && !cast.isEmpty()) {
            criteria.and("cast").regex(cast, "i");  // Case-insensitive search in the cast list
        }

        if (releaseStartDate != null && releaseEndDate != null) {
            criteria.and("release_date").gte(releaseStartDate).lte(releaseEndDate);
        }

        return Aggregation.match(criteria);  // Return the match stage with the dynamic criteria
    }


    @Setter
    @Getter
    public static class MovieProjection {
        private Long totalMovies;
    }

}
