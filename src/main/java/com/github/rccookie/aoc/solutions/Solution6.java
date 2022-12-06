package com.github.rccookie.aoc.solutions;

import com.github.rccookie.aoc.Solution;

public class Solution6 extends Solution {

    @Override
    public Object task1() {
        return distinctIndex(4);
    }

    @Override
    public Object task2() {
        return distinctIndex(14);
    }

    private long distinctIndex(int distinctCount) {
        for(int i=0; i<charList.size() - distinctCount+1; i++)
            if(charList.subList(i, i+distinctCount).stream().distinct().count() == distinctCount)
                return i+distinctCount;
        return -1;
    }

    public static void main(String[] args) {
        run();
    }
}
