package com.atguigu.yygh.cmn.service.Impl;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    /**
     * 根据id去查询
     * @param id
     * @return
     */
    @Override
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> query  = new QueryWrapper<>();
        query.eq("parent id",id);
        List<Dict> dictList = baseMapper.selectList(query);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChil = this.isChildren(dictId);
            dict.setHasChildren(isChil);
        }
        return dictList;
    }

    /**
     * 判断id下是否有子节点
     * @param dictId  id
     * @return  true 是
     */
    private boolean isChildren(Long dictId) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent id",dictId);
        Integer integer = baseMapper.selectCount(wrapper);
        return integer >0;
    }


}
