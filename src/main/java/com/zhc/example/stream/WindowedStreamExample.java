package com.zhc.example.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

public class WindowedStreamExample {
    /**
     * You can use another terminal to send data from the server.
     * nc -lk 9998
     * You input some lines in the nc terminal, and every five seconds,
     * the Spark will process and output the data.
     */
    public static void run() {
        // local means run locally with one thread local means with 1 thread.
        // local[*] means run with many worker threads as logical cores on your machine.
        SparkConf conf = new SparkConf().setMaster("local").setAppName("StatefulStreamWordCount");
        // batch interval is 5 seconds.
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        jssc.checkpoint("file:///Users/hczhang/Downloads/spark-2.3.0-bin-hadoop2.7/bin/stateful_checkpoint");
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9998);

        // Stateless transformations, though these functions look like they are applying to the
        // whole stream, internally each DStream is composed of multiple RDDs (batches), and each
        // stateless transformation applies separately to each RDD.
        JavaDStream<String> words = lines.flatMap(
                s -> Arrays.asList(s.split(" ")).iterator()
        );

        JavaPairDStream<String, Integer> pairs = words.mapToPair(
                s -> new Tuple2<>(s, 1)
        );

        // Window duration is 30, controls how many previous batches of data are considered.
        // The second parameter is sliding duration, defaults to batch interval, controls how
        // frequently the new DStream computes results.
        JavaPairDStream<String, Integer> windowedWordCounts = pairs.reduceByKeyAndWindow(
                (i1, i2) -> i1 + i2,
                Durations.seconds(30),
                Durations.seconds(10)
        );

        /*
        Function3<String, Optional<Integer>, State<Integer>, Tuple2<String, Integer>> mappingFunc =
                (word, one, state) -> {
                    int sum = one.orElse(0) + (state.exists()? state.get() : 0);
                    Tuple2<String, Integer> output = new Tuple2<>(word, sum);
                    state.update(sum);
                    return output;
                };

        JavaPairDStream<String, Integer> statefulDStream = pairs.mapWithState(StateSpec.function(mappingFunc).initia;


                (List<Integer> nums, Optional<Integer> current) -> {
                }
         */
        // For example, reduceByKey() reduces data within each time step instead of across time steps.

        windowedWordCounts.print();
        // This will save data every duration even if there's no data received.
        windowedWordCounts.dstream().saveAsTextFiles("windowed_stream_word_count", "data");

        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception while awaitTermination") ;
            e.printStackTrace();
        }
    }
}