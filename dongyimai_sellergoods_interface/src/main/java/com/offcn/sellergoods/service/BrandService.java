package com.offcn.sellergoods.service;

import com.offcn.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    //public PageResult findPage(int page ,int rows);

    public void add(TbBrand brand);

    public void update(TbBrand brand);

    public TbBrand findOne(Long id);

    public void delete(Long[] ids);

    public PageResult findPage(TbBrand brand,int page ,int rows);

    List<Map> selectOptionList();

    public PageResult findPage(int page ,int rows);
}
