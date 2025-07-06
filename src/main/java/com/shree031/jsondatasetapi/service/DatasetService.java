package com.shree031.jsondatasetapi.service;

import com.shree031.jsondatasetapi.dto.RecordDto;

import java.util.List;
import java.util.Map;

public interface DatasetService {

    List<Map<String, Object>> getAll(String datasetName);

    RecordDto insertRecord(String datasetName, RecordDto dto);

    Map<String, List<RecordDto>> groupByField(String datasetName, String field);

    List<RecordDto> sortByField(String datasetName, String field, String order);
}
