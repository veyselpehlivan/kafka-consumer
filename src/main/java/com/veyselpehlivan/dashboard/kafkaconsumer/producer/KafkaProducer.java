package com.veyselpehlivan.dashboard.kafkaconsumer.producer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Random;


/***
 * The only mission of KafkaProducer class is to edit the log file.
 * To use this class, a file should be created under confluent home directory
 */
public class KafkaProducer {


    public static void main(String[] args) throws Exception {



        String[] cityNames = {"Istanbul", "Tokyo", "Moscow", "Beijing", "London"};
        String[] logLevel = {"INFO", "WARN", "FATAL", "DEBUG", "ERROR"};
        Random random = new Random();

        String filePath = "/home/kafka/confluent-5.2.1/log.txt";


        while (true) {



            Thread.sleep(10000); // do nothing for 10000 miliseconds (10 second)



            int randomCity = random.nextInt(5);
            int randomLogLevel = random.nextInt(5);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String row = timestamp + "   " + logLevel[randomLogLevel] + "   " + cityNames[randomCity] + "   " +  "Hello-from-" + cityNames[randomCity];

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.append(row);
            writer.newLine();

            writer.close();


        }



    }


}


