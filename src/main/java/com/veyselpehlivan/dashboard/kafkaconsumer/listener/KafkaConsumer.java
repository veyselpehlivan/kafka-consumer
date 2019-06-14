package com.veyselpehlivan.dashboard.kafkaconsumer.listener;


import com.veyselpehlivan.dashboard.kafkaconsumer.record.Record;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class KafkaConsumer implements ConsumerSeekAware {

    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> level = new ArrayList<String>();
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> detail = new ArrayList<String>();


    @KafkaListener(topics = "data-in3", groupId = "group_id")
    public void consume(String message){

        String[] parts = message.split("   ");

        for (int i=0; i<parts.length; i++) {
            if (i%4 == 0){
                parts[i] = parts[i].substring(1);
                parts[i] = parts[i].substring(0, 10);
                time.add(parts[i]);

            }
            else if (i%4 == 1){
                level.add(parts[i]);
            }
            else if (i%4 == 2){
                city.add(parts[i]);
            }
            else if (i%4 == 3){
                detail.add(parts[i]);
            }






        }

        List<Record> recordList = new ArrayList<>();

        for (int i = 0; i < time.size(); i++) {
            recordList.add(new Record(time.get(i), city.get(i)));
        }

        //System.out.println(recordList.toString());
        //System.out.println(recordList.size());
        //System.out.println(time.size());


//        Map<String, Map<String, List<Record>>>
//        recordByDateByCity =  recordList.stream()
//            .collect(Collectors.groupingBy(Record::getTime, Collectors.groupingBy(Record::getCity) ));


        Map<String,Map<String,Long>> map =
                recordList.stream()
                        .collect(Collectors.groupingBy(e -> e.getTime(),
                                Collectors.groupingBy(x -> x.getCity(), Collectors.counting()))
                        );

        map.forEach((k,v)-> v.forEach((a,b)-> System.out.println(k + " " +  a + " " + b)));
        System.out.println("---------------------------------------");





        //System.out.println(recordByDateByCity);






//        for (int i = 1; i <time.size() ; i++) {
//            System.out.print(time.get(i) + ", ");
//        }

        //System.out.println(time.toString());

        //System.out.println("-------------");

        //System.out.println("SIZE OF CITY ARRAY IS " + city.size());
        //System.out.println("SIZE OF TIME ARRAY IS " + time.size());


    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback consumerSeekCallback) {

    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.keySet().stream()
                .filter(partition -> "data-in3".equals(partition.topic()))
                .forEach(partition -> callback.seekToBeginning("data-in3", partition.partition()));
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {

    }

}
