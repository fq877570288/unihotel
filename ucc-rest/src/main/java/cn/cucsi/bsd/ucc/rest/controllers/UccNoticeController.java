package cn.cucsi.bsd.ucc.rest.controllers;

import cn.cucsi.bsd.ucc.common.beans.*;
import cn.cucsi.bsd.ucc.common.untils.UUIDGenerator;
import cn.cucsi.bsd.ucc.data.domain.UccNotice;
import cn.cucsi.bsd.ucc.data.domain.UccNoticeFile;
import cn.cucsi.bsd.ucc.data.domain.UccNoticeTrace;
import cn.cucsi.bsd.ucc.service.UccNoticeFileService;
import cn.cucsi.bsd.ucc.service.UccNoticeService;
import cn.cucsi.bsd.ucc.service.UccNoticeTraceService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by tianyuwei on 2017/10/16.
 */
@Api(tags={"通知公告接口"})
@RestController
@RequestMapping(value = "/uccNotice")
public class UccNoticeController {
    @Autowired
    UccNoticeService uccNoticeService;
    @Autowired
    private UccNoticeFileService uccNoticeFileService;
    @Autowired
    private UccNoticeTraceService uccNoticeTraceService;
         
    
    @ApiOperation(value="根据查询条件获取通知公告列表", notes="根据查询条件获取通知公告列表", httpMethod = "POST")
    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public PageResultBean<List<UccNotice>> findAll(@RequestBody UccNoticeCriteria search) {
                return new PageResultBean(this.uccNoticeService.findAll(search));
    }
    @ApiOperation(value = "根据noticeId查询UccNotice", notes = "根据noticeId查询UccNotice")
    @RequestMapping(value = "/{noticeId}", method= RequestMethod.GET)
    public ResultBean<UccNotice> findOne(@PathVariable String noticeId){
        return new ResultBean<>(this.uccNoticeService.findOne(noticeId));
    }

    @ApiOperation(value = "根据noticeId删除UccNotice", notes = "根据noticeId删除UccNotice")
    @RequestMapping(value = "/{noticeId}", method= RequestMethod.DELETE)
    public ResultBean<Boolean> delete(@PathVariable String noticeId){
        UccNoticeFileCriteria uccNoticeFileCriteria = new UccNoticeFileCriteria();
        uccNoticeFileCriteria.setNoticeId(noticeId);
        List<UccNoticeFile> uccNoticeFiles = uccNoticeFileService.findAllOne(uccNoticeFileCriteria);
        UccNoticeTraceCriteria uccNoticeTraceCriteria = new UccNoticeTraceCriteria();
        uccNoticeFileCriteria.setNoticeId(noticeId);
        List<UccNoticeTrace>uccNoticeTraces = uccNoticeTraceService.findAllOne(uccNoticeTraceCriteria);
        for(UccNoticeTrace uccNoticeTrace : uccNoticeTraces)
        {
            Boolean bol = uccNoticeTraceService.delete(uccNoticeTrace.getNoticeTraceId());
        }
        for(UccNoticeFile uccNoticeFile : uccNoticeFiles)
        {
            Boolean bol = uccNoticeFileService.delete(uccNoticeFile.getNoticeFileId());
        }
        return new ResultBean<>(this.uccNoticeService.delete(noticeId));
    }

    @ApiOperation(value = "创建UccNotice", notes = "创建UccNotice")
    @RequestMapping(value = "", method =  RequestMethod.POST)
    public ResultBean<Boolean> create(UccNotice uccNotice, @RequestParam("files") MultipartFile[] files) {
        
        Date dateTime = new Date();
        uccNotice.setCreatedTime(dateTime);
        UccNotice uccNoticere = this.uccNoticeService.save(uccNotice);
        if(uccNoticere != null)
        {
            for(MultipartFile file : files)
            {
               byte[] fileBox = null;
               if (file != null && file.getSize() > 0) {                   
                   UccNoticeFile uccNoticeFile = new UccNoticeFile();
                   uccNoticeFile.setNoticeId(uccNoticere.getNoticeId());
                   uccNoticeFile.setFileName(file.getOriginalFilename());
                   uccNoticeFile.setCreatedTime(dateTime);
                   uccNoticeFile.setCreatedUserId(uccNotice.getCreatedUserId());
                   uccNoticeFile.setCreatedUserName(uccNotice.getCreatedUserName());
                   uccNoticeFile.setContentType(file.getContentType());
                   try {
                       fileBox = file.getBytes();
                       this.uccNoticeFileService.save(fileBox, uccNoticeFile);
                   } catch (IOException e) {
                       e.printStackTrace();
                   
                   }
               }
            }
        }
        
        return new ResultBean<>(uccNoticere != null);
    }

