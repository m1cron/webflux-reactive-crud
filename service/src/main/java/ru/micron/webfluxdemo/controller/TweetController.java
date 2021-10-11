package ru.micron.webfluxdemo.controller;

import ru.micron.webfluxdemo.model.Tweet;
import ru.micron.webfluxdemo.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TweetController {

  private final TweetRepository tweetRepository;

  /**
   * Return is similar to Server-Sent-Events but without extra information. Ex:
   * {"id":"59ba5389d2b2a85ed4ebdafa","text":"tweet1","createdAt":1505383305602}
   * {"id":"59ba5587d2b2a85f93b8ece7","text":"tweet2","createdAt":1505383814847}
   *
   * @return
   */
  @GetMapping("/tweets")
  public Flux<Tweet> getAllTweets() {
    return tweetRepository.findAll();
  }

  @PostMapping("/tweets")
  public Mono<Tweet> createTweets(@RequestBody Tweet tweet) {
    return tweetRepository.save(tweet);
  }

  @GetMapping("/tweets/{id}")
  public Mono<ResponseEntity<Tweet>> getTweetById(@PathVariable(value = "id") String tweetId) {
    return tweetRepository
        .findById(tweetId)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PutMapping("/tweets/{id}")
  public Mono<ResponseEntity<Tweet>> updateTweet(
      @PathVariable(value = "id") String tweetId, @RequestBody Tweet tweet) {
    return tweetRepository
        .findById(tweetId)
        .flatMap(
            existingTweet -> {
              existingTweet.setText(tweet.getText());
              return tweetRepository.save(existingTweet);
            })
        .map(updatedTweet -> new ResponseEntity<>(updatedTweet, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/tweets/{id}")
  public Mono<ResponseEntity<Void>> deleteTweet(@PathVariable(value = "id") String tweetId) {
    return tweetRepository
        .findById(tweetId)
        .flatMap(
            existingTweet ->
                tweetRepository
                    .delete(existingTweet)
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Send tweets to the client as Server Sent Events Ex: data:
   * {"id":"59ba5389d2b2a85ed4ebdafa","text":"tweet1","createdAt":1505383305602} data:
   * {"id":"59ba5587d2b2a85f93b8ece7","text":"tweet2","createdAt":1505383814847}
   *
   * @return
   */
  @GetMapping(value = "/stream/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Tweet> streamAllTweets() {
    return tweetRepository.findAll();
  }
}
