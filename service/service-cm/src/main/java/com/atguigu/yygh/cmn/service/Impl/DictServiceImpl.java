package com.atguigu.yygh.cmn.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.cmn.Listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    private DictMapper dictMapper;

    /**
     * 根据id去查询
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> query = new QueryWrapper<>();
        query.eq("parent_id", id);  // 子节点
        List<Dict> dictList = baseMapper.selectList(query);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    /**
     * 判断id下是否有子节点
     * 因为数据库的设计是这样的
     *
     * @param dictId id
     * @return true 是
     */
    private boolean isChildren(Long dictId) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", dictId);
        Integer integer = baseMapper.selectCount(wrapper);
        // 如果大于0，这设置成有子节点d
        return integer > 0;
    }

    /**
     * @param response 下载的内容
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");  // 设置内容类型
            response.setCharacterEncoding("utf-8");

            // 防止url乱码
            String fileName = URLEncoder.encode("数据字典", "utf-8");
            // 设置文件头
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            List<Dict> dictList = dictMapper.selectList(null);
            List<DictEeVo> dictVo = new ArrayList<>(dictList.size());

            // dict 转换成 dictVO
            for (Dict dict : dictList) {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo, DictEeVo.class);
                dictVo.add(dictEeVo);
            }

            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据字典
     * 读取文件要有监听器进行一行一行去读取
     *
     * @param file 导入的文件
     */
    @Override
    @CacheEvict(value = "dict", allEntries = true)  // 把dict 下的缓存清空
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        // 如果value可以唯一定位数据字典，parentDictCode可以传空，列如省市区的value值能够唯一确定
        if (StringUtils.isEmpty(parentDictCode)) {
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if (dict != null) {
                return dict.getName();
            } else {
                Dict parentDict = this.getByDictsCode(parentDictCode);
                if (parentDict == null) {
                    return "";
                }
                Dict dict1 = dictMapper.selectOne(new QueryWrapper<Dict>()
                        .eq("parent_id", parentDict.getId())
                        .eq("value", value));
                if (dict1 != null) {
                    return dict1.getName();
                }
            }
        }
        return "";
    }

    /**
     * 根据dictCode获取下级节点
     * @param dictCode
     * @return
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict codeDict = this.getByDictsCode(dictCode);
        if (codeDict == null){
            return null;
        }
        return this.findChlidData(codeDict.getId());
    }


    private Dict getByDictsCode(String parentDictCode) {
        QueryWrapper<Dict> queryWrapper =  new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentDictCode);
        return dictMapper.selectOne(queryWrapper);
    }


}
