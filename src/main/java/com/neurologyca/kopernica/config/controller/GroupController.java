package com.neurologyca.kopernica.config.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurologyca.kopernica.config.model.Group;
import com.neurologyca.kopernica.config.model.GroupList;
import com.neurologyca.kopernica.config.repository.GroupRepository;

@RestController
@RequestMapping("group")
public class GroupController {
    @Autowired
    private GroupRepository groupRepository;
    
    
    @GetMapping("/getGroups")
	public List<Group> getGroups() throws Exception {
		return groupRepository.getGroups();
	}
	      
    @PutMapping("/saveGroupProtocol/{protocolId}")
    public void saveGroupProtocol(@PathVariable Integer protocolId, @RequestBody GroupList groupList) throws Exception {
        groupRepository.saveGroupProtocol(groupList, protocolId);
    }
    
    @DeleteMapping("/deleteGroupProtocol/{protocolId}/{groupId}")
    public void deleteGroupProtocol(@PathVariable Integer protocolId, @PathVariable Integer groupId) throws Exception {
    	groupRepository.deleteGroupProtocol(protocolId, groupId);
    }

}