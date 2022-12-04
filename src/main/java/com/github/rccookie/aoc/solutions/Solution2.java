package com.github.rccookie.aoc.solutions;

import com.github.rccookie.aoc.Solution;

public class Solution2 extends Solution {

    @Override
    public Object task1() {
        return sum(l -> {
            int a = l.charAt(0) - 'A', b = l.charAt(2) - 'X';
            int score;
            if(a == b) score = 3;
            else if((a+1)%3 == b) score = 6;
            else score = 0;
            return score + b+1;
        });
    }

    @Override
    public Object task2() {
        return sum(l -> {
            int a = l.charAt(0) - 'A', d = l.charAt(2) - 'X' - 1;
            int b = (a+3+d) % 3;
            return (d+1)*3 + b+1;
        });
    }

    public static void main(String[] args) {
        run();
    }
}
