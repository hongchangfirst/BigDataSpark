package com.zhc.example.pagerank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class PageRank {
    /**
     * 1 mapToPair to get links: line -> (SourcePageId, [TargetPageId1, TargetPageId2, ...])
     * 2 mapValues to get ranks: (SourcePageId, [TargetPageId1, TargetPageId2, ...]) -> (SourcePageId, 1.0)
     * 3 links join ranks: -> (SourcePageId, ([TargetPageId1, TargetPageId2, ...], 1.0))
     * 4 flatMapToPair to get contributions: -> (TargetPageId, contribution)
     * 5 reduceByKey -> (TargetPageId, contributionSum)
     * 6 mapValues -> (TargetPageId, contributionSumTune)
     * 7 sortByKey -> (TargetPageId, contributionSumTune)
     * @param sc
     */
    public static void run(JavaSparkContext sc) {
        String pageRankInput = "page_rank_links.txt";
        JavaRDD<String> raw_links = sc.textFile(pageRankInput);

        /*
        JavaPairRDD<String, List<String>> links = raw_links.mapToPair(new PairFunction<String, String, List<String>>() {
            public Tuple2<String, List<String>> call(String s) throws Exception {
                String[] pageIds = s.split(" ");

                List<String> pageIdList = Arrays.asList(pageIds);
                pageIdList.remove(0);
                return new Tuple2<>(pageIds[0], pageIdList);

            }

        });
        */
        JavaPairRDD<String, List<String>> links = raw_links.mapToPair(
                s -> {
                    String[] pageIds = s.split(" ");
                    List<String> pageIdList = new ArrayList<>();
                    Collections.addAll(pageIdList, pageIds);
                    pageIdList.remove(0);

                    return new Tuple2<>(pageIds[0], pageIdList);
                }
        );

        // How mapValues works: For each element in JavaPairRDD, assign the key's value as the function returns.
        JavaPairRDD<String, Double> ranks = links.mapValues(
                stringList -> 1.0
        );

        /*
        JavaPairRDD<String, Double> contributions = links.join(ranks).flatMapToPair(
                new PairFlatMapFunction<Tuple2<String, Tuple2<List<String>, Double>>, String, Double>() {
                    @Override
                    public Iterator<Tuple2<String, Double>> call(Tuple2<String, Tuple2<List<String>, Double>> stringTuple2Tuple2) throws Exception {
                        double contrib = stringTuple2Tuple2._2._2 / stringTuple2Tuple2._2._1.size();
                        List<Tuple2<String, Double>> destRanks = new ArrayList<>(stringTuple2Tuple2._2._1.size());
                        for (String dest : stringTuple2Tuple2._2._1) {

                            destRanks.add(new Tuple2<>(dest, contrib));
                        }
                        return destRanks.iterator();
                    }
                }
        );
        */

        for (int i = 0 ; i < 10 ; ++i) {
            // How join works: Join two JavaPairRDD using the same key, use the value of two JavaPairRDD to generate
            // a Tuple2 as the value.
            JavaPairRDD<String, Tuple2<List<String>, Double>> linksAndRanks = links.join(ranks);

            // How flatMapToPair works: For each element in JavaPairRDD(a Tuple2), generate a iterator of pair(a new Tuple2).
            // Spark gets all them and aggregates them to generate a new JavaPairRDD.
            JavaPairRDD<String, Double> contributions = linksAndRanks.flatMapToPair(
                    pageIdLinksAndRank -> {

                        // contrib is the sourcePageId's rank contributes to the targetPageId's rank.
                        double contrib = pageIdLinksAndRank._2._2 / pageIdLinksAndRank._2._1.size();
                        List<Tuple2<String, Double>> destRanks = new ArrayList<>(pageIdLinksAndRank._2._1.size());
                        for (String dest : pageIdLinksAndRank._2._1) {

                            destRanks.add(new Tuple2<>(dest, contrib));
                        }
                        return destRanks.iterator();
                    }
            );
            ranks = contributions.reduceByKey((x, y) -> x + y).mapValues(d -> 0.15 + 0.85 * d);
        }

        ranks = ranks.sortByKey();

        ranks.saveAsTextFile("page_rank_output");

    }
}