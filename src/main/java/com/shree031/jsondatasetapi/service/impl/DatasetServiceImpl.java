package com.shree031.jsondatasetapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shree031.jsondatasetapi.dto.RecordDto;
import com.shree031.jsondatasetapi.entity.DatasetRecord;
import com.shree031.jsondatasetapi.exception.CustomException;
import com.shree031.jsondatasetapi.repository.DatasetRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shree031.jsondatasetapi.service.DatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRecordRepository repository;
    private final ObjectMapper objectMapper;

    public DatasetServiceImpl(DatasetRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public RecordDto insertRecord(String datasetName, RecordDto dto) {
        try {
            DatasetRecord datasetRecord = new DatasetRecord();
            datasetRecord.setDatasetName(datasetName);
            datasetRecord.setJsonData(objectMapper.writeValueAsString(dto));
            DatasetRecord saved = repository.save(datasetRecord);
            dto.setId(saved.getId());
            return dto;
        } catch (JsonProcessingException e) {
            throw new CustomException("Failed to parse JSON data");
        }
    }


    @Override
    public Map<String, List<RecordDto>> groupByField(String datasetName, String field) {
        return repository.findByDatasetName(datasetName).stream().map(this::toDto).collect(Collectors.groupingBy(dto -> extractFieldValue(dto, field)));
    }

    @Override
    public List<RecordDto> sortByField(String datasetName, String field, String order) {
        Comparator<RecordDto> comparator = Comparator.comparing(dto -> (Comparable) extractFieldValue(dto, field));

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return repository.findByDatasetName(datasetName).stream().map(this::toDto).sorted(comparator).collect(Collectors.toList());
    }


    private RecordDto toDto(DatasetRecord datasetRecord) {
        try {
            return objectMapper.readValue(datasetRecord.getJsonData(), RecordDto.class);
        } catch (JsonProcessingException e) {
            throw new CustomException("Invalid JSON in dataset datasetRecord ID: " + datasetRecord.getId());
        }
    }


    private String extractFieldValue(RecordDto dto, String fieldName) {
        try {
            Field field = RecordDto.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return String.valueOf(field.get(dto));
        } catch (Exception e) {
            throw new CustomException("Invalid groupBy/sortBy field: " + fieldName);
        }
    }

    @Override
    public List<Map<String, Object>> getAll(String datasetName) {
        return repository.findByDatasetName(datasetName).stream().map(datasetRecord -> {
            System.out.println(datasetRecord);
            try {
                return objectMapper.readValue(datasetRecord.getJsonData(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
