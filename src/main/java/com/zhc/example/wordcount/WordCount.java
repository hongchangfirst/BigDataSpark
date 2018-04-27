package com.zhc.example.wordcount;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class WordCount {
    public static void run(JavaSparkContext sc) {
        String inputFile = "word_counts_input";
        JavaRDD<String> input = sc.textFile(inputFile);

        /*
        JavaRDD<String> words = input.flatMap(
                new FlatMapFunction<String, String>() {
                    public Iterator<String> call(String s) throws Exception {
                        return Arrays.asList(s.split(" ")).iterator();
                    }
                }
        );
        */
        /*
        //Java8 Lambda expression, statement lambda
        JavaRDD<String> words = input.flatMap(
                (String s) -> { return Arrays.asList(s.split(" ")).iterator(); }
        );
        */

        // expression lambda
        JavaRDD<String> words = input.flatMap(
                s -> Arrays.asList(s.split(" ")).iterator()
        );

        JavaPairRDD<String, Integer> counts = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    public Tuple2<String, Integer> call(String s) throws Exception {
                        return new Tuple2(s, 1);
                    }
                }
        ).reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    public Integer call(Integer x, Integer y) throws Exception {
                        return x + y;
                    }
                }
        );

        List<String> list = words.take(10);
        for (String s : list) {
            System.out.println(s);
        }
        String outputFile = "word_counts_output";
        counts.saveAsTextFile(outputFile);
    }
}