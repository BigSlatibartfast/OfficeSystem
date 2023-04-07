package com.atguigu.wechat.service;


import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-04
 */
public interface MenuService extends IService<Menu> {

    //获取所有菜单列表
    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
