package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

/**
 * GoodsServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2018-11-18 21:04:19
 */
@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService {

    /**
     * 注入数据访问层代理对象
     */
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;


    /**
     * 添加方法
     */
    public void save(Goods goods) {
        try {
            /** #### 往tb_goods表插入数据  ##### */
            // 设置未申核状态
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);

            /** #### 往tb_goods_desc表插入数据  ##### */
            // 为商品描述对象设置主键id
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());

            // 判断是否启用规格
            if ("1".equals(goods.getIsEnableSpec())) {
                /** #### 往tb_item表插入数据  ##### */
                for (Item item : goods.getItems()) {
                    // item: {"spec":{"网络":"移动3G","机身内存":"128G"},"price":"1000",
                    //        "num":"200","status":1,"isDefault":1}
                    // Apple iPhone 8 Plus (A1864) 联通4G 64G

                    StringBuilder title = new StringBuilder(goods.getGoodsName());
                    // {"网络":"移动3G","机身内存":"128G"} --> Map
                    Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                    for (String value : specMap.values()) {
                        title.append(" " + value);
                    }
                    // SKU商品的标题 SPU的名称 + N个规格选项名称
                    item.setTitle(title.toString());

                    // 设置SKU商品的其它信息
                    setItemInfo(goods, item);

                    // 往tb_item表添加数据
                    itemMapper.insertSelective(item);
                }
            } else {// 不启用规格
                // SPU == SKU (往SKU表插入一条数据)
                // {"spec":{"网络":"移动3G","机身内存":"128G"},"price":"1000",
                //        "num":"200","status":1,"isDefault":1}
                /** 创建SKU具体商品对象 */
                Item item = new Item();
                /** 设置SKU商品的价格 */
                item.setPrice(goods.getPrice());
                /** 设置SKU商品库存数据 */
                item.setNum(9999);
                /** 设置SKU商品启用状态 */
                item.setStatus("1");
                /** 设置是否默认*/
                item.setIsDefault("1");
                /** 设置规格选项 */
                item.setSpec("{}");
                /** 设置SKU商品的标题 */
                item.setTitle(goods.getGoodsName());

                // 设置SKU商品的其它信息
                setItemInfo(goods, item);

                // 往tb_item表循环添加数据
                itemMapper.insertSelective(item);

            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    // 设置SKU商品的其它信息
    private void setItemInfo(Goods goods, Item item) {
        //获取全部的图片
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            //SKU商品的图片
            item.setImage(imageList.get(0).get("url").toString());
        }
        //// SKU商品的三级分类
        item.setCategoryid(goods.getCategory3Id());

        // SKU商品的创建时间
        item.setCreateTime(new Date());
        // SKU商品的修改时间
        item.setUpdateTime(item.getCreateTime());
        // SKU商品关联的SPU的id
        item.setGoodsId(goods.getId());
        // SKU商品的商家id
        item.setSellerId(goods.getSellerId());

        /** ####### 搜索系统需要用到 ########## */
        // SKU商品的三级分类名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat != null ? itemCat.getName() : "");
        // SKU商品的品牌名称
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand != null ? brand.getName() : "");
        // SKU商品的店铺名称
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller != null ? seller.getNickName() : "");
    }

    /**
     * 修改方法
     */
    public void update(Goods goods) {
        try {
            goodsMapper.updateByPrimaryKeySelective(goods);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            goodsMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Goods.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            goodsMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Goods findOne(Serializable id) {
        try {
            return goodsMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Goods> findAll() {
        try {
            return goodsMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(Goods goods, int page, int rows) {
        try {
            PageInfo<Map<String, Object>> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            goodsMapper.findAll(goods);
                        }
                    });
            // 获取分页结果集
            List<Map<String, Object>> goodsList = pageInfo.getList();
            for (Map<String, Object> map : goodsList) {
                // 获取三级分类的id
                Long category3Id = (Long) map.get("category3Id");
                if (category3Id != null && category3Id > 0) {
                    ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                    map.put("category1Name", itemCat1.getName());
                    ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                    map.put("category2Name", itemCat2.getName());
                    ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                    map.put("category3Name", itemCat3.getName());
                }
            }
            return new PageResult(pageInfo.getTotal(), goodsList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 修改状态码
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(String columnName, Long[] ids, String status) {
        try {
            goodsMapper.updateStatus(columnName, ids, status);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 根据主键id获取商品信息
     */
    public Map<String, Object> getGoods(Long goodsId) {
        try {
            Map<String, Object> dataModel = new HashMap<>();

            //查询tb_goods表
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            //查询tb_goods_desc表
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);

            //查询商品分类
            if (goods.getCategory3Id() != null) {
                //查询一级名称
                String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                dataModel.put("itemCat1", itemCat1);
                //查询二级名称
                String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                dataModel.put("itemCat2", itemCat2);
                //查询三级名称
                String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
                dataModel.put("itemCat3", itemCat3);
            }
            //查询tb_item表
            //创建示范对象
            Example example = new Example(Item.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            //设置状态码为1
            criteria.andEqualTo("status", "1");
            //添加查询条件
            criteria.andEqualTo("goodsId",goodsId);
            /** 按是否默认降序(保证第一个为默认) */
            example.orderBy("isDefault").desc();
            /** 根据条件查询SKU商品数据 */
            List<Item> itemList = itemMapper.selectByExample(example);
            //把itemList转化成json数组的字符串[{},{}]
            dataModel.put("itemList",JSON.toJSONString(itemList));

            return dataModel;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /** 查询上架的SKU商品数据 */
    @Override
    public List<Item> findItemByGoodsId(Long[] ids){
        try{
            /** 创建示范对象 */
            Example example = new Example(Item.class);
            /** 创建查询条件对象 */
            Example.Criteria criteria = example.createCriteria();
            /** 添加in查询条件 */
            criteria.andIn("goodsId", Arrays.asList(ids));
            /** 查询数据 */
            return itemMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}