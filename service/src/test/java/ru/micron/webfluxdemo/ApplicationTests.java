package ru.micron.webfluxdemo;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import ru.micron.webfluxdemo.model.Tweet;
import ru.micron.webfluxdemo.repository.TweetRepository;
import org.assertj.core.api.Assertions;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

  @Autowired private WebTestClient webTestClient;

  @Autowired TweetRepository tweetRepository;

  @Test
  public void testCreateTweet() {
    Tweet tweet = new Tweet().setText("This is a test");

    webTestClient
        .post()
        .uri("/tweets")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just(tweet), Tweet.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.text")
        .isEqualTo("This is a test");
  }

  @Test
  public void testGetAllTweets() {
    webTestClient
        .get()
        .uri("/tweets")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBodyList(Tweet.class);
  }

  @Test
  public void testGetSingleTweet() {
    Tweet tweet = tweetRepository.save(new Tweet().setText("Hello, World!")).block();

    webTestClient
        .get()
        .uri("/tweets/{id}", Collections.singletonMap("id", tweet.getId()))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .consumeWith(response -> Assertions.assertThat(response.getResponseBody()).isNotNull());
  }

  @Test
  public void testUpdateTweet() {
    Tweet tweet = tweetRepository.save(new Tweet().setText("Initial Test")).block();
    Tweet newTweetData = new Tweet().setText("Updated Tweet");

    webTestClient
        .put()
        .uri("/tweets/{id}", Collections.singletonMap("id", tweet.getId()))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just(newTweetData), Tweet.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody()
        .jsonPath("$.text")
        .isEqualTo("Updated Tweet");
  }

  @Test
  public void testDeleteTweet() {
    Tweet tweet = tweetRepository.save(new Tweet().setText("To be deleted")).block();

    webTestClient
        .delete()
        .uri("/tweets/{id}", Collections.singletonMap("id", tweet.getId()))
        .exchange()
        .expectStatus()
        .isOk();
  }
}
