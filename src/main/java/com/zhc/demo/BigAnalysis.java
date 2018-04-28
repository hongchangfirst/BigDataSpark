package com.zhc.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.zhc.example.loading.LoadingJsonFile;
import com.zhc.example.pagerank.PageRank;
import com.zhc.example.wordcount.WordCount;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class BigAnalysis {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("ZhcApp");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //WordCount.run(sc);
        //PageRank.run(sc);
        LoadingJsonFile.run(sc);
    }


}