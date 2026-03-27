package com.affidock.api.modules.groups.controller;

import com.affidock.api.common.base.BaseController;
import com.affidock.api.modules.groups.dto.GroupRequest;
import com.affidock.api.modules.groups.dto.GroupResponse;
import com.affidock.api.modules.groups.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController extends BaseController<GroupRequest, GroupResponse> {
    public GroupController(GroupService service) {
        super(service);
    }
}
