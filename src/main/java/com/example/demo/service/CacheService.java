package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.bson.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Cacheable(value = "employees", key = "#id")
    public Document getEmployeeById(int id) {
        String employeeJson = redisTemplate.opsForValue().get("employee_" + id);
        return jsonToDocument(employeeJson);
    }

    @CachePut(value = "employees", key = "#result.getInteger('id')")
    public Document updateEmployeeCache(Document employee) {
        String employeeJson = documentToJson(employee);
        redisTemplate.opsForValue().set("employee_" + employee.getInteger("id"), employeeJson);
        redisTemplate.expire("employee_" + employee.getInteger("id"), 30, TimeUnit.SECONDS);
        return employee;
    }

    @CacheEvict(value = "employees", key = "#id")
    public void evictEmployeeCache(int id) {
        redisTemplate.delete("employee_" + id);
    }
    
    @CacheEvict(value = "employees", allEntries = true)
    public void evictAllEmployees() {
        Set<String> employeeKeys = redisTemplate.keys("employee_*");
        redisTemplate.delete(employeeKeys);
    }

    public List<Document> getAllEmployees() {
        Set<String> employeeKeys = redisTemplate.keys("employee_*");
        
        List<Document> employees = employeeKeys.stream()
                .map(key -> {
                    String employeeJson = redisTemplate.opsForValue().get(key);
                    return jsonToDocument(employeeJson);
                })
                .collect(Collectors.toList());

        return employees;
    }

    private String documentToJson(Document employee) {
        return employee.toJson();
    }

    private Document jsonToDocument(String employeeJson) {
        return Document.parse(employeeJson);
    }
}
