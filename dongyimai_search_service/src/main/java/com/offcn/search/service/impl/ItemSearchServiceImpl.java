package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        Map<String,Object> map=new HashMap<String, Object>();
         map.putAll(searchList(searchMap));
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        String categoryName =(String) searchMap.get("category");
        if (!"".equals(categoryName)){
            map.putAll(searchBrandAndSpecList(categoryName));
        }else
        if(categoryList.size()>0){
            map.putAll(searchBrandAndSpecList((String)categoryList.get(0)));
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for (TbItem item : list) {
            System.out.println(item.getTitle());
            Map<String,?> specMap = JSON.parseObject(item.getSpec(), Map.class);
            Map map=new HashMap();
            for (String key : specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    private Map searchList(Map searchMap){
        Map<String,Object> map=new HashMap<String, Object>();
        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、设定需要高亮处理字段
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //3、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //4、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //5、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);

        //6、设定查询条件 根据关键字查询
        //6.1创建查询条件对象
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //6.2根据分类查询
        if (!"".equals(searchMap.get("category"))){
            Criteria filterCriteria =new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //6.3根据品牌查询
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery =new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //6.4根据规格查询
        if (searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria=new Criteria("item_spec_"+ Pinyin.toPinyin(key,"").toLowerCase()).is(specMap.get("spec"));
                FilterQuery filterQuery =new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //6.5根据价格查询
        if (!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!"0".equals(price[0])){
                Criteria filterCriteria=new Criteria("item_price").greaterThan(price[0]);
                FilterQuery filterQuery =new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){
                Criteria filterCriteria=new Criteria("item_price").lessThan(price[1]);
                FilterQuery filterQuery =new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6.6分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo==null){
            pageNo=1;
        }
        if (pageSize==null){
            pageSize=20;
        }
        query.setOffset(pageNo);
        query.setRows(pageSize);

        //6.6排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (!"".equals(sortValue)&&sortValue!=null){
            if (sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
        }
        //7、发出带高亮数据查询请求
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //8、获取高亮集合入口
        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        //9、遍历高亮集合
        for(HighlightEntry<TbItem> highlightEntry:highlightEntryList){
            //获取基本数据对象
            TbItem tbItem = highlightEntry.getEntity();
            if(highlightEntry.getHighlights().size()>0&&highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
                List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
                //高亮结果集合
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段对应的高亮结果，设置到商品标题
                tbItem.setTitle(snipplets.get(0));
            }

        }
        //把带高亮数据集合存放map
        map.put("rows",page.getContent());
        //分页数据带回
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }

    private  List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        Long typeId=(Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId!=null){
            List brandList =(List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList =(List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }
        return map;
    }
}
