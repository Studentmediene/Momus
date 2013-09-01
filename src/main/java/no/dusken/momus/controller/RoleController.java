package no.dusken.momus.controller;

import no.dusken.momus.model.Group;
import no.dusken.momus.service.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private GroupRepository groupRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Group> getAllRoles() {
        return groupRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Group createNewRole(@RequestBody Group newGroup) {
        return groupRepository.saveAndFlush(newGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteRole(@PathVariable Long id) {
        groupRepository.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Group updateRole(@PathVariable Long id, @RequestBody Group updatedGroup) {
        Group savedGroup = groupRepository.findOne(id);
        savedGroup.setName(updatedGroup.getName());
        savedGroup = groupRepository.saveAndFlush(savedGroup);

        return savedGroup;
    }

}
