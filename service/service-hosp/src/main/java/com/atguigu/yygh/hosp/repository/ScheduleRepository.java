package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    /**
     * 获取depcode
     * @param hoscode 医院代码
     * @param depcode 科室代码
     * @return
     */

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String depcode);
}
