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
 * @Date: 2023/10/20 11:15
 */
@Data
@Accessors(chain = true)
public class Chinese extends People{

    private String city;
}
