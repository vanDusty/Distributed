package cn.van.distributed.packet.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: DoubleUtil
 *
 * @author: Van
 * Date:     2020-02-09 22:46
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
public class DoubleUtil implements Serializable {
    private static final long serialVersionUID = -3345205828566485102L;
    /**
     * 默认除法运算精度
     */
    private static final Integer DEF_DIV_SCALE = 2;

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时， 精确到小数点以后10位，以后的数字四舍五入。
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 两个参数的商
     */
    public static Double divide(Double dividend, Double divisor) {
        BigDecimal b1 = new BigDecimal(Double.toString(dividend));
        BigDecimal b2 = new BigDecimal(Double.toString(divisor));
        return b1.divide(b2, DEF_DIV_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

}
