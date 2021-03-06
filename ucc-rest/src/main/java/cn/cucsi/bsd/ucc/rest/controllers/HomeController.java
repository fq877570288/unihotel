package cn.cucsi.bsd.ucc.rest.controllers;

import cn.cucsi.bsd.ucc.common.beans.*;
import cn.cucsi.bsd.ucc.common.untils.MyUtils;
import cn.cucsi.bsd.ucc.data.domain.*;
import cn.cucsi.bsd.ucc.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * coding in zss
 * 2018.9.26
 * 移植自outcall 根据需求已更改
 */
@Api(tags = {"主页信息接口(移植自outcall _ coding zss)"})
@RestController
@RequestMapping(value = "/home")
public class HomeController {
    private static SimpleDateFormat sdf = new SimpleDateFormat("M.d");
    private static SimpleDateFormat sdf_M_D_CHINA = new SimpleDateFormat("M月d日");
    private static SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private PbxExtsService pbxExtsService;
    @Autowired
    private UccNoticeService uccNoticeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private PbxGatewaysService pbxGatewaysService;
    @Autowired
    private PbxQueuesService pbxQueuesService;
    @Autowired
    private UccUserService uccUserService;
    @Autowired
    private UccDeptsService uccDeptsService;
    @Autowired
    private LoginLogService loginLogService;

