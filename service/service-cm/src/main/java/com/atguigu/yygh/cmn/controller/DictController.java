package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
@Slf4j
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询子数据列表
     * @param id child的id
     * @return 返回结果
     */
    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@ApiParam(name = "id",value = "父级id",required = true)
                                    @PathVariable("id") Long id) {
        List<Dict> dictList = dictService.findChlidData(id);
        log.info(dictList.toString());
        return Result.ok(dictList);
    }

    @ApiOperation("导出exel文件")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @ApiOperation("导入exel文件")
    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    /**
     *
     * 有父类id的情况
     * @param parentDictCode  父类id
     * @param value    值
     * @return
     */
    @ApiOperation("获取数据字典名称")
    @GetMapping("/getName/{parentDictCode}/{value}")
    public String getName(
            @ApiParam(name = "parentDictCode", value = "上级代码", required = true)
            @PathVariable("parentDictCode") String parentDictCode,
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value
    ) {
        return dictService.getNameByParentDictCodeAndValue(parentDictCode, value);
    }

    /**
     * 没有父类id的情况
     * @param value 值
     * @return
     */
    @ApiOperation("获取数据字典名称")
    @GetMapping("/getName/{value}")
    public String getName(
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value
    ) {
        return dictService.getNameByParentDictCodeAndValue("",value);
    }

    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @ApiParam(name = "dictCode",value = "节点编码",required = true)
                 @PathVariable("dictCode")   String dictCode
    ){

        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }
}
