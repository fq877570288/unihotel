package cn.cucsi.bsd.ucc.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

import cn.cucsi.bsd.ucc.common.beans.AllocationTaskCriteria;
import cn.cucsi.bsd.ucc.common.beans.PageResultBean_New;
import cn.cucsi.bsd.ucc.common.beans.TaskDetailSearch;
import cn.cucsi.bsd.ucc.common.mapper.ImportBatchMapper;
import cn.cucsi.bsd.ucc.common.mapper.TaskDetailMapper;
import cn.cucsi.bsd.ucc.data.domain.ImportBatch;
import cn.cucsi.bsd.ucc.data.domain.TaskDetail;
import cn.cucsi.bsd.ucc.data.domain.UccDepts;
import cn.cucsi.bsd.ucc.service.ImportBatchService;
import cn.cucsi.bsd.ucc.service.UccDeptsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportBatchServiceImpl implements ImportBatchService {

	@Autowired
	private ImportBatchMapper importBatchMapper;
	@Autowired
	private TaskDetailMapper taskDetailMapper;
	@Autowired
	private UccDeptsService uccDeptsService;

	@Override
	public List<ImportBatch> queryImportBatch(String taskTypeName, String recent) throws Exception {

		Map<String, Object> condition = new HashMap<>();
//		condition.put("batchFlag", "2");
		condition.put("taskTypeName", taskTypeName);
		condition.put("recent", recent);
		List<ImportBatch> importBatchList = importBatchMapper.selectBySearch(condition);
		return importBatchList;
	}

	@Override
	public List<String> selectRecentImportBatch() throws Exception{

//		Map<String, Object> barchsMap = new HashMap<>();
//		barchsMap.put("batchFlag", "2");
		return this.importBatchMapper.selectForTaskJob();
	}

	@Override
	public List<ImportBatch> selectBySearch(Map<String, Object> barchsMap) throws Exception{
		return this.importBatchMapper.selectBySearch(barchsMap);
	}

	@Override
	public List<ImportBatch> selectAll(ImportBatch importBatch) throws Exception {
		return importBatchMapper.selectAll(importBatch);
	}

	@Override
	public List<ImportBatch> selectAllByBatchFlag(ImportBatch importBatch) throws Exception {
		return importBatchMapper.selectAllByBatchFlag(importBatch);
	}

	/***
	 * 分配任务列表查询
	 */
	@Override
	public PageResultBean_New<List<ImportBatch>> selectAllocationAllByBatchFlag(ImportBatch importBatch, AllocationTaskCriteria allocationTaskCriteria)
			throws Exception {
		PageResultBean_New<List<ImportBatch>> pageResultBean_new = null;
		TaskDetailSearch search = new TaskDetailSearch();
		List<TaskDetail> taskDetailList = null;
		try {
			String userId = allocationTaskCriteria.getUserId()==null?"":allocationTaskCriteria.getUserId();
			search.setUserId(userId);
			String deptIdAndChildId = allocationTaskCriteria.getDeptIdAndChildIds()==null?"":allocationTaskCriteria.getDeptIdAndChildIds();

			if(deptIdAndChildId != null && !"".equals(deptIdAndChildId)){
                //String [] deptIdAndChildIds = deptIdAndChildId.split(",");
                //if(deptIdAndChildIds.length > 1){
                    /*// 部门
					List<UccDepts> UccDeptsList = null;
					try {
						UccDeptsList = uccDeptsService.selectByUserId(search.getUserId());
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 增加权限控制  登录人是中心权限 查询流转操作为0的数据  登录人是网格或包区权限查询流转操作为1的数据
                    if(UccDeptsList != null && UccDeptsList.size() > 0){
                        if(UccDeptsList.get(0).getDeptLevel() <= 1){
                            search.setTaskStatus("0");
                            search.setImportPersonId(search.getUserId().toString());
                        }else {
                            search.setTaskStatus("1");
                        }
                    }*/
                    search.setRoperateDeptId(deptIdAndChildId);
					search.setTaskStatusList("'0', '1'");
					try {
						taskDetailList = taskDetailMapper.selectImportBatchsBySearch(search);
					} catch (Exception e) {
						e.printStackTrace();
					}
				//}
            }else {
				search.setAllLines(0);
			}
			String importBatchs = "";
			if(taskDetailList != null && taskDetailList.size() > 0){
                for(TaskDetail taskDetail : taskDetailList){
                    importBatchs += taskDetail.getImportBatch() + "','";
                }
            }
			if(!"".equals(importBatchs)){
                importBatchs = "'" + importBatchs.substring(0, importBatchs.lastIndexOf(","));
                importBatch.setImportBatchs(importBatchs);
            }
			Page pageInfo = PageHelper.startPage(allocationTaskCriteria.getPageNum(), allocationTaskCriteria.getPageSize());
			List<ImportBatch> list= importBatchMapper.selectAllByBatchFlag(importBatch);
			pageResultBean_new = new PageResultBean_New(pageInfo);
			pageResultBean_new.setList(list);
			pageResultBean_new.setReturnMsg("查询成功！");
			pageResultBean_new.setReturnCode(PageResultBean_New.SUCCESS);
			return pageResultBean_new;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("分配任务列表查询出现异常！");
			return null;
		}
	}

}