    @ApiOperation(value = "修改UccNotice", notes = "修改UccNotice")
    @RequestMapping(value = "/update/{noticeId}", method =  RequestMethod.POST)
    public ResultBean<UccNotice> save(@PathVariable String noticeId, String[] delFiledId,UccNotice uccNotice, @RequestParam("files") MultipartFile[] files){
        Date dateTime = new Date();
        UccNotice uccNoticeold = uccNoticeService.findOne(noticeId);
        
        uccNoticeold.setNoticeTitle(uccNotice.getNoticeTitle());
        uccNoticeold.setNoticeType(uccNotice.getNoticeType());
        uccNoticeold.setNoticeContent(uccNotice.getNoticeContent());
        uccNoticeold.setUpdatedNickName(uccNotice.getUpdatedNickName());
        uccNoticeold.setUpdatedUserId(uccNotice.getUpdatedUserId());
        uccNoticeold.setUpdatedUserName(uccNotice.getUpdatedUserName());
        uccNoticeold.setUpdatedTime(dateTime);
        //
        //uccNotice.setUpdatedTime(dateTime);
        if(delFiledId != null)
        {
            for(String noticeFileId:delFiledId)
            {
                uccNoticeFileService.delete(noticeFileId);
            }
        }
        if(files != null)
        {
            for(MultipartFile file : files)
            {
               byte[] fileBox = null;
               if (file != null && file.getSize() > 0) {                   
                   UccNoticeFile uccNoticeFile = new UccNoticeFile();
                   uccNoticeFile.setNoticeId(uccNotice.getNoticeId());
                   uccNoticeFile.setFileName(file.getOriginalFilename());
                   uccNoticeFile.setCreatedTime(dateTime);
                   uccNoticeFile.setCreatedUserId(uccNotice.getUpdatedUserId());
                   uccNoticeFile.setCreatedUserName(uccNotice.getUpdatedUserName());
                   uccNoticeFile.setContentType(file.getContentType());
                   try {
                       fileBox = file.getBytes();
                       this.uccNoticeFileService.save(fileBox, uccNoticeFile);
                   } catch (IOException e) {
                       e.printStackTrace();

                   }
               }
            }
        }
        return new ResultBean<>(this.uccNoticeService.save(uccNoticeold));
    }

    /***
     * 根据查询条件获取通知列表--APP用
     * 备注：如果是未读的，点击查询后需要将其置为已读
     * add by wangxiaoyu
     * 2018-09-07
     */
    @ApiOperation(value="根据查询条件获取通知公告列表--APP用", notes="根据查询条件获取通知公告列表--APP用", httpMethod = "POST")
    @RequestMapping(value = "/findNoticeListByUserId", method = RequestMethod.POST)
    public PageResultBean_New<List<UccNotice>> findNoticeListByUserId(@RequestBody NoticeShowListCriteria noticeShowListCriteria) {
        return new PageResultBean_New(uccNoticeService.selectByUserId(noticeShowListCriteria));
    }

    /***
     * 查询通知详情--APP用
     * 备注：该接口目前仅是APP用。因为该类上面方法也有查看详情的，
     * 但是本接口还需要含判断“如果是未读的，点击查询后需要将其置为已读”，
     * springdata我不太熟，所以单独写了这个 见谅！
     * add by wangxiaoyu
     * 2018-09-07
     */
    @ApiOperation(value="查询通知详情", notes="查询通知详情")
    @RequestMapping(value = "/showNoticeDetail", method= RequestMethod.POST)
    public ResultBean_New<UccNotice> showNoticeDetailByNoticeId(@RequestBody ShowNoticeDetailCriteria showNoticeDetailCriteria){
        return uccNoticeService.showNoticeDetailByNoticeId(showNoticeDetailCriteria);
    }

}
