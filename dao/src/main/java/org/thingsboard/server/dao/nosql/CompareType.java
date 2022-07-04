package org.thingsboard.server.dao.nosql;

import lombok.Data;

/**
 * @author wb04
 * @version 1.0
 * @description: TODO
 * @date 2022/5/6 14:45
 */
@Data
public class CompareType {

    String gt= " > ";

    String  lt=" < ";


    String largeEqual=" >= ";

    String smallEqual= " <= ";

    public CompareType() {
    }
}
