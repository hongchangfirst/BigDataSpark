package com.zhc.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.zhc.example.accumulator.AccumulatorExample;
import com.zhc.example.broadcast.BroadcastExample;
import com.zhc.example.loading.LoadingJsonFile;
import com.zhc.example.pagerank.PageRank;
import com.zhc.example.sql.SQLExample;
import com.zhc.example.stream.StreamExample;
import com.zhc.example.wordcount.WordCount;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.hive.HiveContext;

import scala.Tuple2;

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
        SparkSession sparkSession = SparkSession.builder()
                .config(conf)
                .getOrCreate();

        JavaSparkContext sc = JavaSparkContext.fromSparkContext(sparkSession.sparkContext());

        //JavaSparkContext sc = new JavaSparkContext(conf);

        //1. WordCount.run(sc);
        //2. PageRank.run(sc);
        //3. LoadingJsonFile.run(sc);
        //4. AccumulatorExample.run(sc);
        //5. BroadcastExample.run(sc);
        //6. SQLExample.run(sc);
        StreamExample.run();
    }


}