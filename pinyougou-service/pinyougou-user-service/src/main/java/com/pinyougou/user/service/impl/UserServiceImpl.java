package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NTP
 * @date 2018/12/8
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 添加方法
     *
     * @param user
     */
    @Override
    public void save(User user) {
        try {
            // 创建日期
            user.setCreated(new Date());
            // 修改日期
            user.setUpdated(user.getCreated());
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     *
     * @param user
     */
    @Override
    public void update(User user) {

    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {

    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public User findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<User> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param user
     * @param page
     * @param rows
     */
    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /** 发送验证码 */
     public boolean sendCode(String phone){
         try {
             // 生成验证码(随机生成 6位数字)
             String code = UUID.randomUUID().toString().replaceAll("-","")
                     .replaceAll("[a-z]","").substring(0,6);
             System.out.println(code);
             // 调用短信接口发送短信
             //创建httpclientUtil
             HttpClientUtils httpClientUtils = new HttpClientUtils(false);
             /**
              *  phone           String  必须  待发送手机号
              signName        String  必须  短信签名-可在短信控制台中找到
              templateCode    String 必须  短信模板-可在短信控制台中找到
              templateParam   String 必须 模板中的变量替换 JSON 串,
              如模板内容为"亲爱的${name},您的验证码为${code}"时,
              此处的值为{"name" : "", "code" : ""}
              */
             //封装请求参数
             Map<String,String> params = new HashMap<>();
             /*params.put("phone",phone);
             params.put("signName",signName);
             params.put("templateCode",templateCode);
             params.put("templateParam","{'number':'"+code+"'}");*/

             params.put("phone",phone);
             params.put("signName",signName);
             params.put("templateCode",templateCode);
             params.put("templateParam","{'code':'"+code+"'}");
             //发送post请求
             String content = httpClientUtils.sendPost(smsUrl, params);
             // 把验证码存储到Redis数据库(有效时间90秒)
             //把json字符串转化成map集合
             Map<String,Object> map = JSON.parseObject(content, Map.class);
             if ((boolean)map.get("success")){
                 //把验证码存储到redis中
                 redisTemplate.boundValueOps(phone).set(code,90, TimeUnit.SECONDS);
             }
             return (boolean)map.get("success");
         }catch (Exception ex){
             throw new RuntimeException(ex);
         }
     }

    /** 检验短信验证码 */
     public boolean checkSmsCode(String phone, String smsCode){
        try {
            String code = (String) redisTemplate.boundValueOps(phone).get();
            return StringUtils.isNoneBlank(code) && smsCode.equals(code);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
