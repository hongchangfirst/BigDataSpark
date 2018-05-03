package com.zhc.example.broadcast;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

public class BroadcastExample {
    /**
     * Though Spark automatically sends all variables referenced in your closures to the worker nodes. But if
     * the closures use the same variable in multiple parallel operations, spark sends it separately for each
     * operation.
     * So here comes the broadcast variables.
     * Broadcast variables allow the program to efficiently send a large, read-only value to all the worker
     * nodes for use in one or more spark operations. The value is sent to each node only once, using an
     * efficient, BitTorrent-like communication mechanism.
     * @param sc
     */
    public static void run(JavaSparkContext sc) {
        String inputFile = "file:///Users/hczhang/Downloads/spark-2.3.0-bin-hadoop2.7/bin/broadcast_input_data";
        JavaRDD<String> callingCodes = sc.textFile(inputFile);

        Broadcast<Map<String, String>> callSignTable = sc.broadcast(getCallSignTable());
        JavaRDD<String> callingCountries = callingCodes.map(
                s -> {
                    if (callSignTable.value().containsKey(s)) {
                        return callSignTable.value().get(s);
                    }
                    return "NotExistCountry";
                }
        );

        String outputFile = "broadcast_output_data";
        callingCountries.saveAsTextFile(outputFile);

    }

    private static  Map<String, String> getCallSignTable() {
        Map<String, String> callSign = new HashMap<>();
        callSign.put("+86", "CN");
        callSign.put("+1", "US");
        callSign.put("+6", "GB");
        callSign.put("+3", "IN");

        return callSign;
    }
}