/*
* Copyright 2011 France Télécom
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

package ro.isdc.wro.taglib.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesGroup {
	private String name;
	private Map<String,List<String>> group = new HashMap<String, List<String>>();
	private Map<String,String> minimizedFiles = new HashMap<String, String>();
	
	public FilesGroup(String name) {
		this.name = name;
	}
	
	public void put(String type, List<String> files) {
		group.put(type, files);
	}
	
	public List<String> get(String type) {
		return group.get(type);
	}
	
	public void putMinimizedFile(String type, String minimizedFile) {
		minimizedFiles.put(type, minimizedFile);
	}
	
	public String getMinimizedFile(String type) {
		return minimizedFiles.get(type);
	}
	
	public String getName() {
		return name;
	}
	
}
