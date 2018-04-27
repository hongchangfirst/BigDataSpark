package com.zhc.example.wordcount;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class WordCount {
    /**
     * 1 flatMap:     line -> words
     * 2 mapToPair:   word -> (word, 1)
     * 3 reduceByKey: (word, 1) -> (word, count)
     * @param sc JavaSparkContext
     */
    public static void run(JavaSparkContext sc) {
        String inputFile = "word_counts_input";
        JavaRDD<String> input = sc.textFile(inputFile);

        /*
        // Solution 1. This is to use the Function Object, traditional solution before Java8.
        JavaRDD<String> words = input.flatMap(
                new FlatMapFunction<String, String>() {
                    public Iterator<String> call(String s) throws Exception {
                        return Arrays.asList(s.split(" ")).iterator();
                    }
                }
        );
        */
        /*
        // Solution 2. Java8 statement lambda
        JavaRDD<String> words = input.flatMap(
                (String s) -> { return Arrays.asList(s.split(" ")).iterator(); }
        );
        */

        // Solution 3. Java8 expression lambda
        // How flatMap works: For every element(line) in JavaRDD, use the lambda function to process
        // and return an iterator of String. Spark gets all the iterator and aggregates all the string
        // together to generate a new JavaRDD of words.
        JavaRDD<String> words = input.flatMap(
                s -> Arrays.asList(s.split(" ")).iterator()
        );


        /*
        // How mapToPair works: For Every element in JavaRDD, use the function to process and return a
        // pair(Tuple2 in java). Spark gets all the pair and aggregates all the pair to generate a new
        // JavaPairRDD.
        JavaPairRDD<String, Integer> counts = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    public Tuple2<String, Integer> call(String s) throws Exception {
                        return new Tuple2(s, 1);
                    }
                }
        );

        // How reduceByKey works: For every element in JavaRDD, aggregates all the elements of the same
        // key, and call the function in binary way, so that it takes O(n) time in total.
        counts = counts.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    public Integer call(Integer x, Integer y) throws Exception {
                        return x + y;
                    }
                }
        );
        */

        // Simplified version using Lambda.
        JavaPairRDD<String, Integer> counts = words.mapToPair(
                s -> new Tuple2<>(s, 1)
        ).reduceByKey(
                (x, y) -> x + y
        );

        // See an example of 10 words.
        List<String> list = words.take(10);
        for (String s : list) {
            System.out.println(s);
        }

        String outputFile = "word_counts_output";
        counts.saveAsTextFile(outputFile);
    }
}