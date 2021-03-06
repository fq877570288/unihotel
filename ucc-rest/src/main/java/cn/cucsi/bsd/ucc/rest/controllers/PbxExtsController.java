package cn.cucsi.bsd.ucc.rest.controllers;

import cn.cucsi.bsd.ucc.common.beans.PageResultBean;
import cn.cucsi.bsd.ucc.common.beans.PbxExtsCriteria;
import cn.cucsi.bsd.ucc.common.beans.ResultBean;
import cn.cucsi.bsd.ucc.common.untils.PbxReload;
import cn.cucsi.bsd.ucc.common.untils.ZooKeeperUtils;
import cn.cucsi.bsd.ucc.data.domain.PbxExtGroups;
import cn.cucsi.bsd.ucc.data.domain.PbxExts;
import cn.cucsi.bsd.ucc.service.PbxExtGroupsService;
import cn.cucsi.bsd.ucc.service.PbxExtsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjjjj on 2017-10-13.
 */
@RestController
@RequestMapping(value = "/PbxExts")
public class PbxExtsController {

    @Autowired
    private PbxExtsService PbxExtsService;
    @Autowired
    private ZooKeeperUtils zk;

    @ApiOperation(value = "根据查询条件获取分机表", notes = "根据查询条件获取分机表", httpMethod = "POST")
    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public PageResultBean<List<PbxExts>> findAll(@RequestBody PbxExtsCriteria search) {
        return new PageResultBean(this.PbxExtsService.findAll(search));
    }

    @ApiOperation(value = "根据查询条件获取分机表", notes = "根据查询条件获取分机表", httpMethod = "POST")
    @RequestMapping(value = "/findAllOfNoPage", method = RequestMethod.POST)
    public ResultBean<List<PbxExts>> findAllOfNoPage(@RequestBody PbxExtsCriteria search) {
        return new ResultBean(this.PbxExtsService.findAllOfNoPage(search));
    }

    @ApiOperation(value = "根据ExtNum和ExtId获取分机表", notes = "根据ExtNum和ExtId获取分机表", httpMethod = "POST")
    @RequestMapping(value = "/findBySeach", method = RequestMethod.POST)
    public ResultBean<List<PbxExts>> findBySeach(@RequestBody PbxExtsCriteria search) {
        return new ResultBean(this.PbxExtsService.findAllBySearch(search));
    }

    @ApiOperation(value = "查询所有没有分配的分机号码", notes = "查询所有没有分配的分机号码", httpMethod = "POST")
    @RequestMapping(value = "/findAllFree/{domainId}", method = RequestMethod.POST)
    public List<PbxExts> findAllFree(@RequestBody String domainId) {
        return this.PbxExtsService.findAllFreeExts(domainId);
    }

    @RequestMapping(value = "/findAllFreeByDomainId", method = RequestMethod.POST)
    public ResultBean<List<PbxExts>> findAllFreeByDomainId(@RequestBody PbxExts search) {
        return new ResultBean<>(this.PbxExtsService.findAllFreeExtsByDomainId(search));
    }

    @ApiOperation(value = "根据extId查询PbxExts", notes = "根据extId查询PbxExts")
    @RequestMapping(value = "/{extId}", method = RequestMethod.POST)
    public ResultBean<PbxExts> findOne(@RequestBody String extId) {
        return new ResultBean<>(this.PbxExtsService.findOne(extId));
    }

    @ApiOperation(value = "根据extId删除PbxExts", notes = "根据extId删除PbxExts")
    @RequestMapping(value = "/{extId}", method = RequestMethod.DELETE)
    public ResultBean<Boolean> delete(@PathVariable String extId) {
        boolean result = this.PbxExtsService.delete(extId);
        if(result){
            PbxExts pbxExts = new PbxExts();
            pbxExts.setExtId(extId);
            PbxReload.reloadExtAsync(pbxExts, "delete", zk);
        }
        return new ResultBean<>(result);
    }

    @ApiOperation(value = "批量新增PbxExts", notes = "批量新增PbxExts")
    @RequestMapping(value = "/createMany", method = RequestMethod.POST)
    public ResultBean<Boolean> createMany(@RequestBody PbxExts PbxExts, String extGroupExts, Integer extNumStart
            , Integer extNumCount, String cover) {
        boolean result = this.PbxExtsService.saveMany(PbxExts, extGroupExts, extNumStart, extNumCount, cover) != null;
        return new ResultBean<>(result);
    }

    @ApiOperation(value = "新增/修改PbxExts", notes = "新增/修改PbxExts")
    @RequestMapping(value = "/createOne", method = RequestMethod.POST)
    public ResultBean<Boolean> createOne(@RequestBody PbxExts PbxExts, String extGroupExts) {
        boolean result = this.PbxExtsService.saveOne(PbxExts, PbxExts.getExtGroupExtIds()) != null;
        if(result){
            PbxReload.reloadExtAsync(PbxExts, "create", zk);
        }
        return new ResultBean<>(result);
    }

    @ApiOperation(value = "修改PbxExts", notes = "修改PbxExts")
    @RequestMapping(value = "/{extId}", method = RequestMethod.PUT)
    public ResultBean<Boolean> save(@RequestBody PbxExts PbxExts, @PathVariable String extId, String extGroupExts) {
        boolean result = this.PbxExtsService.saveOne(PbxExts, extGroupExts) != null;
        if(result){
            PbxReload.reloadExtAsync(PbxExts, "update", zk);
        }
        return new ResultBean<>(result);
    }

}
