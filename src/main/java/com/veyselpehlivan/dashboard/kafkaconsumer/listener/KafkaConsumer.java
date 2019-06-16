package com.veyselpehlivan.dashboard.kafkaconsumer.listener;


import com.veyselpehlivan.dashboard.kafkaconsumer.model.Log;
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
    private String topic = "data-in6";


    @KafkaListener(topics = "data-in6", groupId = "group_id")
    public void consume(String message){

        // Processing of message
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

        List<Log> logList = new ArrayList<>();
        for (int i = 0; i < time.size(); i++) {
            logList.add(new Log(i, time.get(i), level.get(i), city.get(i), detail.get(i)));

        }


        // To prevent duplicated data in elasticsearch
        logRepository.deleteAll();
        // Saving logs to elasticsearch
        logRepository.saveAll(logList);

        // Calculation of log counts of cities in a day
        Map<String,Map<String,Long>> map =
                logList.stream()
                        .collect(Collectors.groupingBy(e -> e.getTimeStamp(), TreeMap::new,
                                Collectors.groupingBy(x -> x.getCityName(), Collectors.counting()))
                        );



        //For showing messages on frontend
        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesIn, k + " " + a + " " + b)));

        //For adding data to chart
        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesOut, k + " " + a + " " + b)));

    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback consumerSeekCallback) {

    }

    // This method has same meaning with -from-beginning
    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.keySet().stream()
                .filter(partition -> topic.equals(partition.topic()))
                .forEach(partition -> callback.seekToBeginning(topic, partition.partition()));
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {

    }

}
