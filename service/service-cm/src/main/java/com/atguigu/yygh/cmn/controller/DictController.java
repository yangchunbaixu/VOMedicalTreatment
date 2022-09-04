package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "数据字典接口")
//@Api(tags = "医疗管理")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询子数据列表
     * @param id  child的id
     * @return 返回结果
     */
    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData (@PathVariable Long id){
        List<Dict> dictList = dictService.findChlidData(id);
        return Result.ok(dictList);
    }


}
