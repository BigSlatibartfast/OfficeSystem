package com.atguigu.wechat.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.atguigu.wechat.service.MenuService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-04
 */
@RestController
@RequestMapping("/admin/wechat/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    //获取所有菜单列表
    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo(){
        List<MenuVo> menuList = menuService.findMenuInfo();
        return Result.ok(menuList);
    }

    //同步菜单
    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu() {
        menuService.syncMenu();
        return Result.ok();
    }

    //删除同步菜单
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() {
        menuService.removeMenu();
        return Result.ok();
    }


}

