package com.zhc.example.accumulator;

import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.Accumulator;
import org.apache.spark.storage.StorageLevel;

public class AccumulatorExample {
    /**
     * In Spark, we can use variables defined outside map() function or filter() function in the driver program.
     * But each task running on the cluster gets a new copy of each variable, and updates from these copies are not
     * propagated back to the driver. Accumulator is spark's shared variables, providing a simple syntax for aggregating
     * values from worker nodes back to the driver program.
     *
     * One example to use accumulators is to count events that occur during job execution for debugging purposes. Such as
     * we want to know how many lines of the input file are blank.
     * @param sc
     */
    public static void run(JavaSparkContext sc) {
        String inputFile = "input_data_with_blank_lines";
        JavaRDD<String> rdd = sc.textFile(inputFile);

        final Accumulator<Integer> blankLines = sc.accumulator(0);
        JavaRDD<String> fields = rdd.flatMap(
                s -> {
                    if (s.equals("")) {
                        blankLines.add(1);
                    }
                    return Arrays.asList(s.split(" ")).iterator();
                }
        );

        String outputFile = "output_data_accumulator";
        // We can only get the right number of accumulator since the lazy mechanism of map transformation.
        // The incrementing of accumulator will happen only when the the saveAsTextFile action occurs.
        fields.saveAsTextFile(outputFile);
        // output 3
        System.out.println("Accumulator blank lines: " + blankLines.value());

        // We use another action to force compute.
        fields.foreach(
                s -> System.out.println(s)
        );
        // We print the accumulator again to see what happens
        // output 6
        System.out.println("Accumulator blank lines after two actions: " + blankLines.value());

        fields.count();
        // output 9
        System.out.println("Accumulator blank lines after three actions: " + blankLines.value());

        // How to solve such problem? One way is to use only one action in order to make sure the correctness.
        // The other way is to use cache or persist to not let spark calculate the transformations again.
        // cache use persist with default storage level.
        // cache itself doesn't perform any action, It only marks the RDD to be cached. The next action afterwards
        // it is going to be cached.
        fields.cache();
        //fields.persist(StorageLevel.MEMORY_AND_DISK());

        // another action, spark calculates the graph and then cached the result.
        fields.count();
        // output 12, calculate again
        System.out.println("Accumulator blank lines after cache: " + blankLines.value());

        // another action, spark doesn't need to calculate the graph again since it can use the cached result.
        fields.count();
        // output 12, not calculate again
        System.out.println("Accumulator blank lines again after cache: " + blankLines.value());
    }
}