package com.atguigu.wechat.service.impl;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.atguigu.common.result.Result;
import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.atguigu.wechat.mapper.MenuMapper;
import com.atguigu.wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-04
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private WxMpService wxMpService;

    //获取所有菜单列表
    @Override
    public List<MenuVo> findMenuInfo() {
        List<MenuVo> list = new ArrayList<>();

        //1.查询所有菜单list集合
        List<Menu> menuList = baseMapper.selectList(null);
        System.out.println("=============所有菜单列表：" + menuList.toString());

        //2.查询所有一级菜单  parent_id = 0， 返回一级菜单list集合
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0)
                .collect(Collectors.toList());

        //3.一级菜单list集合遍历，得到每个一级菜单
        for (Menu oneMenu : oneMenuList) {
            //一级菜单Menu---MenuVo
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, oneMenuVo);

            //4.获取每个一级菜单里的所有二级菜单  id和parent_id 比较
            //一级菜单id  和  其他菜单parent_id
            List<Menu> twoMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == oneMenu.getId())
                    .collect(Collectors.toList());

            //5.把一级菜单里面所有耳机菜单获取到，封装一级菜单的children集合里面
            //涉及到Menu和MenuVo转换      List<Menu> --- List<MenuVo>
            List<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);

            //把每个封装好的一级菜单放到最终list集合
            list.add(oneMenuVo);
        }

        return list;
    }

    //同步菜单
    @Override
    public void syncMenu() {
        //1.菜单数据查询出来，封装称微信要求菜单格式
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://magrathea2.viphk.91tunnel.com" + oneMenuVo.getUrl());//9090
            } else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://magrathea2.viphk.91tunnel.com#" + twoMenuVo.getUrl());//9090
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        //2.调用工具里面的方法实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toString());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    //删除菜单
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

    }


}
