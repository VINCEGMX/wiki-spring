package com.example.wiki.web;

import com.example.wiki.dataEntry;
import com.example.wiki.data.dataRepository;
import com.example.wiki.showExNumArticleOutput;
import com.example.wiki.showExRegArticleOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;


import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;



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

    @GetMapping(path = "/user{user}", produces="application/json")
    public Iterable<dataEntry> allOrders(@PathVariable("user") String user) {

        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        return mt.find(query, dataEntry.class);
    }

    @GetMapping(path = "/showExNumArticle/{num}", produces="application/json")
    public Iterable<showExNumArticleOutput> showExNumArticle(@PathVariable("num") int num) {

        Aggregation agg = newAggregation(
                group("title").count().as("total"),
                project("total").and("title").previousOperation(),
                sort(Sort.Direction.DESC, "total"),
                limit(num)

        );

        AggregationResults<showExNumArticleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, showExNumArticleOutput.class);

        return groupResults.getMappedResults();
    }

    @GetMapping(path = "/showExRegArticle/{num}", produces="application/json")
    public Iterable<showExRegArticleOutput> showExRegArticle(@PathVariable("num") int num) {

        Aggregation agg = newAggregation(
                match(Criteria.where("usertype").in(new String[]{"admin", "regular"})),
                group("title").addToSet("user").as("users"),
                project().and("users").size().as("groupSize").and("title").previousOperation(),
                sort(Sort.Direction.DESC, "groupSize"),
                limit(num)

        );

        AggregationResults<showExRegArticleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, showExRegArticleOutput.class);

        return groupResults.getMappedResults();
    }

}
