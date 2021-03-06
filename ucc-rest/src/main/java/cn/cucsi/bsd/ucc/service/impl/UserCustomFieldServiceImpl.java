package cn.cucsi.bsd.ucc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.cucsi.bsd.ucc.common.mapper.DataCustomfieldMapper;
import cn.cucsi.bsd.ucc.common.mapper.UserCustomFieldMapper;
import cn.cucsi.bsd.ucc.common.untils.UUIDGenerator;
import cn.cucsi.bsd.ucc.data.domain.DataCustomfield;
import cn.cucsi.bsd.ucc.data.domain.UserCustomField;
import cn.cucsi.bsd.ucc.service.UserCustomFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCustomFieldServiceImpl implements UserCustomFieldService {
	
	@Autowired
	private UserCustomFieldMapper userCustomFieldMapper;
	@Autowired
	private DataCustomfieldMapper dataCustomfieldMapper;

	@Transactional
	@Override
	public int saveUserCustomField(String customfieldNames, String userId) throws Exception {
		
		List<DataCustomfield> customfiledInfoList = null;
		List<UserCustomField> userCustomFiledList = new ArrayList<UserCustomField>();
		UserCustomField userCustomFiled = null;
		try {
			// 主键
			UUIDGenerator generator = new UUIDGenerator();
			String dataCustomfieldsIds = "'" + customfieldNames.replaceAll(",", "','") + "'";

			Map<String, String> idsMap = new HashMap<String, String>();
			idsMap.put("dataCustomfieldsIds", dataCustomfieldsIds);

			// 根据选择的自定义字段主键  查询自定义字段信息
			customfiledInfoList = dataCustomfieldMapper.selectByIDs(idsMap);

			// 根据员工ID　删除员工-自定义字段关联中间表信息
			userCustomFieldMapper.deleteImportByUserID(userId);

			// 组织数据  插入将员工对应选择的自定义字段 保存到员工-自定义字段关联中间表
			for(int i=0; i<customfiledInfoList.size(); i++){
                userCustomFiled = new UserCustomField();

                userCustomFiled.setUserId(userId);
                userCustomFiled.setDataCustomfieldsId(customfiledInfoList.get(i).getDataCustomfieldsId());
                userCustomFiled.setIeFlag(UserCustomField.IMPORTFIELD);

                String uuid = generator.generate();
                userCustomFiled.setUserCustomfieldsId(uuid);

                userCustomFiledList.add(userCustomFiled);
            }
			return userCustomFieldMapper.insertGroup(userCustomFiledList);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("saveUserCustomField方法操作失败！");
			return -1;
		}
	}

	@Transactional
	@Override
	public int saveUserCustomExportField(String customfieldNames, String userId) throws Exception {
		
		List<DataCustomfield> customfiledInfoList = null;
		
		List<UserCustomField> userCustomFiledList = new ArrayList<UserCustomField>();
		
		UserCustomField userCustomFiled = null;
		
		// 主键
		UUIDGenerator generator = new UUIDGenerator();

		try {
			String dataCustomfieldsIds = "'" + customfieldNames.replaceAll(",", "','") + "'";

			Map<String, String> idsMap = new HashMap<String, String>();
			idsMap.put("dataCustomfieldsIds", dataCustomfieldsIds);

			// 根据选择的自定义字段主键  查询自定义字段信息
			try {
                customfiledInfoList = dataCustomfieldMapper.selectByIDsExport(idsMap);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("根据选择的自定义字段主键查询自定义字段信息发生异常！");
            }

			// 根据员工ID　删除员工-自定义字段关联中间表信息
			try {
                userCustomFieldMapper.deleteExportByUserID(userId);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("根据员工ID删除员工-自定义字段关联中间表信息发生异常！");
            }

			// 组织数据  插入将员工对应选择的自定义字段 保存到员工-自定义字段关联中间表
			for(int i=0; i<customfiledInfoList.size(); i++){
                userCustomFiled = new UserCustomField();
                userCustomFiled.setUserId(userId);
                userCustomFiled.setDataCustomfieldsId(customfiledInfoList.get(i).getDataCustomfieldsId());
                userCustomFiled.setIeFlag(UserCustomField.EXPORTFIELD);
                String uuid = generator.generate();
                userCustomFiled.setUserCustomfieldsId(uuid);
                userCustomFiledList.add(userCustomFiled);
            }
			return userCustomFieldMapper.insertGroup(userCustomFiledList);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("导出数据 员工自定义显示字段保存发生异常 ");
			return -1;
		}
	}
}
