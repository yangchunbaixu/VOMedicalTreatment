package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {

    /*
     * 根据数据id查询子数据列表
     */
    public List<Dict> findChlidData(Long id);

    /**
     * 导出接口
     *
     * @param response 传入的内容
     */
    void exportData(HttpServletResponse response);

    void importDictData(MultipartFile file);
}
