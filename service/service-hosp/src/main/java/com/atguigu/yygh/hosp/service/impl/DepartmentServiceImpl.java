package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paraMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paraMap), Department.class);
        Department targetDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if (null != targetDepartment) {
            BeanUtils.copyProperties(department, targetDepartment, Department.class);
            departmentRepository.save(targetDepartment);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

    /**
     * 查询科室
     * 分页查询
     *
     * @param page              当前页码
     * @param limit             每页记录数
     * @param departmentQueryVo 查询条件
     * @return
     */
    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建匹配器
        ExampleMatcher matching = ExampleMatcher.matching()// 构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)  //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); // //改变默认大小写忽略方式：忽略大小写
        Example<Department> example = Example.of(department, matching);
        Page<Department> pages = departmentRepository.findAll(example, pageable);

        return pages;
    }

    /**
     * 删除科室
     * @param depcode 科室id
     * @param hoscode 医院id
     */
    @Override
    public void remove(String depcode, String hoscode) {
        // 获取信息,数据存在MongoDB
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号获取所有科室信息
     * @param hoscode
     * @return
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        List<DepartmentVo> result = new ArrayList<>();
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);

        List<Department> departmentRepositoryAll = departmentRepository.findAll(example);

        Map<String, List<Department>> collect = departmentRepositoryAll.stream().collect(Collectors.groupingBy(Department::getBigcode));

        for (Map.Entry<String,List<Department>> entry : collect.entrySet()){
            // 大科室编号
            String bigCode = entry.getKey();
            List<Department> deparment1List = entry.getValue();
            // 大科室编号要对应全局数据
            List<Department> bigCodeValue = entry.getValue();
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(bigCodeValue.get(0).getBigname());

            // 封装小科室
            List<DepartmentVo> childern = new ArrayList<>();
            for (Department depart : deparment1List) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(depart.getDepcode());
                departmentVo1.setDepname(depart.getDepname());
                childern.add(departmentVo1);
            }

            // 把小科室放到大科室下面
            departmentVo.setChildren(childern);
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if (department != null){
            return department.getDepname();
        }
        return null;
    }
}
