package no.dusken.momus.controller;

import no.dusken.momus.model.Role;
import no.dusken.momus.service.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Role createNewRole(@RequestBody Role newRole) {
        Role createdRole = roleRepository.saveAndFlush(newRole);
        return createdRole;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteRole(@PathVariable Long id) {
        roleRepository.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Role updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        Role savedRole = roleRepository.findOne(id);
        savedRole.setName(updatedRole.getName());
        savedRole = roleRepository.saveAndFlush(savedRole);

        return savedRole;
    }

}
