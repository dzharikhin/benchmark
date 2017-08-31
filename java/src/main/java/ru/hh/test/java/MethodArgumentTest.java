package ru.hh.test.java;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

public class MethodArgumentTest {

  private static List<Map.Entry<String, String>> testStream(
    Integer countryId,
    Integer employerId,
    List<Integer> priceRegionIds,
    List<Integer> professionalAreaIds
  ) {
    List<Map.Entry<String, String>> result = new ArrayList<>();
    result.add(new AbstractMap.SimpleEntry<>("countryId", countryId.toString()));
    List<Map.Entry<String, String>> val = Stream.of(
      Stream.of(employerId).filter(Objects::nonNull).map(id -> new AbstractMap.SimpleEntry<>("employerId", id.toString())),
      priceRegionIds.stream().filter(Objects::nonNull).map(id -> new AbstractMap.SimpleEntry<>("priceRegionIds", id.toString())),
      professionalAreaIds.stream().filter(Objects::nonNull).map(id -> new AbstractMap.SimpleEntry<>("profAreaIds", id.toString()))
    ).flatMap(Function.identity()).collect(toList());
    result.addAll(val);
    return result;
  }

  private static List<Map.Entry<String, String>> testOptional(
    Optional<Integer> employerId,
    Integer countryId,
    List<Integer> priceRegionIds,
    Optional<List<Integer>> professionalAreaIds) {
    List<Map.Entry<String, String>> result = new ArrayList<>();
    result.add(new AbstractMap.SimpleEntry<>("countryId", countryId.toString()));
    employerId.ifPresent(id -> result.add(new AbstractMap.SimpleEntry<>("employerId", id.toString())));
    priceRegionIds.forEach(id -> result.add(new AbstractMap.SimpleEntry<>("priceRegionIds", id.toString())));
    professionalAreaIds.ifPresent(ids -> ids.forEach(id -> result.add(new AbstractMap.SimpleEntry<>("profAreaIds", id.toString()))));
    return result;
  }

  public static void main(String[] args) {
    int iterations = 10_000_000;
    int listSize = 10;
    Random rnd = new Random();
    List<Map.Entry<String, String>> entries = null;
    long start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      entries = testStream(
        rnd.nextInt(),
        rnd.nextInt(),
        rnd.ints(listSize).boxed().collect(toList()),
        rnd.ints(listSize).boxed().collect(toList())
      );
    }
    System.out.println("Filled Stream: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Entries size: " + entries.size());



    entries = null;
    start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      entries = testOptional(
        Optional.of(rnd.nextInt()),
        rnd.nextInt(),
        rnd.ints(listSize).boxed().collect(toList()),
        Optional.of(rnd.ints(listSize).boxed().collect(toList()))
      );
    }
    System.out.println("Filled Optional: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Entries size: " + entries.size());



    entries = null;
    start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      entries = testStream(
        rnd.nextInt(),
        null,
        Collections.emptyList(),
        Collections.emptyList()
      );
    }
    System.out.println("Empty Stream: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Entries size: " + entries.size());



    entries = null;
    start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      entries = testOptional(
        Optional.empty(),
        rnd.nextInt(),
        Collections.emptyList(),
        Optional.empty()
      );
    }
    System.out.println("Empty Optional: " + (System.currentTimeMillis() - start) + "ms");
    System.out.println("Entries size: " + entries.size());
  }
}
