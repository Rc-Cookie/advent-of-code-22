package com.github.rccookie.aoc.solutions;

import java.util.Stack;
import java.util.stream.Stream;

import com.github.rccookie.aoc.Solution;
import com.github.rccookie.graph.Graph;
import com.github.rccookie.graph.HashGraph;

public class Solution7 extends Solution {

    Stack<String> path = new Stack<>();
    Graph<String, Long> fileTree = new HashGraph<>();

    @Override
    public Object task1() {
        loadFileTree();
        return dirs().mapToLong(this::sizeof)
                .filter(s -> s<=100000)
                .sum();
    }

    @Override
    public Object task2() {
        loadFileTree();
        long req = 30000000 - (70000000 - sizeof("/"));
        return dirs().mapToLong(this::sizeof)
                .filter(s -> s >= req)
                .min();
    }

    private void loadFileTree() {
        fileTree.clear();
        fileTree.connect("/", "/", 0L);

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
                        fileTree.connect(cd, fullName, 0L);
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
                        fileTree.connect(cd, sub(cd, parts[1]), size);
                        for(int j=0; j<=path.size(); j++)
                            fileTree.connect(cd(j-1), cd(j), sizeof(cd(j)) + size);
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

    private String parent(String path) {
        return path.lastIndexOf('/') < 1 ? "/" : path.substring(0, path.lastIndexOf('/'));
    }

    private long sizeof(String f) {
        return fileTree.edge(parent(f), f);
    }

    private Stream<String> dirs() {
        return fileTree.stream().filter(f -> fileTree.adj(f).size() != 0 || sizeof(f) == 0);
    }

    private void printTree() {
        Stack<String> stack = new Stack<>();
        stack.push("/");
        while(!stack.isEmpty()) {
            String f = stack.pop();
            System.out.println((f.length()==1?"/":"  ".repeat((int) f.chars().filter(c -> c == '/').count()) + f.substring(f.lastIndexOf('/') + 1)) + " - " + sizeof(f));
            for(String sf : fileTree.adj(f))
                if(!sf.equals("/"))
                    stack.push(sf);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
