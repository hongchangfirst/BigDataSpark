package com.zhc.demo;

import com.zhc.example.machinelearning.SpamClassifier;
import com.zhc.example.sql.SQLExample;
import com.zhc.example.stream.WindowedStreamExample;
import com.zhc.example.wordcount.WordCount;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class BigAnalysis {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                //.setMaster("local")
                .setAppName("ZhcApp")
                //.set("spark.driver.bindAddress", "192.168.1.150")
                //.set("spark.master", "spark://172.24.43.31:7077")
                //.set("spark.ui.port", "8080")
                // enable override output files
                .set("spark.hadoop.validateOutputSpecs", "false");

        // SparkSession is the new entry point of Dataset and DataFrame, it composes of
        // SparkContext, HiveContext, and SparkStreamingContext in future.

        //*
        SparkSession sparkSession = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
        //*/

        WordCount.run(sc);
        //2. PageRank.run(sc);
        //3. LoadingJsonFile.run(sc);
        //4. AccumulatorExample.run(sc);
        //5. BroadcastExample.run(sc);
        //SQLExample.run(sparkSession);
        //7. StreamExample.run();
        //8. ParallelizeExample.run(sc);
        //9. WindowedStreamExample.run();
        //10. SpamClassifier.run(sc);
    }


}