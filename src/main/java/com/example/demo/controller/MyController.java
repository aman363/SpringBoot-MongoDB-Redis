package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import com.example.demo.models.Employee;
import com.example.demo.models.QA;
import com.example.demo.service.CacheService;
import com.example.demo.models.Development;
import com.example.demo.models.Manager;

@RestController
@RequestMapping("/employee")
public class MyController {


    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private CacheService cacheService;

    @PostMapping("/")
    public ResponseEntity<?> addEmployee(@RequestBody String jsonBody) throws IOException {
        JSONObject inputParams = new JSONObject(jsonBody);

        int id = inputParams.getInt("id");
        String name = inputParams.getString("name");
        String designation = inputParams.getString("designation");
        String city = inputParams.getString("city");
        String description = inputParams.getString("description");

        Employee employee;
        switch (designation) {
            case "QA":
                employee = new QA(id, name, designation, city, description);
                break;
            case "Development":
                employee = new Development(id, name, designation, city, description);
                break;
            case "Manager":
                employee = new Manager(id, name, designation, city, description);
                break;
            default:
                throw new IllegalArgumentException("Unknown designation: " + designation);
        }


        Document doc = new Document();
        doc.put("id", employee.getId());
        doc.put("name", employee.getName());
        doc.put("designation", employee.getDesignation());
        doc.put("city", employee.getCity());
        doc.put("description", employee.getDescription());

        mongoTemplate.insert(doc, "Employees");
       // cacheService.updateEmployeeCache(doc);

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/")
    public ResponseEntity<?> getEmployee() {
        //List<Document> documents = mongoTemplate.findAll(Document.class, "Employees");
        //List<Employee> employees = documents.stream().map(this::documentToEmployee).collect(Collectors.toList());
    	List<Document> documents = cacheService.getAllEmployees();
    	List<Employee> employees = documents.stream()
                .map(this::documentToEmployee)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    private Employee documentToEmployee(Document doc) {
        String designation = doc.getString("designation");
        int id = doc.getInteger("id");
        String name = doc.getString("name");
        String city = doc.getString("city");
        String description = doc.getString("description");

        switch (designation) {
            case "QA":
                return new QA(id, name, designation, city, description);
            case "Development":
                return new Development(id, name, designation, city, description);
            case "Manager":
                return new Manager(id, name, designation, city, description);
            default:
                throw new IllegalArgumentException("Unknown employee designation: " + designation);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getEmployees(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name) {

        List<Document> documents;
        if (id != null) {
            documents = mongoTemplate.find(new Query(Criteria.where("id").is(id)), Document.class, "Employees");
        } else if (designation != null) {
            documents = mongoTemplate.find(new Query(Criteria.where("designation").is(designation)), Document.class, "Employees");
        } else if (city != null) {
            documents = mongoTemplate.find(new Query(Criteria.where("city").is(city)), Document.class, "Employees");
        } else if (name != null) {
            documents = mongoTemplate.find(new Query(Criteria.where("name").is(name)), Document.class, "Employees");
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<Employee> employees = documents.stream().map(this::documentToEmployee).collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") int id, @RequestBody String jsonBody) throws IOException {
        JSONObject inputParams = new JSONObject(jsonBody);

        Query query = new Query(Criteria.where("id").is(id));
        Document existingDoc = mongoTemplate.findOne(query, Document.class, "Employees");

        if (existingDoc != null) {
            existingDoc.put("name", inputParams.getString("name"));
            existingDoc.put("designation", inputParams.getString("designation"));
            existingDoc.put("city", inputParams.getString("city"));
            existingDoc.put("description", inputParams.getString("description"));

            mongoTemplate.save(existingDoc, "Employees");

            Employee updatedEmployee = documentToEmployee(existingDoc);

            return ResponseEntity.ok(updatedEmployee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllEmployees() {
        // Clear cache
        cacheService.evictAllEmployees();
        // mongoTemplate.remove(new Query(), "Employees");

        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") int id) {
        Query query = new Query(Criteria.where("id").is(id));
        Document deletedEmployee = mongoTemplate.findAndRemove(query, Document.class, "Employees");

        if (deletedEmployee != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
