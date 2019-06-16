package com.veyselpehlivan.dashboard.kafkaconsumer.listener;


import com.veyselpehlivan.dashboard.kafkaconsumer.model.Log;
import com.veyselpehlivan.dashboard.kafkaconsumer.model.Record;
import com.veyselpehlivan.dashboard.kafkaconsumer.repository.LogRepository;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KafkaConsumer implements ConsumerSeekAware {

    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> level = new ArrayList<String>();
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> detail = new ArrayList<String>();

    @Autowired
    LogRepository logRepository;

    @Autowired
    private SimpMessagingTemplate template;

    private String destinationMessagesIn = "/dashboard/stream-in";
    private String destinationMessagesOut = "/dashboard/stream-out";


    @KafkaListener(topics = "data-in6", groupId = "group_id")
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
                parts[i] = parts[i].substring(0, parts[i].length() - 1);
                detail.add(parts[i]);
            }


        }

        //System.out.println(time.toString());

        // Sending statistics to websocket

        List<Record> recordList = new ArrayList<>();
        List<Log> logList = new ArrayList<>();

        for (int i = 0; i < time.size(); i++) {
            recordList.add(new Record(time.get(i), city.get(i)));

            logList.add(new Log(i, time.get(i), level.get(i), city.get(i), detail.get(i)));



        }



        logRepository.deleteAll();




        logRepository.saveAll(logList);


        //System.out.println("LOGLIST " + logList.size());


        Map<String,Map<String,Long>> map =
                recordList.stream()
                        .collect(Collectors.groupingBy(e -> e.getTime(), TreeMap::new,
                                Collectors.groupingBy(x -> x.getCity(), Collectors.counting()))
                        );




        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesIn, k + " " + a + " " + b)));

        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesOut, k + " " + a + " " + b)));



        //Sending to Elasticsearch

        //logRepository.findAll();





    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback consumerSeekCallback) {

    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.keySet().stream()
                .filter(partition -> "data-in6".equals(partition.topic()))
                .forEach(partition -> callback.seekToBeginning("data-in6", partition.partition()));
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {

    }

}
