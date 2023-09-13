package com.kob.backend.pojo.tomato;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TomatoDays {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer year;
    private Integer month;
    private Integer day;
    private Boolean state;
    private Integer userId;
    private Integer focusSeconds;
}
