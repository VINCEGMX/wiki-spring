package com.example.wiki;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.example.wiki.data.dataRepository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@SpringBootApplication
public class WikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikiApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(dataRepository repo, MongoTemplate mt) {

        return args -> {

            loadUserType("bots.txt", "bots", mt);
            loadUserType("administrators.txt", "admins", mt);

            Query query = new Query();
            query.addCriteria(Criteria.where("usertype").exists(false).
                    andOperator(Criteria.where("anon").exists(true)));
            Update update = new Update();
            update.set("usertype", "anon");
            mt.updateMulti(query, update, dataEntry.class);

            query = new Query();
            query.addCriteria(Criteria.where("usertype").exists(false).
                    andOperator(Criteria.where("anon").exists(false)));
            update = new Update();
            update.set("usertype", "regular");
            mt.updateMulti(query, update, dataEntry.class);

            System.out.println("done!");
        };
    }

    public void loadUserType(String path, String userType, MongoTemplate mt){
        Set<String> userGroup = new HashSet<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                userGroup.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("user").in(userGroup).
                andOperator(Criteria.where("usertype").ne("bot")));
        Update update = new Update();
        update.set("usertype", userType);
        mt.updateMulti(query, update, dataEntry.class);
    }
}
