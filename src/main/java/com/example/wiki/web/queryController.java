package com.example.wiki.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.example.wiki.dataEntry;
import com.example.wiki.data.dataRepository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RestController
@RequestMapping(path = "/query", produces = "application/json")
@CrossOrigin(origins = "*")
public class queryController {
    private dataRepository dataRepo;
    private MongoTemplate mt;

    public queryController(dataRepository dataRepo, MongoTemplate mt){
        this.dataRepo = dataRepo;
        this.mt = mt;
    }

//    @GetMapping(path = "/user{user}", produces="application/json")
//    public Iterable<dataEntry> allOrders(@PathVariable("user") String user) {
//        return dataRepo.findByUser(user);
//    }

//    @GetMapping(path = "/user{user}", produces="application/json")
//    public Iterable<dataEntry> allOrders(@PathVariable("user") String user) {
//
//        return dataRepo.findByUser(user);
//    }

    @GetMapping(path = "/user{user}", produces="application/json")
    public Iterable<dataEntry> allOrders(@PathVariable("user") String user) {

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        return mt.find(query, dataEntry.class);
    }
//    @GetMapping("/user{user}")
//    public Iterable<dataEntry> findByUser(@PathVariable("user") String user){
//        PageRequest page = PageRequest.of(
//                0, 12, Sort.by("timestamp").descending());
//        return dataRepo.findByuser(user, page).get;
//    }
}
