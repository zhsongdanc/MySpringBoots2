package com.demus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/20 11:14
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class People {

    protected String name;

    private final String age = "age";
}


