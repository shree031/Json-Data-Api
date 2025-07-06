package com.shree031.jsondatasetapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shree031.jsondatasetapi.dto.RecordDto;
import com.shree031.jsondatasetapi.service.DatasetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatasetController.class)
class DatasetControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DatasetService datasetService;

	@Autowired
	private ObjectMapper objectMapper;

	private RecordDto sampleDto;

	@BeforeEach
	void setUp() {
		sampleDto = new RecordDto();
		sampleDto.setId(4L);
		sampleDto.setName("John Doe");
		sampleDto.setAge(30);
		sampleDto.setDepartment("Engineering");
	}

	@Test
	void testInsertRecord() throws Exception {
		Mockito.when(datasetService.insertRecord(eq("employee_dataset"), any(RecordDto.class))).thenReturn(sampleDto);

		mockMvc.perform(post("/api/dataset/employee_dataset/record")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(sampleDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Record added successfully"))
				.andExpect(jsonPath("$.dataset").value("employee_dataset"))
				.andExpect(jsonPath("$.recordId").value(4));
	}

	@Test
	void testQueryAllRecords() throws Exception {
		Map<String, Object> mockRecord = Map.of(
				"id", 1L,
				"name", "John Doe",
				"age", 30,
				"department", "Engineering"
		);
		Mockito.when(datasetService.getAll("employee_dataset")).thenReturn(List.of(mockRecord));

		mockMvc.perform(get("/api/dataset/employee_dataset/query"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("John Doe"))
				.andExpect(jsonPath("$[0].age").value(30))
				.andExpect(jsonPath("$[0].department").value("Engineering"));
	}

	@Test
	void testQueryWithGroupBy() throws Exception {
		RecordDto jane = new RecordDto();
		jane.setId(3L);
		jane.setName("Jane Smith");
		jane.setAge(27);
		jane.setDepartment("Marketing");

		Mockito.when(datasetService.groupByField("employee_dataset", "department"))
				.thenReturn(Map.of("Marketing", List.of(jane)));

		mockMvc.perform(get("/api/dataset/employee_dataset/query")
						.param("groupBy", "department"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groupedRecords.Marketing[0].name").value("Jane Smith"))
				.andExpect(jsonPath("$.groupedRecords.Marketing[0].department").value("Marketing"));
	}

	@Test
	void testQueryWithSortBy() throws Exception {
		RecordDto john = new RecordDto();
		john.setId(1L);
		john.setName("John Doe");
		john.setAge(30);
		john.setDepartment("Engineering");

		RecordDto jane1 = new RecordDto();
		jane1.setId(3L);
		jane1.setName("Jane Smith");
		jane1.setAge(27);
		jane1.setDepartment("Marketing");

		RecordDto jane2 = new RecordDto();
		jane2.setId(1L);
		jane2.setName("Jane Smith");
		jane2.setAge(27);
		jane2.setDepartment("Marketing");

		RecordDto alice = new RecordDto();
		alice.setId(2L);
		alice.setName("Alice Brown");
		alice.setAge(20);
		alice.setDepartment("Marketing");

		List<RecordDto> sortedList = List.of(john, jane1, jane2, alice);

		Mockito.when(datasetService.sortByField("employee_dataset", "name", "asc"))
				.thenReturn(sortedList);

		mockMvc.perform(get("/api/dataset/employee_dataset/query")
						.param("sortBy", "name"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sortedRecords[0].name").value("John Doe"))
				.andExpect(jsonPath("$.sortedRecords[1].name").value("Jane Smith"))
				.andExpect(jsonPath("$.sortedRecords[3].name").value("Alice Brown"));
	}
}
