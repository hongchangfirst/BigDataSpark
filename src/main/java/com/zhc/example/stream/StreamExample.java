package com.zhc.example.stream;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

public class StreamExample {
    /**
     * You can use another terminal to send data from the server.
     * nc -lk 9998
     * You input some lines in the nc terminal, and every five seconds,
     * the Spark will process and output the data.
     */
    public static void run() {
        // local means run locally with one thread local[3] means with 3 threads.
        // local[*] means run with many worker threads as logical cores on your machine.
        SparkConf conf = new SparkConf().setMaster("local[3]").setAppName("StreamWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9998);

        JavaDStream<String> words = lines.flatMap(
                s -> Arrays.asList(s.split(" ")).iterator()
        );

        JavaPairDStream<String, Integer> pairs = words.mapToPair(
                s -> new Tuple2<>(s, 1)
        );

        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
                (i1, i2) -> i1 + i2
        );

        wordCounts.print();
        // This will save data every duration even if there's no data received.
        wordCounts.dstream().saveAsTextFiles("stream_word_count", "data");

        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception while awaitTermination") ;
            e.printStackTrace();
        }
    }
}