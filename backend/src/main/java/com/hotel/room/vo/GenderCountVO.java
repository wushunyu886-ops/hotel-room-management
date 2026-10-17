package com.hotel.room.vo;

import com.hotel.room.enums.Gender;
import lombok.Data;

/**
 * 性别统计 VO
 */
@Data
public class GenderCountVO {

    private Gender gender;
    private Long count;
}
