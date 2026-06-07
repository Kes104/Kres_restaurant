package com.kar.table;

import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class TableService {
    
    private final RestaurantTableRepository restaurantTableRepository;

    public TableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public Iterable<RestaurantTable> getAllTables() {
        return restaurantTableRepository.findAll();
    }

    public RestaurantTable getTableById(Long id) {
        return restaurantTableRepository.findById(id).orElse(null);
    }

    public Iterable<RestaurantTable> getTablesByStatus(RestaurantTable.TableStatus status) {
        return restaurantTableRepository.findByStatus(status);
    }

    public Iterable<RestaurantTable> getTablesBySize(int size) {
        return restaurantTableRepository.findBySize(size);
    }

    public Iterable<RestaurantTable> getAvailableTables() {
        return restaurantTableRepository.findByStatus(RestaurantTable.TableStatus.AVAILABLE);
    }

    public Iterable<RestaurantTable> updateTableStatus(Long id, RestaurantTable.TableStatus status) {
        RestaurantTable table = restaurantTableRepository.findById(id).orElse(null);
        if (table != null) {
            table.setStatus(status);
            restaurantTableRepository.update(table);
        }
        return restaurantTableRepository.findAll();
    }

    public RestaurantTable assignTable(int partySize) {
        List<Integer> sizes = List.of(2, 4, 8);
        for (int size : sizes) {
            if (size >= partySize) {
                var tables = restaurantTableRepository.findByStatusAndSize(
                        RestaurantTable.TableStatus.AVAILABLE, size);
                if (tables.iterator().hasNext()) {
                    var table = tables.iterator().next();
                    table.setStatus(RestaurantTable.TableStatus.OCCUPIED);
                    restaurantTableRepository.update(table);
                    return table;
                }
            }
        }
        return null;
    }

}
