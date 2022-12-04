package com.github.rccookie.aoc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.ToLongFunction;

import com.github.rccookie.http.HttpRequest;
import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ListStream;
import com.github.rccookie.util.Wrapper;
import com.github.rccookie.xml.Document;
import com.github.rccookie.xml.Node;
import com.github.rccookie.xml.XML;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public abstract class Solution {

    protected String input = null;
    protected ListStream<String> lines = null;
    protected String[] linesArr = null;


    public abstract Object task1();

    public Object task2() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        if(!caller.getClassName().equals(Solution.class.getName()))
            return "";
        throw new NotImplemented();
    }

    public int getDay() {
        String name = getClass().getSimpleName();
        int i = name.length() - 2;
        while(name.charAt(i) >= '0' && name.charAt(i) <= '9') i--;
        return Integer.parseInt(name.substring(i+1));
    }

    public int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    protected long sum(ToLongFunction<String> mapper) {
        return lines.mapToLong(mapper).sum();
    }

    protected ListStream<String> split(String regex) {
        return ListStream.of(input.split(regex)).useAsList();
    }

    protected void printInputStats() {
        Console.map("Input statistics",
                "Lines:", linesArr.length,
                "| Chars:", lines.mapToLong(String::length).sum(),
                "| Blank lines:", lines.filter(String::isBlank).count());
    }



    public static void run(Class<? extends Solution> type, int execTask) {
        try {
            Constructor<? extends Solution> ctor = type.getDeclaredConstructor();
            Solution s = ctor.newInstance();
            int day = s.getDay(), year = s.getYear();

            String token;
            try {
                token = Json.parse(new FileReader("config.json")).get("token").asString();
            } catch(Exception e) {
                Console.error("Failed to read login token.");
                Console.error("Ensure you have a file 'config.json' with a key 'token'. " +
                        "The value should be the value of the cookie 'session' which you can " +
                        "read in your browser's dev window when logged in.");
                return;
            }


            Wrapper<Integer> possibleAnswer = new Wrapper<>(null);
            new Thread(() -> {
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL("https://adventofcode.com/"+year+"/day/"+day).openConnection();
                    con.setRequestProperty("Cookie", "session=" + token);
                    Document d = XML.parse(con.getInputStream(), XML.HTML);

                    Node success = d.getElements().filter(n -> {
                        String cs = n.attribute("class");
                        return cs != null && cs.contains("day-success");
                    }).findFirst().orElse(null);
                    int possible;
                    if(success == null) possible = 0;
                    else if(!success.text().contains("Both")) possible = 1;
                    else possible = -1;
                    synchronized(possibleAnswer) {
                        possibleAnswer.value = possible;
                        possibleAnswer.notify();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();


            Path inputFile = Path.of("input/" + day);

            if(!Files.exists(inputFile.getParent())) {
                Files.createDirectories(inputFile.getParent());
                new JsonObject("token", token).store(new File("input/info.json"));
            }
            else if(!Files.exists(inputFile.getParent().resolve("info.json")) ||
                    !Json.parse(new FileReader("input/info.json")).get("token").asString().equals(token)) {
                for(int i=1; i<=25; i++)
                    Files.deleteIfExists(Path.of("input/"+i));
                new JsonObject("token", token).store(new File("input/info.json"));
            }

            if(Files.exists(inputFile))
                s.input = Files.readString(inputFile);
            else {
                Console.log("Fetching input...");
                s.input = new HttpRequest("https://adventofcode.com/"+year+"/day/"+day+"/input")
                        .setCookies("session=" + token)
                        .send().data.waitFor();
                if(s.input.startsWith("Puzzle inputs differ by user.")) {
                    Console.error("Invalid login token; cannot fetch input");
                    System.exit(1);
                }
                Files.writeString(inputFile, s.input);
            }
            s.lines = ListStream.of(s.input.lines()).useAsList();
            s.linesArr = s.input.split("\r?\n");

            s.printInputStats();


            Object resultObj;
            int task;
            if(execTask < 1 || execTask > 2) {
                try {
                    resultObj = s.task2();
                    task = 1;
                } catch (NotImplemented n) {
                    resultObj = s.task1();
                    task = 0;
                }
            }
            else if(execTask == 1) {
                resultObj = s.task1();
                task = 0;
            }
            else {
                try {
                    resultObj = s.task2();
                    task = 1;
                } catch(NotImplemented n) {
                    Console.error("Task 2 is is not yet implemented. Override the 'Object task2()' method in " + type.getName());
                    System.exit(1);
                    return;
                }
            }
            optionalLoop: while(resultObj != null) {
                switch(resultObj) {
                    case Optional<?> o -> resultObj = o.orElse(null);
                    case OptionalInt o -> resultObj = o.isPresent() ? o.getAsInt() : null;
                    case OptionalLong o -> resultObj = o.isPresent() ? o.getAsLong() : null;
                    case OptionalDouble o -> resultObj = o.isPresent() ? o.getAsDouble() : null;
                    default -> { break optionalLoop; }
                }
            }

            String result = Objects.toString(resultObj);
            Console.map("Result", result);
            if(result.isBlank()) System.exit(0);


            synchronized(possibleAnswer) {
                if(possibleAnswer.value == null)
                    possibleAnswer.wait();
            }
            if(possibleAnswer.value != task) {
                if(possibleAnswer.value == 0) {
                    Console.log("Cannot submit second solution before first.");
                    if(Console.inputYesNo("Execute task 1 instead?"))
                        run(type, 1);
                }
                return;
            }

            if(!Console.input("Submit? >").isBlank()) return;

            Console.log("Submitting answer...");

            HtmlUnitDriver driver = new HtmlUnitDriver();
            driver.get("https://adventofcode.com/"+year+"/day/"+day);
            driver.manage().addCookie(new Cookie("session", token));
            driver.get("https://adventofcode.com/"+year+"/day/"+day);
            WebElement answer = driver.findElement(By.name("answer"));
            answer.sendKeys(result);
            answer.submit();

            String info = driver.findElement(By.tagName("main")).findElement(By.tagName("p")).getText();
            if(info.contains("the right answer!"))
                info = info.substring(0, info.indexOf('!') + 1);
            else if(info.contains("You gave an answer too recently"))
                info = info.substring(0, info.indexOf("wait.") + 5);
            else info = info.substring(0, info.indexOf('.') + 1);
            Console.log(info);

            driver.close();

        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void run(Class<? extends Solution> type) {
        run(type, 0);
    }

    @SuppressWarnings("unchecked")
    public static void run(int task) {
        try {
            run((Class<? extends Solution>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()), task);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void run() {
        try {
            run((Class<? extends Solution>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()), 0);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }

    private static class NotImplemented extends Error { }
}
