package com.veyselpehlivan.dashboard.kafkaconsumer.repository;

import com.veyselpehlivan.dashboard.kafkaconsumer.model.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ElasticsearchRepository<Log, Integer> {


}
