package com.demus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/19 17:57
 */
public class Main {


    public static void main(String[] args) throws JsonProcessingException {
        Employee employee = new Employee();
        employee.setName("");
        employee.setAddress(new AtomicReference<>(null));
        employee.setPhones(new HashMap<String, Integer>(){{
            put("1",1);
            put("0",null);
        }});
        List<List<String>> list = new ArrayList<List<String>>(){{
            add( new ArrayList<>());
            add( null);
        }};
        employee.setFriends(list);
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Map<String, Object> map3 = new HashMap<>();
        map3.put("key", "value");
        List<Map<String, Object>> data = Arrays.asList(map1, map2, map3);
        employee.setData(data);



        ObjectMapper om = new ObjectMapper();
        String jsonString = om.writeValueAsString(employee);
        System.out.println(jsonString);


        Employee readValue = om.readValue(jsonString, Employee.class);
        System.out.println(om.writeValueAsString(readValue));

    }
}
