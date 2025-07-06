package com.shree031.jsondatasetapi.controller;

import com.shree031.jsondatasetapi.dto.RecordDto;
import com.shree031.jsondatasetapi.exception.CustomException;
import com.shree031.jsondatasetapi.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @Operation(summary = "Insert a JSON record", description = "Inserts a JSON record into the given dataset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record inserted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/{datasetName}/record")
    public ResponseEntity<Map<String, Object>> insertRecord(@PathVariable String datasetName, @RequestBody RecordDto dto) {
        RecordDto saved = datasetService.insertRecord(datasetName, dto);
        return ResponseEntity.ok(Map.of(
                "message", "Record added successfully",
                "dataset", datasetName,
                "recordId", saved.getId()
        ));
    }

    @Operation(summary = "Query dataset records", description = "Query records from dataset with optional groupBy or sortBy")
    @Parameters({
            @Parameter(name = "groupBy", description = "Field to group records by"),
            @Parameter(name = "sortBy", description = "Field to sort records by"),
            @Parameter(name = "order", description = "Sort order (asc or desc)")
    })
    @GetMapping("/{datasetName}/query")
    public ResponseEntity<Object> queryDataset(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {

        if (groupBy != null) {
            return ResponseEntity.ok(Map.of("groupedRecords", datasetService.groupByField(datasetName, groupBy)));
        } else if (sortBy != null) {
            return ResponseEntity.ok(Map.of("sortedRecords", datasetService.sortByField(datasetName, sortBy, order)));
        } else {
            return  ResponseEntity.ok(datasetService.getAll(datasetName));
        }
    }
}
