package cn.van.unique.id;

import cn.van.unique.id.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: UniqueIdTest
 *
 * @author: Van
 * Date:     2019-12-05 20:17
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Slf4j
public class UniqueIdTest {


    @Test
    public void snowFlakeID() {
        for (int i = 0; i < 10; i++) {
            Long uniqueId = SnowFlakeUtil.nextId();
            log.info("uniqueId:{}", uniqueId);
        }
    }
}
