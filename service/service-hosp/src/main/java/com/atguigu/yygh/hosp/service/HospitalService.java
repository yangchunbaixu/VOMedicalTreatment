package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.*;

import java.util.Map;

public interface HospitalService {
    /**
     * 上传医院信息
     * @param paramMap 医院信息
     */
    void save(Map<String,Object> paramMap);

    /**
     * 获取签名key
     * @param hoscode
     * @return
     */
    String getSignKey(String hoscode);

    /**
     * 查询医院
     * @param hoscode
     * @return
     */
    Hospital getByHoscode(String hoscode);


    /**
     * 分页数据
     */
    Page<Hospital> selectPage(Integer page , Integer limit, HospitalQueryVo hospitalSetQueryVo);


    /**
     * 更新上线状态
     */
    void updateStatus(String id, Integer status);

    /**
     * 医院详细
     * @param id
     * @return
     */
    Map<String , Object> show(String id);

    /**
     * 获取医院名称
     * @param hoscode
     * @return
     */
    String getHospName(String hoscode);
}
