package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImperativeExample {
    public static void main(String args[]) {

        var nameList = List.of("alex", "ban", "adam", "mukesh");
        List<String> names = nameGreaterThanSizeStream(nameList, 3);
        System.out.println(names);

    }

    private static List<String> nameGreaterThanSize(List<String> names, int size) {
        var list = new ArrayList<String>();

        for (String name : names) {
            if (name.length() > size) {
                list.add(name);
            }
        }

        return list;
    }

    private static List<String> nameGreaterThanSizeStream(List<String> names, int size) {
        return names.stream().filter(e -> e.length() > 3).collect(Collectors.toList());
    }
}
