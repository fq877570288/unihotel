package cn.cucsi.bsd.ucc.common.beans;

import java.util.Date;

public class TaskTypeForMybatisCriteria extends BasicPageCriteriaForBean{

    private String taskTypeId; //主键

    private String taskTypeCode; //任务类型编码

    private String taskTypeName; //任务类型名称

    private Date createTime; //创建时间

    private String domainId;//租户id

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId == null ? null : taskTypeId.trim();
    }

    public String getTaskTypeCode() {
        return taskTypeCode;
    }

    public void setTaskTypeCode(String taskTypeCode) {
        this.taskTypeCode = taskTypeCode == null ? null : taskTypeCode.trim();
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName == null ? null : taskTypeName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
