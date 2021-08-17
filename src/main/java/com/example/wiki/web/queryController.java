package com.example.wiki.web;

import com.example.wiki.*;
import com.example.wiki.data.dataRepository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;



@RestController
@RequestMapping(path = "/query", produces = "application/json")
@CrossOrigin(origins = "*")
public class queryController {
    private dataRepository dataRepo;
    private MongoTemplate mt;
    int fromYear = 2001;
    int toYear = 2020;

    @Autowired
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

    @GetMapping(path = "/showExHisArticle/{num}", produces="application/json")
    public Iterable<showExHisArticleOutput> showExHisArticle(@PathVariable("num") int num) {

        String jsonExpression = "{\"$divide\":[{\"$subtract\":[\"$$NOW\",\"$minTime\"]},86400000]}";
        Aggregation agg = newAggregation(
                group("title").min("timestamp").as("minTime"),
                project().and(context -> context.getMappedObject(Document.parse(jsonExpression))).as("age")
                        .and("title").previousOperation(),
                        sort(Sort.Direction.DESC, "age"),
                        limit(num)
        );

        AggregationResults<showExHisArticleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, showExHisArticleOutput.class);

        return groupResults.getMappedResults();
    }

    /*

      @GetMapping(path = "/revsByUsertypeByYear", produces="application/json")
    */

    @GetMapping(path = "/revDisByUsertype", produces="application/json")
    public Iterable<revDisByUsertypeOutput> revDisByUsertype() {

        Aggregation agg = newAggregation(
                group("usertype").count().as("usertypeCount"),
                project("usertypeCount").and("usertype").previousOperation(),
                sort(Sort.Direction.DESC, "usertypeCount")

        );

        AggregationResults<revDisByUsertypeOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, revDisByUsertypeOutput.class);

        return groupResults.getMappedResults();
    }

    @GetMapping(path = "/totalRevTitle", produces="application/json")
    public Iterable<totalRevTitleOutput> totalRevTitle() {
        Aggregation agg = newAggregation(
                project("title").and("timestamp").extractYear().as("year"),
                match(Criteria.where("year").gte(fromYear).andOperator(Criteria.where("year").lte(toYear))),
                group("title").count().as("totalRevisions"),
                project("totalRevisions").and("title").previousOperation(),
                sort(Sort.Direction.DESC, "totalRevisions")

        );

        AggregationResults<totalRevTitleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, totalRevTitleOutput.class);

        return groupResults.getMappedResults();
    }

    @GetMapping(path = "/totalRevByTitle/{title}", produces="application/json")
    public Iterable<totalRevTitleOutput> totalRevByTitle(@PathVariable("title") String title) {
        Aggregation agg = newAggregation(
                project("title").and("timestamp").extractYear().as("year"),
                match(Criteria.where("year").gte(fromYear).lte(toYear)
                        .andOperator(Criteria.where("title").is(title))),
                group("title").count().as("totalRevisions"),
                project("totalRevisions").and("title").previousOperation()
        );

        AggregationResults<totalRevTitleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, totalRevTitleOutput.class);

        return groupResults.getMappedResults();
    }

    @GetMapping(path = "/topRegularUsers/{title}", produces="application/json")
    public Iterable<topRegularUsersOutput> topRegularUsers(@PathVariable("title") String title) {
        Aggregation agg = newAggregation(
                project("title","usertype","user").and("timestamp").extractYear().as("year"),
                match(Criteria.where("year").gte(fromYear).lte(toYear)
                        .andOperator(Criteria.where("title").is(title), Criteria.where("usertype").is("regular"))),
                group("user").count().as("userRevisions"),
                project("userRevisions").and("user").previousOperation(),
                sort(Sort.Direction.DESC, "userRevisions"),
                limit(5)
        );

        AggregationResults<topRegularUsersOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, topRegularUsersOutput.class);

        return groupResults.getMappedResults();
    }

    /*

      @GetMapping(path = "/revDisByYearByUsertypeArticle", produces="application/json")
    */

    @GetMapping(path = "/revDisByUsertypeArticle/{title}", produces="application/json")
    public Iterable<revDisByUsertypeArticleOutput> revDisByUsertypeArticle(@PathVariable("title") String title) {
        Aggregation agg = newAggregation(
                project("title","usertype").and("timestamp").extractYear().as("year"),
                match(Criteria.where("year").gte(fromYear).lte(toYear)
                        .andOperator(Criteria.where("title").is(title))),
                group("usertype").count().as("userTypeRevisions"),
                project("userTypeRevisions").and("usertype").previousOperation()
        );

        AggregationResults<revDisByUsertypeArticleOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, revDisByUsertypeArticleOutput.class);

        return groupResults.getMappedResults();
    }

    @GetMapping(path = "/revDisByYearTopRegUser/{title}&{user}", produces="application/json")
    public Iterable<revDisByYearTopRegUserOutput> revDisByYearTopRegUser(@PathVariable("title") String title,
                                                                          @PathVariable("user") String user) {
        Aggregation agg = newAggregation(
                project("title","user").and("timestamp").extractYear().as("year"),
                match(Criteria.where("year").gte(fromYear).lte(toYear)
                        .andOperator(Criteria.where("title").is(title), Criteria.where("user").is(user))),
                group("year").count().as("userYearRevisions"),
                project("userYearRevisions").and("year").previousOperation(),
                sort(Sort.Direction.DESC, "userYearRevisions")
        );

        AggregationResults<revDisByYearTopRegUserOutput> groupResults
                = mt.aggregate(agg, dataEntry.class, revDisByYearTopRegUserOutput.class);

        return groupResults.getMappedResults();
    }

}
