package com.github.rccookie.aoc.solutions;

import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import com.github.rccookie.aoc.Solution;
import com.github.rccookie.graph.Graphs;
import com.github.rccookie.graph.HashTree;
import com.github.rccookie.graph.Tree;

public class Solution7 extends Solution {

    Stack<String> path = new Stack<>();
    Tree<String, Long> fileTree = new HashTree<>("/");

    @Override
    public Object task1() {
        return dirs().mapToLong(this::sizeof)
                .filter(s -> s<=100000)
                .sum();
    }

    @Override
    public Object task2() {
        long req = 30000000 - (70000000 - sizeof("/"));
        return dirs().mapToLong(this::sizeof)
                .filter(s -> s >= req)
                .min();
    }

    @Override
    public void load() {
        for(int i=1; i<linesArr.length; i++) {
            assert linesArr[i].startsWith("$ ");
            String cmd = linesArr[i].substring(2);

            if(cmd.startsWith("cd")) {
                if(cmd.equals("cd .."))
                    path.pop();
                else {
                    String cd = cd();
                    String name = cmd.substring(3), fullName = sub(cd, name);
                    if(!fileTree.contains(fullName))
                        fileTree.add(fullName, cd, 0L);
                    path.push(name);
                }
            }
            else {
                assert cmd.equals("ls");
                String cd = cd();
                while(i < linesArr.length-1 && !linesArr[i+1].startsWith("$")) {
                    if(!linesArr[++i].startsWith("dir")) {
                        String[] parts = linesArr[i].split(" ");
                        long size = Long.parseLong(parts[0]);
                        fileTree.add(sub(cd, parts[1]), cd, size);
                        for(int j=1; j<=path.size(); j++)
                            fileTree.setEdgeTo(cd(j), sizeof(cd(j)) + size);
                    }
                }
            }
        }
        printTree();
    }

    private String cd() {
        return '/' + String.join("/", path);
    }

    private String cd(int index) {
        if(index < 1) return "/";
        StringBuilder cd = new StringBuilder();
        for(int i=0; i<index; i++)
            cd.append('/').append(path.get(i));
        return cd.toString();
    }

    private String sub(String dir, String file) {
        return dir.length() == 1 ? '/'+file : dir+'/'+file;
    }

    private long sizeof(String f) {
        if(f.equals("/"))
            return fileTree.adj("/").stream().map(Map.Entry::getKey).mapToLong(this::sizeof).sum();
        return fileTree.edgeTo(f);
    }

    private Stream<String> dirs() {
        return fileTree.stream().filter(f -> fileTree.adj(f).size() != 0 || sizeof(f) == 0);
    }

    private void printTree() {
        for(String f : Graphs.traverseDepthFirst(fileTree))
            System.out.println((f.length()==1?"/":"  ".repeat(fileTree.depth(f)) + f.substring(f.lastIndexOf('/') + 1)) + " - " + sizeof(f));
    }

    public static void main(String[] args) {
        run();
    }
}
