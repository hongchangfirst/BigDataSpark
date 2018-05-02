package com.zhc.example.loading;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avro.ipc.specific.Person;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.storage.StorageLevel;
import org.codehaus.jackson.map.ObjectMapper;

public class LoadingJsonFile {
    /**
     *
     * @param sc JavaSparkContext
     */
    public static void run(JavaSparkContext sc) {

        String inputFile = "input.json";
        JavaRDD<String> input = sc.textFile(inputFile);
        System.out.println("=================================");

        // Use Functional Object
        // JavaRDD<Person> people = input.mapPartitions(new PersonParseJson());

        // Use Lambda
        // How does mapPartitions works: For each partition(instead of element), call the function.
        // For big data process, if there's resource initialization step such as db connection, mapPartition
        // can share the db connection so that it's more efficient.
        // For example. you have 10 elements, and have 3 partitions. if you use mapPartitions, the function is
        // called 3 times. if you use map, the function is called 10 times.
        /*
        JavaRDD<Person> people = input.mapPartitions(
               lines -> {
                   ArrayList<Person> peo = new ArrayList<>();
                   ObjectMapper mapper = new ObjectMapper();
                   while (lines.hasNext()) {
                       String line = lines.next();
                       try {
                           Person p = mapper.readValue(line, Person.class);
                           peo.add(p);
                       } catch (Exception e) {
                           // skip records
                           System.out.println(line);
                           System.out.println(e.getStackTrace());
                       }
                   }
                   return peo.iterator();
               }
        );
        */

        // we are not allowed to return null during function, if we return null, maybe a NullPointerException
        // will be thrown if there is a reference afterwards. We can return an empty object and then filter it.
        JavaRDD<Person> people = input.map(
               line -> {
                   ObjectMapper mapper = new ObjectMapper();
                   try {
                       Person p = mapper.readValue(line, Person.class);
                       return p;
                   } catch (Exception e) {
                       // skip records
                   }
                   // can't return null
                   // return null;
                   return new Person();
               }
        );

        JavaRDD<Person> men = people.filter(
                p -> p.sex && !p.name.isEmpty()
        );

        // Since we need use twice, so we persist to avoid calculating linear graph again.
        // men.saveAsTextFile("output_person.json");
        // why I use persist, the function is still called twice?
        men.persist(StorageLevel.MEMORY_AND_DISK());

        men.map(
                p -> {
                    System.out.println("======================================name " + p.name);
                    ObjectMapper mapper = new ObjectMapper();
                    String s = mapper.writeValueAsString(p);
                    return s;
                }
        ).repartition(2).saveAsTextFile("output_mapper.json");



    }

    /*
    static class PersonParseJson implements FlatMapFunction<Iterator<String>, Person> {
        @Override
        public Iterator<Person> call(Iterator<String> lines) throws Exception {
            ArrayList<Person> people = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            while (lines.hasNext()) {
                String line = lines.next();
                try {
                    Person p = mapper.readValue(line, Person.class);
                    people.add(p);
                } catch (Exception e) {
                    // skip records
                    System.out.println(line);
                    System.out.println(e.getStackTrace());
                }
            }
            return people.iterator();
        }
    }
    */

    static class Person {
        public String name;
        public int age;
        public boolean sex;
    }
}