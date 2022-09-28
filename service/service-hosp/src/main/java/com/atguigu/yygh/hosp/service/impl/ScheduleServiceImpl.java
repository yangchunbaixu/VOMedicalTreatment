package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 导入数据
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        Schedule targetSchedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (targetSchedule != null){
            BeanUtils.copyProperties(schedule,targetSchedule,Schedule.class);
            scheduleRepository.save(targetSchedule);
        }else {
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        Sort createTime = Sort.by(Sort.Direction.DESC, "createTime");
        log.info("createTime : "+ createTime);
        PageRequest pageRequest = PageRequest.of(page - 1, limit, createTime);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    .withIgnoreCase(true);

        Example<Schedule> example = Example.of(schedule, exampleMatcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageRequest);

        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, depcode);
        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }


}
