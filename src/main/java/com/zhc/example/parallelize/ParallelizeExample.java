package com.zhc.example.parallelize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class ParallelizeExample {
    public static void run(JavaSparkContext sc) {
        Set<String> data = new HashSet<>();
        data.add("2");
        data.add("1");
        data.add("3");
        data.add("32");

        // default numSlices is 1
        JavaRDD<String> javaRDD = sc.parallelize(new ArrayList<>(data));

        javaRDD.filter(
                s -> s.length() < 2
        ).saveAsTextFile("parallelize_output");

    }
}