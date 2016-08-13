package com.soze.dev.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A service which generates random Kluchs after construction. Those Kluchs at
 * least attempt to look like sentences, but the method of their generation is
 * very primitive.
 * 
 * @author sozek
 *
 */
@Service
@Profile("dev")
public class RandomKluchGenerator {

  private static final Logger log = LoggerFactory.getLogger(RandomKluchGenerator.class);
  private final List<String> randomKluchs = new ArrayList<>();
  private final List<String> randomKluchsWithHashtags = new ArrayList<>();
  private final List<String> allHashtagKluch = new ArrayList<>();
  private final List<Character> vowels = new ArrayList<>(
      Arrays.asList('a', 'e', 'i', 'o', 'u'));
  private final List<Character> consonants = new ArrayList<>(
      Arrays.asList('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p',
          'q', 'r', 's', 't', 'v', 'x', 'z'));
  private final Random random = new Random();
  private final AtomicInteger atomicInteger = new AtomicInteger();
  
  @PostConstruct
  public void init() {
    log.info("Generating random kluchs.");
    Set<String> kluchSet = new HashSet<>();
    for (int i = 0; i < 25000; i++) {
      kluchSet.add(generateRandomKluchTextNoHashtags());
    }
    randomKluchs.addAll(kluchSet);
    kluchSet.clear();
    for (int i = 0; i < 25000; i++) {
      kluchSet.add(generateRandomKluchTextWithHashtags());
    }
    randomKluchsWithHashtags.addAll(kluchSet);
    kluchSet.clear();
    for (int i = 0; i < 25000; i++) {
      kluchSet.add(generateAllHashtagKluch());
    }
    allHashtagKluch.addAll(kluchSet);
    log.info("Generated [{}] random kluchs. ", (randomKluchs.size() + randomKluchsWithHashtags.size() + allHashtagKluch.size()));
  }

  private String generateRandomKluchTextNoHashtags() {
    return generateKluch(0f);
  }
  
  private String generateRandomKluchTextWithHashtags() {
    return generateKluch(0.1f);
  }
  
  private String generateAllHashtagKluch() {
    return generateKluch(1f);
  }
  
  private String generateKluch(float hashtagChance) {
    int kluchLength = randomInt(16, 250);
    StringBuilder sb = new StringBuilder(kluchLength);
    while (sb.length() < kluchLength) {
      String word = generateRandomWord(hashtagChance);
      if (sb.length() + word.length() > kluchLength) {
        break;
      } else {
        sb.append(word);
      }
    }
    return sb.toString();
  }
  
  private String generateRandomWord(float hashtagChance) {
    float endOfSentenceChance = (float) Math.random();
    if(endOfSentenceChance < 0.15f) {
      return ". ";
    }
    float roll = (float) Math.random();
    if(roll <= hashtagChance) {
      return generateHashtag();
    } else {
      return generateRandomWord();
    }
  }
  
  private String generateHashtag() {
    String pound = "#";
    String word = generateRandomWord();
    return pound + word;
  }
  
  private String generateRandomWord() {
    int wordLength = randomInt(1, 10);
    StringBuilder sb = new StringBuilder(wordLength);
    CharacterType type = CharacterType.CONSONANT;
    while(sb.length() < wordLength) {
      type = determineCharacterType(type);
      sb.append(nextCharacter(type));
    }
    return sb.toString();
  }

  private Character getRandomVowel() {
    int randomIndex = random.nextInt(vowels.size() - 1);
    return vowels.get(randomIndex);
  }

  private Character getRandomConsonant() {
    int randomIndex = random.nextInt(consonants.size() - 1);
    return consonants.get(randomIndex);
  }

  private CharacterType determineCharacterType(CharacterType currentType) {
    switch (currentType) {
      case VOWEL: return getAfterVowel();
      case CONSONANT: return getAfterConsonant();
      default: return CharacterType.CONSONANT;
    }
  }
  
  private Character nextCharacter(CharacterType currentType) {
    switch (currentType) {
    case VOWEL:
      return getRandomVowel();
    case CONSONANT:
      return getRandomConsonant();
    default:
      return null;
    }
  }

  private CharacterType getAfterVowel() {
    return CharacterType.CONSONANT;
  }

  private CharacterType getAfterConsonant() {
    return CharacterType.VOWEL;
  }

  private enum CharacterType {
    VOWEL, CONSONANT;
  }
  
  private int randomInt(int min, int max) {
    return random.nextInt((max - min) + 1) + min;
  }
  
  public String getRandomKluch() {
    int randomIndex = random.nextInt(randomKluchs.size() - 1);
    return randomKluchs.get(randomIndex);
  }
  
  public String getCurrentTimestamp() {
    Timestamp timestamp = new Timestamp(Instant.now().toEpochMilli());
    return timestamp.toString();
  }
  
  public String getUniqueIdAsText() {
    return "" + atomicInteger.getAndIncrement();
  }
  
  public String getRandomKluchWithSomeHashtags() {
    int randomIndex = random.nextInt(randomKluchsWithHashtags.size() - 1);
    return randomKluchsWithHashtags.get(randomIndex);
  }
  
  public String getAllHashtagKluch() {
    int randomIndex = random.nextInt(allHashtagKluch.size() - 1);
    return allHashtagKluch.get(randomIndex);
  }

}
