# standalone mode
./spark-submit --class com.zhc.demo.BigAnalysis ~/workplace/BigDataSpark/target/BigAnalysis-1.0-SNAPSHOT.jar

# standalone remote debug mode
./spark-submit --class com.zhc.demo.BigAnalysis --driver-java-options "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8888" ~/workplace/BigDataSpark/target/BigAnalysis-1.0-SNAPSHOT.jar

