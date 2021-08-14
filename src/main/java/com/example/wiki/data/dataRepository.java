package com.example.wiki.data;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.CrudRepository;

import com.example.wiki.dataEntry;

import java.util.List;

public interface dataRepository extends CrudRepository<dataEntry, String>{

}
