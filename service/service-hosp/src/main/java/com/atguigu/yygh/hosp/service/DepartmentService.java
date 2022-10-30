package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    /**
     * 上传科室
     *
     * @param paraMap
     */
    void save(Map<String, Object> paraMap);

    /**
     * 查询科室
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param departmentQueryVo 查询条件
     * @return
     */
    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     * @param depcode 科室id
     * @param hoscode 医院id
     */
    void remove(String depcode,String hoscode);

    /**
     * 根据医院编号，获取所有科室信息
     * @param hoscode
     * @return
     */
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据科室编号，和医院编号，查询科室名称
    String getDepName(String hoscode, String depcode);

}
