package dev.partin.james.jellyfinlibrarymanager.processors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

//Write me a pipeline for example purposes

public class pipeline_test {
    List<Integer> listOfNumbers = Arrays.asList(1, 2, 3, 4);
    ForkJoinPool customThreadPool = new ForkJoinPool(4);
    int sum = customThreadPool.submit(() -> listOfNumbers.parallelStream().reduce(0, Integer::sum)).get();

    public pipeline_test() throws ExecutionException, InterruptedException {
    }
}