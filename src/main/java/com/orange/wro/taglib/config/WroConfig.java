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
* 
* Author is Julien Wajsberg <julien.wajsberg@orange.com>
*/

package com.orange.wro.taglib.config;

import org.apache.commons.io.FilenameUtils;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

import java.util.*;

/**
 * This singleton keeps the minimized and unminimized files for
 * each group.
 * 
 * @author Julien Wajsberg
 */
public class WroConfig {
	/* package */ static WroConfig instance;

	private Map<String, FilesGroup> groups;
	private WroTagLibConfig wroTagLibConfig;
	private boolean initialized = false;

	public static WroConfig getInstance() throws ConfigurationException {
		if (instance == null) {
			throw new ConfigurationException("The instance was not created.");
		}
		/* lazy initialization because we need to be in a request thread */
		synchronized(instance) {
			if (!instance.initialized) {
				instance.loadConfig();
				instance.initialized = true;
			}
		}
		return instance;
	}

	private void loadConfig() throws ConfigurationException {
		WroModel model = this.wroTagLibConfig.getModel(
            this.wroTagLibConfig.getServletContextAttributeHelper()
        );
		
		groups = new HashMap<String, FilesGroup>();
		
		for (Group group: model.getGroups()) {
			String groupName = group.getName();
			FilesGroup filesGroup = new FilesGroup(groupName, new GroupLoader(group));
			groups.put(groupName, filesGroup);
		}
	}

	/* package */static synchronized void createInstance(WroTagLibConfig wroTagLibConfig) {
		if (instance == null) {
			instance = new WroConfig();
			instance.wroTagLibConfig = wroTagLibConfig;
		}
	}

	public FilesGroup getGroup(String groupName) {
		if (initialized) {
			return groups.get(groupName);
		} else {
			throw new ConfigurationException(
					"WroConfig was not correctly initialized");
		}
	}

    public WroTagLibConfig getWroTagLibConfig() {
        return this.wroTagLibConfig;
    }

	
	private class GroupLoader implements IGroupLoader {
		
		private Group group;
		
		GroupLoader(Group group) {
			this.group = group;
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
			Set<String> resourcePaths = wroTagLibConfig.getResourcePaths();
			String groupName = group.getName();
			
			if (resourcePaths == null) {
				return null;
			}

			for (String path : resourcePaths) {
				String basename = FilenameUtils.getBaseName(path);
				
				if (basename.startsWith(groupName)) {
					String type = FilenameUtils.getExtension(path);
					res.put(ResourceType.get(type), path);
				}
			}
			
			return res;
		}
	}
}
