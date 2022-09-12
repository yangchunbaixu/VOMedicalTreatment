package com.atguigu.yygh.hosp.api;

import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.hosp.service.HospitalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 上传医院接口
     * @param request
     * @return
     */
    @ApiOperation("上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 检验参数
        String hoscode = (String) paramMap.get("hoscode");
        if(StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String) paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)){
            String logoData = logoDataString.replaceAll(" ","+");
            paramMap.put("logoData",logoData);
        }
        // 签名检验
        if(!HttpRequestHelper.isSignEquals(paramMap,hospitalService.getSignKey(hoscode))){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation("获取医院接口")
    @PostMapping("hospital/show")
    public Result show(HttpServletRequest request){
        Map<String, Object> paraMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数检验
        String hoscode = (String) paraMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名检验
        String signKey = hospitalService.getSignKey(hoscode);
        if(!HttpRequestHelper.isSignEquals(paraMap,signKey)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        return Result.ok(hospitalService.getByHoscode((String) paraMap.get("hoscode")));
    }

    @ApiOperation("上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){return null;
    }
}
