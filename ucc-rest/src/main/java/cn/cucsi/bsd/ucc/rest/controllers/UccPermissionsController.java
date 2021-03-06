package cn.cucsi.bsd.ucc.rest.controllers;

import cn.cucsi.bsd.ucc.common.beans.PageResultBean;
import cn.cucsi.bsd.ucc.common.beans.ResultBean;
import cn.cucsi.bsd.ucc.common.beans.ResultBeanWithPermission;
import cn.cucsi.bsd.ucc.common.beans.UccPermissionsCriteria;
import cn.cucsi.bsd.ucc.data.domain.UccPermissions;
import cn.cucsi.bsd.ucc.data.domain.UccRoles;
import cn.cucsi.bsd.ucc.data.domain.UccUsers;
import cn.cucsi.bsd.ucc.service.UccPermissionsService;
import cn.cucsi.bsd.ucc.service.UccUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by mk on 2017/10/13.
 */
@RestController
@RequestMapping(value = "/uccPermissions")
public class UccPermissionsController {
    @Autowired
    UccPermissionsService uccPermissionsService;
    @Autowired
    private UccUserService uccUserService;
  
   

 @ApiOperation(value="根据查询条件获取角色列表", notes="根据查询条件获取角色列表", httpMethod = "GET")
    @RequestMapping(value = "/findWhole", method= RequestMethod.GET)
    public PageResultBean<List<UccPermissions>> findWhole(@ModelAttribute UccPermissionsCriteria search){
        return new PageResultBean(this.uccPermissionsService.findWhole(search));
    }
    
    @ApiOperation(value="根据查询条件获取权限列表", notes="根据查询条件获取权限列表", httpMethod = "GET")
    @RequestMapping(value = "/findAll", method= RequestMethod.GET)
    public ResultBeanWithPermission<List<UccPermissions>> findAll(@ModelAttribute UccPermissionsCriteria search){
        //首先要某用户id下的role信息
        UccUsers user = uccUserService.findOne(search.getUserId());
        Collection<UccRoles> roles  =  user.getRoles();

        List<String> permissionIds = new ArrayList<String>();
        List<String> roleNames = new ArrayList<String>();

        if(roles!=null && roles.size()>0){
            for (UccRoles uccRoles:roles) {
                roleNames.add(uccRoles.getRoleName());
                Collection<UccPermissions> uccPermissions = uccRoles.getUccPermissions();
                if(uccPermissions!=null && uccPermissions.size()>0){
                    for (UccPermissions Permissions:uccPermissions
                         ) {
                        permissionIds.add(Permissions.getPermissionId());
                    }
                }
            }
        }
        if(permissionIds.size()>0){
            search.setPermissionIds(permissionIds);
        }else{
            permissionIds.add("");
            search.setPermissionIds(permissionIds);
        }

        ResultBeanWithPermission resultBean =  new ResultBeanWithPermission(this.uccPermissionsService.findAll(search));
        resultBean.setRoleNames(roleNames);
        return resultBean;  
    }
    @ApiOperation(value = "根据permissionId查询UccPermissions", notes = "根据permissionId查询UccPermissions")
    @RequestMapping(value = "/{permissionId}", method= RequestMethod.GET)
    public ResultBean<UccPermissions> findOne(@PathVariable String permissionId){
        return new ResultBean<>(this.uccPermissionsService.findOne(permissionId));
    }
    @ApiOperation(value = "根据permissionId删除uccPermissions", notes = "根据permissionId删除uccPermissions")
    @RequestMapping(value = "/{permissionId}", method= RequestMethod.DELETE)
    public ResultBean<Boolean> delete(@PathVariable String permissionId){
        return new ResultBean<>(this.uccPermissionsService.delete(permissionId));
    }

    @ApiOperation(value = "创建UccPermissions", notes = "创建UccPermissions")
    @RequestMapping(value = "", method =  RequestMethod.POST)
    public ResultBean<Boolean> create(@RequestBody UccPermissions uccPermissions) {
        boolean result=this.uccPermissionsService.save(uccPermissions) != null;
        return new ResultBean<>(result);
    }

    @ApiOperation(value = "修改UccPermissions", notes = "修改UccPermissions")
    @RequestMapping(value = "/{permissionId}", method = RequestMethod.PUT)
    public ResultBean<Boolean> save(@PathVariable String permissionId,@RequestBody UccPermissions uccPermissions){
        boolean result=this.uccPermissionsService.save(uccPermissions) != null;
        return new ResultBean<>(result);
    }

    @ApiOperation(value="根据查询获取权限树", notes="根据查询获取权限树", httpMethod = "POST")
    @RequestMapping(value = "/tree", method= RequestMethod.POST)
    public PageResultBean<List<UccPermissions>> tree(@RequestBody UccPermissionsCriteria search){
        PageResultBean<List<UccPermissions>> uccPermissionsList = new PageResultBean(this.uccPermissionsService.findAllTree(search));
        List<UccPermissions> list = uccPermissionsList.getData();
        List<UccPermissions> pIdList = new ArrayList<>();
        if(list!=null&&list.size()!=0) {
            //父ID
            for (UccPermissions uccPermissions : list) {
                if (uccPermissions.getMpid() == null) {
                    pIdList.add(uccPermissions);
                }
            }
            //往父ID中添加子节点
            if(pIdList.size()!=0){
                for(int i=0;i<pIdList.size();i++){
                    queryChildren(pIdList.get(i),list);
                }
            }
            uccPermissionsList.setData(pIdList);
            return uccPermissionsList;
        }
        return new PageResultBean(this.uccPermissionsService.findAllTree(search));
    }

    public void queryChildren(UccPermissions uccPermissions, List<UccPermissions> list){
        List<UccPermissions> Childrens = new ArrayList<UccPermissions>();
        if(list.size()!=0){
            for(int i=0;i<list.size();i++){
                if(uccPermissions!=null&&uccPermissions.getPermissionId().equals(list.get(i).getMpid())){
                    Childrens.add(list.get(i));
                }
            }
            if(Childrens.size()!=0){
                uccPermissions.setUccPermissions(Childrens);
                for(int a = 0;a<Childrens.size();a++){
                    queryChildren(Childrens.get(a),list);
                }
            }
        }
    }
}
