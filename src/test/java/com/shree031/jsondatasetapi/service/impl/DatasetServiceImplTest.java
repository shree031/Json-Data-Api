package com.shree031.jsondatasetapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shree031.jsondatasetapi.dto.RecordDto;
import com.shree031.jsondatasetapi.entity.DatasetRecord;
import com.shree031.jsondatasetapi.repository.DatasetRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatasetServiceImplTest {

    private DatasetRecordRepository repository;
    private ObjectMapper objectMapper;
    private DatasetServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(DatasetRecordRepository.class);
        objectMapper = new ObjectMapper();
        service = new DatasetServiceImpl(repository, objectMapper);
    }

    @Test
    void insertRecord_shouldSaveRecordAndReturnDtoWithId() {
        RecordDto dto = new RecordDto();
        dto.setName("Alice");
        dto.setAge(25);
        dto.setDepartment("Engineering");

        DatasetRecord savedRecord = new DatasetRecord();
        savedRecord.setId(101L);

        when(repository.save(any())).thenReturn(savedRecord);

        RecordDto result = service.insertRecord("test_dataset", dto);

        assertEquals(101L, result.getId());
        verify(repository).save(any(DatasetRecord.class));
    }


    @Test
    void groupByField_shouldReturnGroupedMap() throws JsonProcessingException {
        DatasetRecord r1 = new DatasetRecord();
        r1.setJsonData(objectMapper.writeValueAsString(new RecordDto() {{
            setId(1L); setName("Alice"); setDepartment("Tech"); setAge(23);
        }}));

        DatasetRecord r2 = new DatasetRecord();
        r2.setJsonData(objectMapper.writeValueAsString(new RecordDto() {{
            setId(2L); setName("Bob"); setDepartment("Tech"); setAge(28);
        }}));

        DatasetRecord r3 = new DatasetRecord();
        r3.setJsonData(objectMapper.writeValueAsString(new RecordDto() {{
            setId(3L); setName("Charlie"); setDepartment("HR"); setAge(31);
        }}));

        when(repository.findByDatasetName("my_ds")).thenReturn(List.of(r1, r2, r3));

        Map<String, List<RecordDto>> result = service.groupByField("my_ds", "department");

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Tech"));
        assertTrue(result.containsKey("HR"));
        assertEquals(2, result.get("Tech").size());
        assertEquals(1, result.get("HR").size());
    }

    @Test
    void sortByField_shouldReturnSortedListAsc() throws JsonProcessingException {
        RecordDto d1 = new RecordDto(); d1.setName("Bob"); d1.setAge(22);
        RecordDto d2 = new RecordDto(); d2.setName("Alice"); d2.setAge(20);
        RecordDto d3 = new RecordDto(); d3.setName("Charlie"); d3.setAge(30);

        DatasetRecord r1 = new DatasetRecord(); r1.setJsonData(objectMapper.writeValueAsString(d1));
        DatasetRecord r2 = new DatasetRecord(); r2.setJsonData(objectMapper.writeValueAsString(d2));
        DatasetRecord r3 = new DatasetRecord(); r3.setJsonData(objectMapper.writeValueAsString(d3));

        when(repository.findByDatasetName("ds")).thenReturn(List.of(r1, r2, r3));

        List<RecordDto> result = service.sortByField("ds", "name", "asc");

        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
        assertEquals("Charlie", result.get(2).getName());
    }

    @Test
    void sortByField_shouldReturnSortedListDesc() throws JsonProcessingException {
        RecordDto d1 = new RecordDto(); d1.setName("A");
        RecordDto d2 = new RecordDto(); d2.setName("B");

        DatasetRecord r1 = new DatasetRecord(); r1.setJsonData(objectMapper.writeValueAsString(d1));
        DatasetRecord r2 = new DatasetRecord(); r2.setJsonData(objectMapper.writeValueAsString(d2));

        when(repository.findByDatasetName("ds")).thenReturn(List.of(r1, r2));

        List<RecordDto> result = service.sortByField("ds", "name", "desc");

        assertEquals("B", result.get(0).getName());
        assertEquals("A", result.get(1).getName());
    }


    @Test
    void getAll_shouldReturnParsedList() throws JsonProcessingException {
        Map<String, Object> data = Map.of("name", "Jane", "age", 27);

        DatasetRecord record = new DatasetRecord();
        record.setJsonData(objectMapper.writeValueAsString(data));

        when(repository.findByDatasetName("ds")).thenReturn(List.of(record));

        List<Map<String, Object>> result = service.getAll("ds");

        assertEquals(1, result.size());
        assertEquals("Jane", result.get(0).get("name"));
    }

    @Test
    void getAll_shouldFilterOutInvalidJson() {
        DatasetRecord badRecord = new DatasetRecord();
        badRecord.setJsonData("{invalidJson}");

        when(repository.findByDatasetName("ds")).thenReturn(List.of(badRecord));

        List<Map<String, Object>> result = service.getAll("ds");

        assertTrue(result.isEmpty());
    }
}
