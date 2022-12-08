package com.github.rccookie.aoc.solutions;

import java.util.stream.IntStream;

import com.github.rccookie.aoc.Solution;
import com.github.rccookie.primitive.int2;

public class Solution8 extends Solution {

    int2 size;
    int[][] heights;

    @Override
    public Object task1() {
        int count = 0;
        for(int2 p : size) {
            int h = heights[p.x][p.y];
            if(IntStream.range(0,p.x)        .allMatch(x->heights[x][p.y]<h) ||
               IntStream.range(p.x+1, size.x).allMatch(x->heights[x][p.y]<h) ||
               IntStream.range(0,p.y)        .allMatch(y->heights[p.x][y]<h) ||
               IntStream.range(p.y+1, size.y).allMatch(y->heights[p.x][y]<h)) count++;
        }
        return count;
    }

    @Override
    public Object task2() {
        return size.iterator().stream().mapToLong(p -> {
            int h = heights[p.x][p.y];
            long left  = Math.min(p.x,          IntStream.iterate(p.x-1, x->x>0, x->x-1).takeWhile(x->heights[x][p.y]<h).count() + 1);
            long right = Math.min(size.x-p.x-1, IntStream.range(p.x+1, size.x)               .takeWhile(x->heights[x][p.y]<h).count() + 1);
            long up    = Math.min(p.y,          IntStream.iterate(p.y-1, y->y>0, y->y-1).takeWhile(y->heights[p.x][y]<h).count() + 1);
            long down  = Math.min(size.y-p.y-1, IntStream.range(p.y+1, size.y)               .takeWhile(y->heights[p.x][y]<h).count() + 1);
            return left * right * up * down;
        }).max();
    }
    @Override
    public void load() {
        size = new int2(linesArr[0].length(), linesArr.length);
        heights = new int[size.x][size.y];
        for(int2 p : size)
            heights[p.x][p.y] = linesArr[p.y].charAt(p.x) - 48;
    }

    public static void main(String[] args) {
        run();
    }
}
