package cn.van.concurrency.limit.web.controller;

import cn.van.concurrency.limit.annotation.AccessLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C), 2015-2019, 风尘博客
 * 公众号 : 风尘博客
 * FileName: TestControler
 *
 * @author: Van
 * Date:     2019-12-10 15:36
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@RestController
@RequestMapping("/limit")
public class TestControler {

    @GetMapping("/test")
    @AccessLimit(seconds = 3, maxCount = 1)
    public String limit() {
        return "访问成功！";
    }
}
