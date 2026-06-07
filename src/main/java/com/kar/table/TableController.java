package com.kar.table;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
// import java.util.Optional;
import io.micronaut.http.annotation.Put;

@Controller("/tables")
public class TableController {
    
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @Get("/")
    public Iterable<RestaurantTable> getAllTables() {
        return tableService.getAllTables();
    }

    @Get("/available")
    public Iterable<RestaurantTable> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    @Get("/{id}")
    public RestaurantTable getTableById(@PathVariable Long id) {
        return tableService.getTableById(id);
    }

    @Put("/status/{status}")
    public Iterable<RestaurantTable> getTablesByStatus(@PathVariable Long id, @Body RestaurantTable.TableStatus status) {
        return tableService.updateTableStatus(id, status);
    }

    @Get("/size/{size}")
    public Iterable<RestaurantTable> getTablesBySize(@PathVariable int size) {
        return tableService.getTablesBySize(size);
    }
}
