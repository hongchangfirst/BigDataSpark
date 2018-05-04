package com.zhc.demo;

import com.zhc.example.stream.WindowedStreamExample;
import org.apache.spark.SparkConf;

public class BigAnalysis {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("ZhcApp")
                //.set("spark.master", "spark://172.24.43.31:7077")
                //.set("spark.ui.port", "8080")
                // enable override output files
                .set("spark.hadoop.validateOutputSpecs", "false");

        // SparkSession is the new entry point of Dataset and DataFrame, it composes of
        // SparkContext, HiveContext, and SparkStreamingContext in future.

        /*
        SparkSession sparkSession = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
        */

        //1. WordCount.run(sc);
        //2. PageRank.run(sc);
        //3. LoadingJsonFile.run(sc);
        //4. AccumulatorExample.run(sc);
        //5. BroadcastExample.run(sc);
        //6. SQLExample.run(sc);
        //7. StreamExample.run();
        //8.ParallelizeExample.run(sc);
        WindowedStreamExample.run();

    }


}