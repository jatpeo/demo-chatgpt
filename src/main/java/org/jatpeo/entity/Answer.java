package org.jatpeo.entity;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class Answer {

    private String id;
    private String object;
    private String model;
    private int created;

    //回答
    private List<Choices> choices;
    private Usage usage;
}
