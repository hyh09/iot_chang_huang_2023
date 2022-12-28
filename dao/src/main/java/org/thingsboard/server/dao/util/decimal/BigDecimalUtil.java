package org.thingsboard.server.dao.util.decimal;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Project Name: iot-business
 * @File Name: BigDecimalUtil
 * @Date: 2022/10/25 18:43
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2022,All Rights Reserved.
 */
public class BigDecimalUtil {

    public final static BigDecimalUtil INSTANCE = new BigDecimalUtil();

    /**
     * 默认保留2位小数
     */
    public BigDecimalUtil() {
        this.scale = 2;
        this.roundingMode = RoundingMode.HALF_UP;
    }

    /**
     * 自定义保留小数点策略
     *
     * @param scale
     * @param roundingMode
     */
    public BigDecimalUtil(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    /**
     * 保留小数位
     */
    private int scale;
    /**
     * 小数舍入模式
     */
    private RoundingMode roundingMode;


    /**
     * 数字格式化
     * 1.如果number1 为null ，则返回0
     * 2.如果为负数 则也返回0
     *
     * @param number1
     * @return
     */
    public BigDecimal formatByObject(Object number1) {
        if (number1 == null) {
            return BigDecimal.ZERO;
        }
        //java.lang.NumberFormatException 的问题，本地修复
//        if(StringUtils.isEmpty(number1.toString())){
//            return BigDecimal.ZERO;
//        }
        //java.lang.NumberFormatException: Character w is neither a decimal digit number, decimal point, nor "e" notation exponential mark.
        try {
            BigDecimal result = new BigDecimal(number1.toString());
            if (this.equalBigNum2(result, BigDecimal.ZERO)) {
                return result.setScale(scale, roundingMode);
            }
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 加法
     *
     * @param number1
     * @param number2
     * @param numberArr
     * @return
     */
    public BigDecimal add(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).add(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.add((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }


    /**
     * 至少两个数值相减，默认结果四舍五入保留4位小数
     * <p>number1 - number2 - ... - number(n)</p>
     */
    public BigDecimal subtract(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).subtract(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.subtract((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 乘法
     *
     * @param number1
     * @param number2
     * @param numberArr
     * @return
     */
    public BigDecimal multiply(Object number1, Object number2, Object... numberArr) {
        BigDecimal result = new BigDecimal(number1.toString()).multiply(new BigDecimal(number2.toString()));
        for (Object number : numberArr) {
            result = result.multiply((new BigDecimal(number.toString())));
        }
        return result.setScale(scale, roundingMode).stripTrailingZeros();
    }


    /**
     * 除法
     * 至少两个数值相除，除数不能为0，默认结果四舍五入保留4位小数
     *
     * @param number1
     * @param number2
     * @param numberArr
     * @return
     */
    public BigDecimal divide(Object number1, Object number2, Object... numberArr) {
        if (number1 == null || number2 == null) {
            return BigDecimal.ZERO;
        }


        BigDecimal result = new BigDecimal(number1.toString()).divide(new BigDecimal(number2.toString()), scale, roundingMode);
        for (Object number : numberArr) {
            if (number == null) {
                return BigDecimal.ZERO;
            }
            result = result.divide((new BigDecimal(number.toString())), scale, roundingMode);
        }
        return result.stripTrailingZeros();
    }


    /**
     * 累加求和
     *
     * @param finalValueList
     * @return
     */
    public BigDecimal accumulator(List<String> finalValueList) {
        BigDecimal value = finalValueList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1))
                .map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        return value.setScale(scale, roundingMode).stripTrailingZeros();
    }

    /**
     * 累加求和
     *
     * @param finalValueList
     * @return
     */
    public String accumulatorStr(List<String> finalValueList) {
        BigDecimal value = finalValueList.stream()
                .filter(m1 -> StringUtils.isNotEmpty(m1))
                .map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        return value.setScale(scale, roundingMode).stripTrailingZeros().toPlainString();
    }

    public String accumulatorBigDecimal(List<BigDecimal> finalValueList) {
        BigDecimal value = finalValueList.stream()
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        return value.setScale(scale, roundingMode).stripTrailingZeros().toPlainString();
    }

    /**
     * 判断两个数据是否相等
     *
     * @param number1
     * @param number2
     * @return
     */
    public Boolean equalOrNot(Object number1, Object number2) {
        if (number1 == null || number2 == null) {
            return false;
        }
        return new BigDecimal(number1.toString()).compareTo(new BigDecimal(number2.toString())) == 0 ? true : false;
    }

    /**
     * 是否大于 numer2
     *
     * @param number1
     * @param number2
     * @return
     */
    public Boolean equalBigNum2(Object number1, Object number2) {
        if (number1 == null || number2 == null) {
            return false;
        }
        return new BigDecimal(number1.toString()).compareTo(new BigDecimal(number2.toString())) == 1 ? true : false;
    }


}
