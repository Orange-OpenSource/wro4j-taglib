/*
* Copyright 2011, 2012 France Télécom
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.orange.wro.taglib.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public class GroupLoader implements IGroupLoader {
	
	private Group group;
	private WroTagLibConfig wroTagLibConfig;
	
	GroupLoader(WroTagLibConfig wroTagLibConfig, Group group) {
		this.group = group;
		this.wroTagLibConfig = wroTagLibConfig;
	}

	@Override
	public List<String> getResources(ResourceType resourceType) {
		
		List<String> result = new ArrayList<String>();
		
		Group filteredGroup = group.collectResourcesOfType(resourceType);
		for (Resource resource: filteredGroup.getResources()) {
			String file = resource.getUri();
			result.add(file);
		}

		return result;
	}

	@Override
	public Map<ResourceType, String> getMinimizedResources() {
		Map<ResourceType, String> res = new HashMap<ResourceType, String>();
		String groupName = group.getName();
		Set<String> resourcePaths = wroTagLibConfig.getResourcePaths();
		
		if (resourcePaths != null) {
			for (String path : resourcePaths) {
				String fileName = FilenameUtils.getName(path);
				String group = wroTagLibConfig.getGroupForFile(fileName);
				
				if (group != null) {
					if (groupName.equals(FilenameUtils.getBaseName(group))) {
						String type = FilenameUtils.getExtension(group);
						res.put(ResourceType.get(type), path);
					}
				
				} else {
					if (FilenameUtils.getBaseName(path).startsWith(groupName)) {
						String type = FilenameUtils.getExtension(path);
						res.put(ResourceType.get(type), path);
					}
				}
			}
		}
		
		return res;
	}
}