package com.francetelecom.saasstore.wro4j.taglib;

import java.io.InputStream;
import java.util.*;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WroConfig {
	private static final String WRO_XML_LOCATION = "/wro.xml";
	private static final String WRO_BASE_URL = "/wro/";

	private static WroConfig instance;

	private Map<String, Group> groups;
	private ServletContext servletContext;
	private boolean initialized = false;

	public static WroConfig getInstance() throws ConfigurationException {
		return instance;
	}

	private void loadConfig() throws ConfigurationException {
		InputStream is = getClass().getResourceAsStream(WRO_XML_LOCATION);
		if (is != null) {
			groups = new HashMap<String, Group>();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new ConfigurationException(e);
			}
			Document doc;
			try {
				doc = db.parse(is);
			} catch (Exception e) {
				throw new ConfigurationException(e);
			}
			doc.getDocumentElement().normalize();

			String rootName = doc.getDocumentElement().getNodeName();
			if (!"groups".equals(rootName)) {
				throw new ConfigurationException(
						"invalid wro config file: the root element is not 'groups'");
			}

			NodeList liste = doc.getElementsByTagName("group");
			for (int i = 0; i < liste.getLength(); i++) {
				Node node = liste.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elt = (Element) node;
					String groupName = elt.getAttribute("name");
					List<String> jsFiles = getFilesFor(elt, "js");
					List<String> cssFiles = getFilesFor(elt, "css");
					Group group = new Group(groupName);
					group.put("js", jsFiles);
					group.put("css", cssFiles);
					groups.put(groupName, group);
				}
			}
		} else {
			throw new ConfigurationException("wro.xml was not found at "
					+ WRO_XML_LOCATION);
		}
	}

	private List<String> getFilesFor(Element groupElt, String tagName) {
		List<String> result = new ArrayList<String>();
		NodeList groups = groupElt.getElementsByTagName(tagName);
		for (int i = 0; i < groups.getLength(); i++) {
			Node node = groups.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String file = node.getChildNodes().item(0).getNodeValue();
				if (file.indexOf('*') != -1 || file.indexOf('?') != -1) {
					result.addAll(getAllPathsForGlob(file));
				} else {
					result.add(file);
				}
			}
		}

		return result;
	}

	private Collection<String> getAllPathsForGlob(String globFile) {
		Collection<String> result = new HashSet<String>();
		Collection<String> resources = getAllResourcePaths();
		AntPathStringMatcher matcher = new AntPathStringMatcher(globFile);
		for (String resource : resources) {
			if (matcher.match(resource)) {
				result.add(resource);
			}
		}

		return result;
	}

	private Collection<String> getAllResourcePaths() {
		Collection<String> result = new HashSet<String>();
		LinkedList<String> queue = new LinkedList<String>();
		queue.add("/"); // initial
		String current;

		while ((current = queue.poll()) != null) {
			Set<String> resourcePaths = servletContext
					.getResourcePaths(current);
			if (resourcePaths == null) {
				continue; // should not happen
			}

			for (String path : resourcePaths) {
				if (path.endsWith("/")) { // directory
					queue.add(path);
				} else {
					result.add(path);
				}
			}
		}

		return result;
	}

	private void loadMinimizedFiles() {
		Set<String> resourcePaths = servletContext
				.getResourcePaths(WRO_BASE_URL);
		if (resourcePaths == null) {
			return;
		}

		for (String path : resourcePaths) {
			String basename = FilenameUtils.getBaseName(path);
			String groupName = basename.substring(0, basename.lastIndexOf('-'));
			if (groups.containsKey(groupName)) {
				String type = FilenameUtils.getExtension(path);
				Group group = groups.get(groupName);
				group.putMinimizedFile(type, path);
			}
		}
	}

	/* package */static synchronized void createInstance(ServletContext context) {
		if (instance == null) {
			instance = new WroConfig();
			instance.servletContext = context;
			instance.loadConfig();
			instance.loadMinimizedFiles();
			instance.initialized = true;
		}
	}

	public Group getGroup(String groupName) {
		if (initialized) {
			return groups.get(groupName);
		} else {
			throw new ConfigurationException(
					"WroConfig was not correctly initialized");
		}
	}
}
