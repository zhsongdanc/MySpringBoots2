package com.demus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/19 17:57
 */
public class Employee {

    // content不能用于String
    @JsonInclude(content = Include.NON_EMPTY)
    private String name;
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    private AtomicReference<String> address;

    // 下面这个只对map的value=null的进行排除
    @JsonInclude(content = Include.NON_EMPTY)
    private Map<String, Integer> phones;

    @JsonInclude(content = Include.NON_EMPTY)
    private List<List<String>> friends;

    @JsonInclude(content = Include.NON_EMPTY)
    private List<Map<String, Object>> data;




    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public List<List<String>> getFriends() {
        return friends;
    }

    public void setFriends(List<List<String>> friends) {
        this.friends = friends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicReference<String> getAddress() {
        return address;
    }

    public void setAddress(AtomicReference<String> address) {
        this.address = address;
    }

    public Map<String, Integer> getPhones() {
        return phones;
    }

    public void setPhones(Map<String, Integer> phones) {
        this.phones = phones;
    }

}
