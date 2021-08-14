package com.example.wiki.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.example.wiki.dataEntry;
import com.example.wiki.data.dataRepository;

@RestController
@RequestMapping(path = "/query", produces = "application/json")
@CrossOrigin(origins = "*")
public class queryController {
    private dataRepository dataRepo;

    public queryController(dataRepository dataRepo){
        this.dataRepo = dataRepo;
    }

}