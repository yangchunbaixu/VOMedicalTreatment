package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private HospitalSetMapper hospitalSetMapper;
    @Autowired
    private DictFeignClient dictFeignClient;
    /**
     * 上传医院设置
     *
     * @param paramMap 医院信息
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // 1. 先把map转换成字符串
        // 2. 再把字符串转换成对象
        //Map转换为Hospital对象时，预约规则bookingRule为一个对象属性，
        // rule为一个数组属性，因此在转换时我们要重新对应的set方法，不然转换不会成功
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Hospital.class);

        // 判断是否存在
        Hospital targetHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        // 有，更新
        if (targetHospital != null) {
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
        //无，新增
        else {
            hospital.setStatus(0);
            hospital.setUpdateTime(new Date());
            hospital.setCreateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 获取key
     * 根据key查询医院签名，数据库
     *
     * @param hoscode
     * @return
     */
    @Override
    public String getSignKey(String hoscode) {

        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);

        if (hospitalSet == null) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        if (hospitalSet.getStatus().intValue() == 0) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_LOCK);
        }
        return hospitalSet.getSignKey();
    }

    /**
     * 查询医院接口
     *
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param limit
     * @param hospitalSetQueryVo
     * @return
     */
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalSetQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalSetQueryVo, hospital);

            //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return pages;

    }

    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0  || status.intValue() == 1){

            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> show(String id) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        //医院基本信息（包含医院等级）
        result.put("hospital",hospital);
        //单独处理更直观
        result.put("bookingRule",hospital.getBookingRule());
        // 不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    // 获取查询list集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        String hostype = dictFeignClient.getName("Hostype", hospital.getHostype());
        // 查询省，地区
        String provinceCode = dictFeignClient.getName(hospital.getProvinceCode());
        String CityCode = dictFeignClient.getName(hospital.getCityCode());
        String districtCode = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceCode+CityCode+districtCode);
        hospital.getParam().put("hostypeString",hostype);
        return hospital;
    }

}
