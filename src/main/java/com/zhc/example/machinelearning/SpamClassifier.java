package com.zhc.example.machinelearning;

import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

public class SpamClassifier {
    public static void run(JavaSparkContext sc) {
        JavaRDD<String> spam = sc.textFile("spam.txt");
        JavaRDD<String> normal = sc.textFile("normal.txt");

        // hashing term frequency of 10,000 features
        // one million features
        // if we set smaller size such as 2 features, then the prediction is wrong.
        // The feature size should be according to the training data size.
        final HashingTF tf = new HashingTF(10000);

        JavaRDD<LabeledPoint> posExamples = spam.map(
                s -> new LabeledPoint(1, tf.transform(Arrays.asList(s.split(" "))))
        );
        JavaRDD<LabeledPoint> negExamples = normal.map(
                s -> new LabeledPoint(0, tf.transform(Arrays.asList(s.split(" "))))
        );

        JavaRDD<LabeledPoint> trainData = posExamples.union(negExamples);
        trainData.cache();

        // Run Logistic Regression using the SGD algorithm
        LogisticRegressionModel model = new LogisticRegressionWithSGD().run(trainData.rdd());

        // Test on a positive example (spam) and a negative one (normal).
        Vector posTest = tf.transform(
                Arrays.asList("O M G GET cheap stuff by sending money to ...".split(" "))
        );

        Vector negTest = tf.transform(
                Arrays.asList("Hi Dad, I started studying spark the other ...".split(" "))
        );

        System.out.println("----------- positive example " + model.predict(posTest));
        System.out.println("----------- negative example " + model.predict(negTest));

    }
}