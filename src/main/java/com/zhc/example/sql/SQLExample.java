package com.zhc.example.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SQLExample {
    /**
     * Dataset
     * @param sparkSession
     */
    public static void run(SparkSession sparkSession) {
        String inputFile = "input.json";
        Dataset<Row> ds = sparkSession.read().json(inputFile);
        //ds.show();

        // DataFrame operations
        //ds.select("name").show();
        //ds.groupBy("age").count().show();

        // SQL Queries Programmatically.

        ds.createOrReplaceTempView("people");
        Dataset<Row> sqlDs = sparkSession.sql("select * from people");
        //sqlDs.show();

        ds.createOrReplaceGlobalTempView("people");
        //sparkSession.sql("select * from global_temp.people").show();


        Encoder<Person> personEncoder = Encoders.bean(Person.class);
        Dataset<Person> peopleDS = sparkSession.read().json(inputFile).as(personEncoder);
        peopleDS.show();
        //parquet format
        //peopleDS.write().save("sql_output");
        //
        // saveAsTable is in the spark-warehouse folder in local config(no hive), such as /spark-warehouse/sql_table_output
        peopleDS.write().saveAsTable("sql_table_output");

    }
}