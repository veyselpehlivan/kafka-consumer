package com.veyselpehlivan.dashboard.kafkaconsumer.listener;


import com.veyselpehlivan.dashboard.kafkaconsumer.record.Record;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class KafkaConsumer implements ConsumerSeekAware {

    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> level = new ArrayList<String>();
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> detail = new ArrayList<String>();

    @Autowired
    private SimpMessagingTemplate template;

    private String destinationMessagesIn = "/dashboard/stream-in";
    private String destinationMessagesOut = "/dashboard/stream-out";


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




        Map<String,Map<String,Long>> map =
                recordList.stream()
                        .collect(Collectors.groupingBy(e -> e.getTime(), TreeMap::new,
                                Collectors.groupingBy(x -> x.getCity(), Collectors.counting()))
                        );




        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesIn, k + " " + a + " " + b)));

        map.forEach((k,v)-> v.forEach((a,b)-> template.convertAndSend(destinationMessagesOut, k + " " + a + " " + b)));





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
