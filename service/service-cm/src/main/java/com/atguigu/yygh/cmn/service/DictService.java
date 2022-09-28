package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 字典接口服务
 */
public interface DictService extends IService<Dict> {

    /*
     * 根据数据id查询子数据列表
     */
    public List<Dict> findChlidData(Long id);

    /**
     * 导出接口
     * @param response 传入的内容
     */
    void exportData(HttpServletResponse response);

    /**
     * 导入字典数据
     * @param file
     */
    void importDictData(MultipartFile file);

    /**
     * 根据上级编码(id)与值(value)获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String parentDictCode,String value);

    /**
     * 根据dictCode获取下级节点
     * @param dictCode
     * @return
     */
    List<Dict> findByDictCode(String dictCode);
}
