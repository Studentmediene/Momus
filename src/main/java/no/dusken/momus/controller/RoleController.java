/*
 * Copyright 2013 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.controller;

import no.dusken.momus.model.Role;
import no.dusken.momus.service.repository.RoleRepository;
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
        return roleRepository.saveAndFlush(newRole);
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
