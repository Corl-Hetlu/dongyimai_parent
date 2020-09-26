package com.offcn.search.service;

import com.offcn.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);

    public void importList(List<TbItem> list);

    public void deleteByGoodsIds(List goodsIdList);
}
