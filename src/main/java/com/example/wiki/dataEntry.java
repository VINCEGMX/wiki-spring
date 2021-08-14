package com.example.wiki;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Document(collection="revisions")
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class dataEntry {
    @Id
    private String id;
    private int revid;
    private int parentid;
    private boolean minor;
    private String anon;
    private String user;
    private int userid;
    private Date timestamp;
    private int size;
    private String sha1;
    private String parsedcomment;
    private String title;
    private String usertype;
}