    /**
     * 主页视图
     */
    //@UserFlag
    @ApiOperation(value = "主页逻辑块", notes = "主页逻辑块", httpMethod = "GET")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Object> IndexView(String domainId, String userId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            PbxExtsCriteria search = new PbxExtsCriteria();
            search.setDomainId(domainId);
            PageResultBean<List<PbxExts>> bean = new PageResultBean(this.pbxExtsService.findAll(search));
            //未读消息
            int countNotice = uccNoticeService.selectByFlagCount(userId, domainId);
            UccNoticeCriteria uccNoticeCriteriaSearch = new UccNoticeCriteria();
            uccNoticeCriteriaSearch.setNoticeType("1");
            uccNoticeCriteriaSearch.setUserId(userId);
            uccNoticeCriteriaSearch.setFlag("0");
            uccNoticeCriteriaSearch.setDomainId(domainId);
            Page<UccNotice> pageUccNotice = uccNoticeService.findAll(uccNoticeCriteriaSearch);
            map.put("return_msg", "success");
            map.put("return_code", "success");
            map.put("countNotice", countNotice);
            map.put("noticeList", pageUccNotice.getContent());
            map.put("exts", bean.getData());
            return map;
        } catch (Exception e) {
            System.out.println("查询主页逻辑块失败！");
            e.printStackTrace();
        }
        map.put("return_msg", "error");
        map.put("return_code", "error");
        return map;
    }

    /**
     * 主页视图Plus
     */
    //@UserFlag
    @ApiOperation(value = "主页视图Plus", notes = "主页视图Plus", httpMethod = "GET")
    @RequestMapping(value = "/index1", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Object> IndexView1(HttpSession session) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        UccUsers user = (UccUsers)session.getAttribute("uccUsers");
        String DeptIdAndChildIds = (String) session.getAttribute("DeptIdAndChildIds");
        try {
            map.put("return_msg", "success");
            map.put("return_code", "success");
            PbxExtsCriteria search = new PbxExtsCriteria();
            search.setDomainId(user.getDomainId());
            PageResultBean<List<PbxExts>> bean = new PageResultBean(this.pbxExtsService.findAll(search));
            map.put("exts", bean.getData());
            int wa = 0;
            int wt = 0;
            int oa = 0;
            int on = 0;
            int cd = 0;
            int ct = 0;
            if (DeptIdAndChildIds != null && DeptIdAndChildIds.length() > 0) {
                //通过部门ID查询需要的信息数量
                wa = taskService.selectWaitAllCount(DeptIdAndChildIds, user.getUserId());
                wt = taskService.selectWaitTodayCount(DeptIdAndChildIds, user.getUserId());
                oa = taskService.selectOngoingAllCount(DeptIdAndChildIds, user.getUserId());
                on = taskService.selectOngoingNoCount(DeptIdAndChildIds, user.getUserId());
                cd = taskService.selectCompleteByDaysCount(DeptIdAndChildIds, user.getDomainId());
                ct = taskService.selectCompleteTodayCount(DeptIdAndChildIds, user.getDomainId());
            }
            map.put("wa", wa);
            map.put("wt", wt);
            map.put("oa", oa);
            map.put("on", on);
            map.put("cd", cd);
            map.put("ct", ct);
            //未读消息
            UccNoticeCriteria uccNoticeCriteriaSearch = new UccNoticeCriteria();
            uccNoticeCriteriaSearch.setDomainId(user.getDomainId());
            uccNoticeCriteriaSearch.setNoticeType("1");
            uccNoticeCriteriaSearch.setUserId(user.getUserId());
            uccNoticeCriteriaSearch.setFlag("0");
            Page<UccNotice> pageUccNotice = uccNoticeService.findAll(uccNoticeCriteriaSearch);

            map.put("count", pageUccNotice.getTotalElements());
            map.put("countNotice", uccNoticeService.selectByFlagTypeCount(user.getUserId(), user.getDomainId()));
            map.put("noticeList", pageUccNotice.getContent());
            //
            UccNoticeCriteria searchSevenDay = new UccNoticeCriteria();
            //开始日期
            Calendar calendar = Calendar.getInstance();
            Date date = new Date(System.currentTimeMillis());
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            date = (Date) calendar.getTime();
            searchSevenDay.setNoticeTimeFrom(date);
            searchSevenDay.setUserId(user.getUserId());
            searchSevenDay.setNoticeType("0");
            searchSevenDay.setDomainId(user.getDomainId());
            Page<UccNotice> pageUccNotice1 = uccNoticeService.findAll(searchSevenDay);
            int countNoticeAndAffiche = uccNoticeService.selectByFlagCount(user.getUserId(), user.getDomainId());
            map.put("noticeList1", pageUccNotice1.getContent());
            map.put("countNotice1", countNoticeAndAffiche);
            return map;
        } catch (Exception e) {
            System.out.println("查询主页视图Plus失败！");
            e.printStackTrace();
        }
        map.put("return_msg", "error");
        map.put("return_code", "error");
        return map;
    }

    /***
     * 任务查询列表查询
     */
    //@UserFlag
    @ApiOperation(value = "主页Echars", notes = "主页Echars", httpMethod = "POST")
    @RequestMapping(value = "/chartforpoint", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String ChartTaskList(@RequestBody TaskDetailSearch search, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        String DeptIdAndChildIds = (String) session.getAttribute("DeptIdAndChildIds");
        try {
            JSONArray j = new JSONArray();
            jsonObject.put("return_msg", "success");
            jsonObject.put("return_code", "success");
            String deptIdAndChildId = "";
            int cTaskInts[] = new int[]{0, 0, 0, 0, 0, 0, 0};
            int eCallInts[] = new int[]{0, 0, 0, 0, 0, 0, 0};
            int aCallInts[] = new int[]{0, 0, 0, 0, 0, 0, 0};
            String[] times = new String[7];
            for (int i = 6; i >= 0; i--) {
                times[i] = sdfd.format(new Date(new Date(System.currentTimeMillis()).getTime() - i * 24 * 60 * 60 * 1000));
            }
            String CompleteTasksql = "";
            String ECallsql = "";
            String ACallsql = "";
            if (DeptIdAndChildIds != null && DeptIdAndChildIds.length() != 0) {
                CompleteTasksql = new MyUtils().generateSQL(times, search.getDomainId(), DeptIdAndChildIds, "CompleteTask");
                ECallsql = new MyUtils().generateSQL(times, search.getDomainId(), DeptIdAndChildIds, "ECall");
                ACallsql = new MyUtils().generateSQL(times, search.getDomainId(), DeptIdAndChildIds, "ACall");
                cTaskInts = taskService.queryCompleteTask(CompleteTasksql);
                eCallInts = taskService.queryECall(ECallsql);
                aCallInts = taskService.queryACall(ACallsql);
               /* for(int i=6; i>=0;i--){
                    long date = new Date(System.currentTimeMillis()).getTime()-i*24*60*60*1000;
                    //把一天的完成任务数装到完成任务数组中
                    cTaskInts[6-i]=taskService.queryCompleteTask(new Date(date),DeptIdAndChildIds,search.getDomainId());

                    //把一天的有效电话数装到有效电话数组中
                    eCallInts[6-i]=taskService.queryECall( new Date(date),DeptIdAndChildIds,search.getDomainId());

                    //把一天的外呼电话数装到外呼电话数组中
                    aCallInts[6-i]=taskService.queryACall(new Date(date),DeptIdAndChildIds,search.getDomainId());
                }*/
            }
            String time = "";
            JSONObject json;
            for (int i = 6; i >= 0; i--) {
                long date = new Date(System.currentTimeMillis()).getTime() - i * 24 * 60 * 60 * 1000;
                time = sdf_M_D_CHINA.format(new Date(date));
                json = new JSONObject();
                json.put("month", time);
                json.put("每日完成任务数量", cTaskInts[i]);
                json.put("有效电话数量", eCallInts[i]);
                json.put("外呼电话数量", aCallInts[i]);
                j.put(json);
                json = null;
            }
            jsonObject.put("data", j);
            return jsonObject.toString();
        } catch (Exception e) {
            System.out.println("主页Echars失败！");
            e.printStackTrace();
        }
        try {
            jsonObject.put("return_msg", "error");
            jsonObject.put("return_code", "error");
        } catch (Exception e) {
            System.out.println("封装json失败");
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 监控中心
     */
    //@UserFlag(870)
    @ApiOperation(value = "监控中心", notes = "监控中心", httpMethod = "GET")
    @RequestMapping(value = "/monitor", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Object> MonitorView(String domainId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("return_msg", "success");
            map.put("return_code", "success");
            PbxExtsCriteria search = new PbxExtsCriteria();
            search.setDomainId(domainId);
            PageResultBean<List<PbxExts>> bean = new PageResultBean(this.pbxExtsService.findAll(search));
            map.put("exts", bean.getData());
            PbxGatewaysCriteria pbxGatewaysSearch = new PbxGatewaysCriteria();
            pbxGatewaysSearch.setDomainId(domainId);
            Page<PbxGateways> pagePbxGateways = pbxGatewaysService.findAll(pbxGatewaysSearch);
            map.put("gws", pagePbxGateways.getContent());
            PbxQueuesCriteria PbxQueuessearch = new PbxQueuesCriteria();
            PbxQueuessearch.setDomainId(domainId);
            Page<PbxQueues> queues = pbxQueuesService.findAll(PbxQueuessearch);
          /*  ObjectMapper mapper = new ObjectMapper();
            for (PbxQueues pbxQueue : queues.getContent()) {
                if (pbxQueue.getNumbers() != null && pbxQueue.getNumbers().size()>0) {
                    try {
                        pbxQueue.setNumbersJson(mapper.writeValueAsString(pbxQueue.getNumbers()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage(), e);
                    }
                }
            }*/
            map.put("queues", queues.getContent());
            return map;
        } catch (Exception e) {
            System.out.println("查询监控中心失败！");
            e.printStackTrace();
        }
        map.put("return_msg", "error");
        map.put("return_code", "error");
        return map;
    }


    /***
     * 员工详情
     */
    //@UserFlag(870)
    @ApiOperation(value = "员工详情", notes = "员工详情", httpMethod = "GET")
    @RequestMapping(value = "/monitor/userInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Object> taskDetail(String userId, String extNum) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("return_msg", "success");
            map.put("return_code", "success");
            UccUserCriteria search = new UccUserCriteria();
            search.setExtNum(extNum);
            Page<UccUsers> pageuser = uccUserService.findAll(search);
            map.put("bean", pageuser.getContent().get(0));
            List<UccDepts> uccDeptsList = uccDeptsService.selectByUserId(userId);
            map.put("depts", uccDeptsList);
            map.put("urs", uccUserService.findOne(userId));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查询员工详情失败！");
        }
        map.put("return_msg", "error");
        map.put("return_code", "error");
        return map;
    }

    /**
     * 用户中心
     */
    //@UserFlag
    @ApiOperation(value = "用户中心", notes = "用户中心", httpMethod = "GET")
    @RequestMapping(value = "/user/center", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map<String, Object> userCenterView(String userId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map.put("return_msg", "success");
            map.put("return_code", "success");
            LoginLogCriteria loginLogCriteria = new LoginLogCriteria();
            loginLogCriteria.setUserId(userId);
            loginLogService.findAll(loginLogCriteria).getContent();
            map.put("list", loginLogService.findAll(loginLogCriteria).getContent());
            return map;
        } catch (Exception e) {
            System.out.println("查询用户中心失败！");
            e.printStackTrace();
        }
        map.put("return_msg", "error");
        map.put("return_code", "error");
        return map;
    }
}
