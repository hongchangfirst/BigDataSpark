package com.zhc.example.sql;

import com.zhc.example.loading.LoadingJsonFile;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jackson.map.ObjectMapper;

public class DatasetExample {
    /**
     * JavaRDD -> DataFrame
     * JavaRDD -> Dataset
     * @param sparkSession
     */
    public static void run(SparkSession sparkSession) {
        String inputFile = "input.json";
        JavaRDD<String> rdd = sparkSession.read().textFile(inputFile).javaRDD();

        Dataset<Row> dataset = sparkSession.createDataFrame(rdd, Person.class);
        JavaRDD<Person> people = rdd.map(
                line -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Person p = mapper.readValue(line, Person.class);
                        return p;
                    } catch (Exception e) { }
                    return new Person();
                }
        );

        Encoder<Person> personEncoder = Encoders.bean(Person.class);
        Dataset<Person> personDataset = sparkSession.createDataset(people.rdd(), personEncoder);
    }
}