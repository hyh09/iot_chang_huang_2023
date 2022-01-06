package org.thingsboard.server.dao.hs.utils;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * decimal 序列化
 *
 * @author wwj
 * @since 2022.01.06
 */
public class DecimalSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            BigDecimal number = value.setScale(2, RoundingMode.HALF_UP);
            gen.writeNumber(number);
        } else {
            gen.writeNull();
        }
    }
}