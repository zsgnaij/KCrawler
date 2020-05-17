package com.jy.yande.controller;

import com.alibaba.fastjson.JSONArray;
import com.jy.yande.utils.RestRequestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    RestRequestUtil restRequestUtil;
    @Autowired
    @Qualifier("yandeThreadPool")
    ThreadPoolTaskExecutor threadPool;
    String lineSeparator = System.lineSeparator();


    @GetMapping("/image")
    public String testGetImg(@RequestParam String tag,@RequestParam Integer total){
       List<String> imgIds = new ArrayList<>(total);
       StringBuffer sb = new StringBuffer();
       int page = (int) Math.ceil(total/40);
        for (int i = 0; i < page; i++) {
            String res = restRequestUtil.restGetRequest(String.format("https://yande.re/post?page=%s&tags=%s",i+1,tag),null);
            org.jsoup.nodes.Document doc = Jsoup.parse(res);
            org.jsoup.nodes.Element htmlBody = doc.body();
            Elements select = htmlBody.select("ul#post-list-posts li");
            select.forEach(element -> {
                imgIds.add(element.attr("id").replace("p",""));
            });
        }
        imgIds.forEach(id->{
            threadPool.execute(()->{
                String imgRes = restRequestUtil.restGetRequest(String.format("https://yande.re/post/show/%s",id),null);
                org.jsoup.nodes.Document doc = Jsoup.parse(imgRes);
                org.jsoup.nodes.Element htmlBody = doc.body();
                Element select = htmlBody.select(".original-file-unchanged").first();
                if(null==select){
                    select = htmlBody.select("#highres").first();
                }
                String href = select.attr("href");
                sb.append(href);
                sb.append(lineSeparator);
                System.out.println("href:"+href);
            });
        });
        System.out.println("end");

        return sb.toString();
    }


}
