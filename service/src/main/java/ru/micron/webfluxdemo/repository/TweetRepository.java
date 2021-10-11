package ru.micron.webfluxdemo.repository;

import ru.micron.webfluxdemo.model.Tweet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {}
