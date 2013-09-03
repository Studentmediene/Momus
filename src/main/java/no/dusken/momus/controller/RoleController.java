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
