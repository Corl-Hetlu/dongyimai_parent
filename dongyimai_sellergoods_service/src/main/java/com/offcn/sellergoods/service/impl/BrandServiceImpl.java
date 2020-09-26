package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /*public PageResult findPage(int page ,int rows){
        PageHelper.startPage(page,rows);
        Page<TbBrand> pageL =(Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(pageL.getTotal(),pageL.getResult());
    }*/
    public void add(TbBrand brand){
        brandMapper.insert(brand);
    }

    public void update(TbBrand brand){
        brandMapper.updateByPrimaryKey(brand);
    }

    public TbBrand findOne(Long id){
        return brandMapper.selectByPrimaryKey(id);
    }

    public void delete(Long[] ids){
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    public PageResult findPage(TbBrand brand,int page ,int rows){
        PageHelper.startPage(page,rows);
        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand!=null){
            if (brand.getName()!=null&&brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> pageL =(Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(pageL.getTotal(),pageL.getResult());
    }

    public  List<Map> selectOptionList(){
        return brandMapper.selectOptionList();
    }

    public PageResult findPage(int page ,int rows) {
        PageHelper.startPage(page,rows);
        Page<TbBrand> pageL =(Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(pageL.getTotal(),pageL.getResult());
    }
}
