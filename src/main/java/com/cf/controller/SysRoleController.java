package com.cf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.cf.entity.SysRoleEntity;
import com.cf.entity.SysUserEntity;
import com.cf.service.SysRoleMenuService;
import com.cf.service.SysRoleService;
import com.cf.service.SysUserService;
import com.cf.utils.Constant;
import com.cf.utils.PageUtils;
import com.cf.utils.Query;
import com.cf.utils.R;
import com.cf.utils.annotation.SysLog;
import com.cf.utils.validator.ValidatorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 * 
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//如果不是超级管理员，则只查询自己创建的角色列表
		if(getCurrentUser().getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getCurrentUser().getUserId() );
		}
		//查询列表数据
		Query query = new Query(params);
		List<SysRoleEntity> list = sysRoleService.queryList(query);
		int total = sysRoleService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	public UserDetails getUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
			    .getAuthentication()
			    .getPrincipal();
		return userDetails;
	}
	
	public SysUserEntity getCurrentUser() {
		return sysUserService.queryByUserName(getUser().getUsername());
	}
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/select")
	public R select(){
		Map<String, Object> map = new HashMap<>();
		//如果不是超级管理员，则只查询自己所拥有的角色列表
		if(getCurrentUser().getUserId()  != Constant.SUPER_ADMIN){
			map.put("createUserId", getCurrentUser().getUserId() );
		}
		List<SysRoleEntity> list = sysRoleService.queryList(map);
		return R.ok().put("list", list);
	}
	
	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	public R info(@PathVariable("roleId") Long roleId){
		SysRoleEntity role = sysRoleService.queryObject(roleId);
		//查询角色对应的菜单
		List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);
		return R.ok().put("role", role);
	}
	
	/**
	 * 保存角色
	 */
	@SysLog("保存角色")
	@RequestMapping("/save")
	//@RequiresPermissions("sys:role:save")
	public R save(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		role.setCreateUserId(getCurrentUser().getUserId() );
		sysRoleService.save(role);
		
		return R.ok();
	}
	
	/**
	 * 修改角色
	 */
	@SysLog("修改角色")
	@RequestMapping("/update")
	public R update(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		role.setCreateUserId(getCurrentUser().getUserId() );
		sysRoleService.update(role);
		
		return R.ok();
	}
	
	/**
	 * 删除角色
	 */
	@SysLog("删除角色")
	@RequestMapping("/delete")
	//@RequiresPermissions("sys:role:delete")
	public R delete(@RequestBody Long[] roleIds){
		sysRoleService.deleteBatch(roleIds);
		return R.ok();
	}
}
