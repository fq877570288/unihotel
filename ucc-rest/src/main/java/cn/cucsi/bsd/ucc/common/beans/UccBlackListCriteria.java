package cn.cucsi.bsd.ucc.common.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/***
 * 根据条件查询黑名单列表
 * add by ZSS
 * 2018-09-6
 */
@ApiModel
public class UccBlackListCriteria extends BasicPageCriteriaForBean{
    @ApiModelProperty(value = "客户Id",required = false)
    private String custId;
    @ApiModelProperty(value = "域Id",required = false)
    private String domainId;
    @ApiModelProperty(value = "客户名称",required = false)
    private String custName;
    @ApiModelProperty(value = "开始时间",required = false)
    private String startTime;
    @ApiModelProperty(value = "结束时间",required = false)
    private String endTime;
    @ApiModelProperty(value = "状态（黑名单为7）",required = false)
    private Integer type;
    @ApiModelProperty(value = "租户名",required = false)
    private String domainName;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
