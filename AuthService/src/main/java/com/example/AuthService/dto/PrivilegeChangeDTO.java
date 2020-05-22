package com.example.AuthService.dto;

import java.util.List;

public class PrivilegeChangeDTO {
    List<Long> privilegeList;
    Long enduserId;
    boolean remove;

    public List<Long> getPrivilegeList() {
        return privilegeList;
    }

    public void setPrivilegeList(List<Long> privilegeList) {
        this.privilegeList = privilegeList;
    }

    public Long getEnduserId() {
        return enduserId;
    }

    public void setEnduserId(Long enduserId) {
        this.enduserId = enduserId;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
