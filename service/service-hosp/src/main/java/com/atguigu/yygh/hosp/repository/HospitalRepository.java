package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // 交给springboot管理
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    // 判断是否存在数据
    Hospital getHospitalByHoscode(String hoscode);
    // 找到医院名称
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
