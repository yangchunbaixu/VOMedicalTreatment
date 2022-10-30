package com.atguigu.yygh.hosp.Controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin   // 允许跨域访问
@Slf4j
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("医院列表")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@ApiParam(name = "page", value = "当前页面", required = true)
                           @PathVariable Integer page,
                           @ApiParam(name = "limit", value = "每页记录数", required = true)
                           @PathVariable Integer limit,
                           @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = true)
                                   HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }


    @ApiOperation("更新线上状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@ApiParam(name = "id", value = "医院id", required = true) @PathVariable("id") String id,
                               @ApiParam(name = "status", value = "医院状态", required = true) @PathVariable("status") Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }
    @ApiOperation("获取医院详细情况")
    @GetMapping("show/{id}")
    public Result show(@ApiParam(name="id",value = "医院id",required = true)
                       @PathVariable("id") String id){
        Map<String, Object> show = hospitalService.show(id);
        return Result.ok(show);
    }

}
