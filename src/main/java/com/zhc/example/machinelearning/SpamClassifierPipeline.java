package com.zhc.example.machinelearning;

import java.util.Arrays;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SpamClassifierPipeline {
    /**
     * A pipeline is a series of algorithms that transform a dataset. The Pipeline can automatically
     * search for the best set of parameters using a grid search, evaluating each set using an
     * evaluation metric of choice.
     * 1 DataFrame
     * 2 Transformer, an algorithm which can transform one DataFrame to another one. Transformer.transform().
     * 3 Estimator, an algorithm which can be fit on a DataFrame to produce a Transformer. Estimator.fit()
     * 4 Pipeline
     * 5 Parameter
     * @param sc
     */
    public static void run(SparkSession sparkSession) {
        Dataset<Row> training = sparkSession.createDataFrame(
                Arrays.asList(
                        new JavaLabeledDocument(0L, "a b c d e spark", 1.0),
                        new JavaLabeledDocument(1L, "b d", 0.0),
                        new JavaLabeledDocument(2L, "spark f g h", 1.0),
                        new JavaLabeledDocument(3L, "hadoop mapreduce", 0.0)

                ), JavaLabeledDocument.class
        );

        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");

        final HashingTF tf = new HashingTF()
                .setNumFeatures(1000)
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol("features");

        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.001);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                        tokenizer,
                        tf,
                        lr
                });
        // why it only accepts dataframe instead of dataset<row>
        //PipelineModel model = pipeline.fit(training);


    }
}