package com.atguigu.yygh.hosp.api;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 所有参数递交的方式必须为POST，参数值编码为gb2312。
 * 平台对外开发的接口都写在该Controller类
 */
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
@Slf4j
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    ///////////////  保存操作
    /**
     * 上传医院接口
     * @param request
     * @return
     */
    @ApiOperation("上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap()); // 获取传递过来的医院的参数

        // 检验参数
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalService.getSignKey(hoscode);
        String logoDataString = (String) paramMap.get("logoData");
        // 检验参数
        flag(hoscode, signKey, paramMap, logoDataString);
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation("上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalService.getSignKey(hoscode);
        // 检验
        flag(hoscode, signKey, paramMap);
        departmentService.save(paramMap);

        return Result.ok();
    }

    @ApiOperation("上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode =(String) paramMap.get("hoscode");
        String signKey = hospitalService.getSignKey(hoscode);
        flag(hoscode,signKey,paramMap);
        scheduleService.save(paramMap);
        return Result.ok();
    }

    /////////////  获取信息

    @ApiOperation("获取医院信息")
    @PostMapping("hospital/show")
    public Result show(HttpServletRequest request) {
        Map<String, Object> paraMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数检验
        String hoscode = (String) paraMap.get("hoscode");

        // 签名检验
        String signKey = hospitalService.getSignKey(hoscode);
        flag(hoscode, signKey, paraMap);
        return Result.ok(hospitalService.getByHoscode((String) paraMap.get("hoscode")));
    }

    /**
     * 获取科室分页
     * @param request
     * @return
     */
    @ApiOperation("获取科室分页数据")
    @PostMapping("department/list")
    public Result department(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        String singKey = hospitalService.getSignKey(hoscode);

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));

        flag(hoscode,singKey,paramMap);
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> departments = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(departments);
    }

    @ApiOperation("获取科室信息")
    @PostMapping("schedule/list")
    public Result schedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        String singKey = hospitalService.getSignKey(hoscode);

        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));
        flag(hoscode,singKey,paramMap);
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> schedules = scheduleService.selectPage(page,limit,scheduleQueryVo);
        return Result.ok(schedules);
    }

    ////////////////// 删除操作
    /**
     * 删除科室
     * @param request
     * @return
     */
    @ApiOperation("删除科室")
    @PostMapping("department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode =(String) paramMap.get("hoscode");
        String depcode =(String) paramMap.get("depcode");
        String signKey = hospitalService.getSignKey(hoscode);
        flag(hoscode,signKey,paramMap);

        departmentService.remove(depcode,hoscode);
        return Result.ok();
    }

    @ApiOperation("删除排队信息")
    @PostMapping("schedule/remove")
    public Result scheduleRemove(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode =(String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");

        String signKey = hospitalService.getSignKey(hoscode);
        flag(hoscode,signKey,paramMap);
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }

    /**
     * 数据校验
     * @param hoscode  医院代码
     * @param signKey  签名
     * @param paramMap 参数
     * @return
     */
    private boolean flag(String hoscode, String signKey, Map<String, Object> paramMap) {
        if (StringUtils.isEmpty(hoscode)) {
            // 不同
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        if (!HttpRequestHelper.isSignEquals(paramMap, signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        return true;
    }

    /**
     * 数据校验
     * @param hoscode        医院代码
     * @param signKey        签名
     * @param paramMap       参数
     * @param logoDataString 格式转换
     * @return
     */
    private boolean flag(String hoscode, String signKey, Map<String, Object> paramMap, String logoDataString) {
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        if (!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }
        if (!HttpRequestHelper.isSignEquals(paramMap, signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        return true;
    }

}
