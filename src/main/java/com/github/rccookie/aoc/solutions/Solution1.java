package com.github.rccookie.aoc.solutions;

import java.util.Arrays;
import java.util.stream.LongStream;

import com.github.rccookie.aoc.Solution;

public class Solution1 extends Solution {

    @Override
    public Object task1() {
        return blockSums().max();
    }

    @Override
    public Object task2() {
        return blockSums()
                .map(l-> -l)
                .sorted()
                .map(l-> -l)
                .limit(3)
                .sum();
    }

    private LongStream blockSums() {
        return Arrays.stream(input.split("\n\r?\n\r?"))
                .mapToLong(block -> block.lines().mapToLong(Long::parseLong).sum());
    }



    public static void main(String[] args) {
        run();
    }
}
