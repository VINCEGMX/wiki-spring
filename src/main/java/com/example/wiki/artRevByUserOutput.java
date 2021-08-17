package com.example.wiki;
import lombok.Data;

@Data
public class artRevByUserOutput {
    private String title;
    private int revisionsByTitleByUser;
}
