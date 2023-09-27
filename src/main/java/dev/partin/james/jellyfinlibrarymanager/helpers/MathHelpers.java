package dev.partin.james.jellyfinlibrarymanager.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MathHelpers {
    private int thing = 0;
    public static long mode(Iterable<Long> numbers) {
        long mode = 0;
        long maxCount = 0;
        for (long number : numbers) {
            long count = 0;
            for (long number2 : numbers) {
                if (number2 == number) {
                    count++;
                }
            }
            if (count > maxCount) {
                mode = number;
                maxCount = count;
            }
        }
        return mode;
    }

    public static long mode(Long[] numbers) {
        return mode(Arrays.asList(numbers));
    }

    public static int mode(int[] numbers) {
        return (int) mode(intArrayToList(numbers));
    }

    public static List<Long> intArrayToList(int[] array) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < array.length; i++) {
            list.add((long) array[i]);
        }
        return list;
    }

}
