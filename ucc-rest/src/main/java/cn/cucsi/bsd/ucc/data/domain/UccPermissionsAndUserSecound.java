package cn.cucsi.bsd.ucc.data.domain;

import cn.cucsi.bsd.ucc.common.JSONView;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ucc_permissions", schema = "ucc", catalog = "")
public class UccPermissionsAndUserSecound {
    @Transient
    private String permissionId;
    @Transient
    private String mpid;
    @JsonView(JSONView.Summary.class)
    private String name;

    @JsonView(JSONView.Summary.class)
    private String icon;

    @JsonView(JSONView.Summary.class)
    private String path;

    @JsonView(JSONView.Summary.class)
    private String isLeftMenu;



    @Id
    @Column(name = "permission_id", nullable = false, length = 32)
    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    @Basic
    @Column(name = "mpid", nullable = true, length = 32)
    public String getMpid() {
        return mpid;
    }

    public void setMpid(String mpid) {
        this.mpid = mpid;
    }

    @Basic
    @Column(name = "permission_name", nullable = true, length = 48)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Basic
    @Column(name = "icon", nullable = true, length = 32)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    @Basic
    @Column(name = "route", nullable = true, length = 32)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic
    @Column(name = "is_left_menu", nullable = true, length = 32)
    public String getIsLeftMenu() {
        return isLeftMenu;
    }

    public void setIsLeftMenu(String isLeftMenu) {
        this.isLeftMenu = isLeftMenu;
    }

}
