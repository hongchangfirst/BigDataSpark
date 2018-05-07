package com.zhc.example.machinelearning.basic;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.mllib.linalg.VectorUDT;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.stat.correlation.Correlation;
import org.apache.spark.mllib.stat.correlation.Correlations;
import org.apache.spark.mllib.stat.correlation.PearsonCorrelation;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class CorrelationExample {
    public static void run(SparkSession sparkSession) {
        /*
        data: 0    1    2    3
              1.0  0.0  0.0  -2.0
              4.0  5.0  0.0  3.0
              6.0  7.0  0.0  8.0
              9.0  0.0  1.0  0.0
         */
        List<Row> data= Arrays.asList(
                // the first param is the length of the vector, the second param is the index array
                // the third param is the value array.
                RowFactory.create(Vectors.sparse(4, new int[]{0,3}, new double[]{1.0, -2.0})),
                RowFactory.create(Vectors.dense(4.0, 5.0, 0.0, 3.0)),
                RowFactory.create(Vectors.dense(6.0, 7.0, 0.0, 8.0)),
                RowFactory.create(Vectors.sparse(4, new int[]{0,2}, new double[]{9.0, 1.0}))
        );

        StructType schema = new StructType(
                new StructField[]{
                        new StructField("features", new VectorUDT(), false, Metadata.empty())
                }
        );

        /*
        Dataset<Row> df = sparkSession.createDataFrame(data, schema);
        Row r1 = Correlations.corr(df, "features").head();
        Row r1 = PearsonCorrelation.computeCorrelation();
*/
    }
}