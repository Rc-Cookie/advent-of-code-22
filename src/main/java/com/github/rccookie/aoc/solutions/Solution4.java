package com.github.rccookie.aoc.solutions;

import java.util.Arrays;
import java.util.stream.Stream;

import com.github.rccookie.aoc.Solution;

public class Solution4 extends Solution {

    @Override
    public Object task1() {
        // [ --- ]
        //       [ - ]
        return ranges().filter(r -> (r[0] <= r[2] && r[1] >= r[3]) ||
                                    (r[0] >= r[2] && r[1] <= r[3])).count();
    }

    @Override
    public Object task2() {
        return ranges().filter(r -> Math.max(r[0], r[2]) <= Math.min(r[1], r[3])).count();
    }

    private Stream<int[]> ranges() {
        return lines.map(l -> {
            String[] strRanges = l.split("[,-]");
            int[] ranges = new int[strRanges.length];
            Arrays.setAll(ranges, i -> Integer.parseInt(strRanges[i]));
            return ranges;
        });
    }

    public static void main(String[] args) {
        run();
    }
}
