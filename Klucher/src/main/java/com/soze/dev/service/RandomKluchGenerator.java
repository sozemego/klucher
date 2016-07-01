package com.soze.dev.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

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
public class RandomKluchGenerator {

  private final List<String> randomKluchs = new ArrayList<>();
  private final List<Character> vowels = new ArrayList<>(
      Arrays.asList('a', 'e', 'i', 'o', 'u'));
  private final List<Character> consonants = new ArrayList<>(
      Arrays.asList('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p',
          'q', 'r', 's', 't', 'v', 'x', 'z'));
  private final Random random = new Random();
  private final AtomicInteger atomicInteger = new AtomicInteger();

  @PostConstruct
  public void init() {
    for (int i = 0; i < 200; i++) {
      randomKluchs.add(generateRandomKluchText());
    }
  }

  private String generateRandomKluchText() {
    int length = random.nextInt(250) + 1;
    StringBuilder sb = new StringBuilder(length);
    CharacterType type = CharacterType.NONE;
    for (int i = 0; i < length; i++) {
      type = determineCharacterType(type);
      sb.append(nextCharacter(type));
    }
    return sb.toString();
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
      case NONE:  return CharacterType.CONSONANT;
      case VOWEL: return getAfterVowel();
      case CONSONANT: return getAfterConsonant();
      case SPACE: return CharacterType.CONSONANT;
      case PERIOD: return CharacterType.SPACE;
      default: return CharacterType.CONSONANT;
    }
  }
  
  private Character nextCharacter(CharacterType currentType) {
    switch (currentType) {
      case NONE:  return null;
      case VOWEL: return getRandomVowel();
      case CONSONANT: return getRandomConsonant();
      case SPACE: return ' ';
      case PERIOD: return '.';
      default: return null;
    }
  }
  
  private CharacterType getAfterVowel() {
    float roll = (float) Math.random();
    if(roll < 0.15f) {
      return CharacterType.SPACE;
    }
    if(roll > 0.15f && roll < 0.8f) {
      return CharacterType.CONSONANT;
    }
    return CharacterType.PERIOD;
  }
  
  private CharacterType getAfterConsonant() {
    float roll = (float) Math.random();
    if(roll < 0.15f) {
      return CharacterType.SPACE;
    }
    if(roll > 0.15f && roll < 0.8f) {
      return CharacterType.VOWEL;
    }
    return CharacterType.PERIOD;
  }

  private enum CharacterType {
    NONE, VOWEL, CONSONANT, SPACE, PERIOD;
  }

}
