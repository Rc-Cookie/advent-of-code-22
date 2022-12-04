package com.github.rccookie.aoc.solutions;

import com.github.rccookie.aoc.Solution;

public class Solution3 extends Solution {

    @Override
    public Object task1() {
        return sum(l -> {
            String a = l.substring(0, l.length() / 2), b = l.substring(l.length() / 2);
            char d = 0;
            for(char c : a.toCharArray()) {
                if(b.contains(c+"")) {
                    d = c;
                    break;
                }
            }
            assert d != 0;
            return prio(d);
        });
    }

    @Override
    public Object task2() {
        int sum = 0;
        for(int i=0; i<linesArr.length; i+=3) {
            char b = 0;
            for(char c : linesArr[i].toCharArray()) {
                if(linesArr[i+1].contains(c+"") && linesArr[i+2].contains(c+"")) {
                    b = c;
                    break;
                }
            }
            assert b != 0;
            sum += prio(b);
        }
        return sum;
    }

    private int prio(char x) {
        if(x >= 'a') return x - 'a' + 1;
        return x - 'A' + 27;
    }

    public static void main(String[] args) {
        run();
    }
}
