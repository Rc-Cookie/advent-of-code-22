package com.github.rccookie.aoc.solutions;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.github.rccookie.aoc.Solution;

public class Solution5 extends Solution {

    @Override
    public Object task1() {
        return calculate((stacks, instr) -> {
            for(int j=0; j<instr.count; j++)
                stacks[instr.to].push(stacks[instr.from].pop());
        });
    }

    @Override
    public Object task2() {
        return calculate((stacks, instr) -> {
            Stack<Character> buffer = new Stack<>();
            for(int j=0; j<instr.count; j++)
                buffer.push(stacks[instr.from].pop());
            for(int j=0; j<instr.count; j++)
                stacks[instr.to].push(buffer.pop());
        });
    }

    @SuppressWarnings("unchecked")
    private String calculate(BiConsumer<Deque<Character>[], Instr> executer) {
        Deque<Character>[] stacks = new Deque[(linesArr[0].length() + 1) / 4];
        Arrays.setAll(stacks, i -> new ArrayDeque<>());
        int i=0;
        for(; !linesArr[i].startsWith(" 1 "); i++) {
            for(int j=0; j<stacks.length; j++) {
                char c = linesArr[i].charAt(4*j+1);
                if(c != ' ') stacks[j].add(c);
            }
        }
        i+=2;
        for(; i<linesArr.length; i++) {
            String[] words = linesArr[i].split(" ");
            executer.accept(stacks, new Instr(
                    Integer.parseInt(words[3]) - 1,
                    Integer.parseInt(words[5]) - 1,
                    Integer.parseInt(words[1])
            ));
        }
        return Arrays.stream(stacks).map(Deque::peek).map(c -> ""+c).collect(Collectors.joining());
    }

    record Instr(int from, int to, int count) { }

    public static void main(String[] args) {
        run();
    }
}
