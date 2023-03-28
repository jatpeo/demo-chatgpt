package org.jatpeo.entity;

import lombok.Data;

/**
 * 回答
 */
@Data
public class Choices {

    private String text;
    private Integer index;
    private String logprobs;
    private String finish_reason;

}
