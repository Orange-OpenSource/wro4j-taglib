A taglib for [wro4j](http://code.google.com/p/wro4j/)
=====================================================

Why ?
-----
When using wro4j at buildtime using its maven plugin, we get the 
combined files with near random filenames. Therefore, we need
a way to find this files for the corresponding groups.

This taglib provides a way to create the markup to include the files.
It also provides a way to get the files' URIs in a JavaScript array.

Seems nice, how do I configure it ?
-----------------------------------

### wro.xml
Configure your `wro.xml` as usually, and place it in your `WEB-INF`
folder as always.

### Maven configuration
The best way to use wro4j at buildtime is the following.

This configuration doesn't generate correct URLs in CSS if you use the
CSS URL rewriting processor (which you should) and you're deploying wro-
generated files using Eclipse WTP. However you shouldn't need this because
this taglib lets you use the original version of the files anyway.

See also the comment in
the file.

```XML
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.orange.wro4j.examples</groupId>
	<artifactId>test-release-wro4j-buildtime</artifactId>
	<version>0.1-SNAPSHOT</version>
	<packaging>war</packaging>
	<name>test release with wro4j used at build-time</name>
	<url>http://maven.apache.org</url>
	<properties>
		<wro4j.version>1.6.0</wro4j.version>
	</properties>

	<build>
		<plugins>
			<plugin>
			  <groupId>org.apache.maven.plugins</groupId>
			  <artifactId>maven-compiler-plugin</artifactId>
			  <configuration>
			    <source>6</source>
			    <target>6</target>
			  </configuration>
			</plugin>
			<plugin>
				<groupId>ro.isdc.wro4j</groupId>
				<artifactId>wro4j-maven-plugin</artifactId>
				<version>${wro4j.version}</version>
				<executions>
					<execution>
						<phase>prepare-package</phase>
						<goals>
							<goal>run</goal>
						</goals>
					</execution>
				</executions>
			
				<configuration>
					<targetGroups>jquery,jquery-ui,appli</targetGroups>
					<ignoreMissingResources>false</ignoreMissingResources>
					<wroManagerFactory>com.orange.wro.examples.buildtime.CustomWroManagerFactory</wroManagerFactory>
					<contextFolder>${basedir}/src/main/webapp/</contextFolder>
					<!-- use ${basedir}/src/main/webapp/wro if you use the CssUrlRewritingProcessor
					     and wants to deploy wro-generated CSS files in WTP
					     but I dislike generating things there as it isn't removed by mvn clean -->
					<destinationFolder>${project.build.directory}/${project.build.finalName}/wro/</destinationFolder>
					<wroFile>${basedir}/src/main/webapp/WEB-INF/wro.xml</wroFile>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<version>2.1.1</version>
				<configuration>
					<webResources>
						<resource>
							<!-- this is relative to the pom.xml directory -->
							<directory>${project.build.directory}/generated-resources/wro/</directory>
						</resource>
					</webResources>
				</configuration>
			</plugin>
		</plugins>
	</build>
	<dependencies>
		<dependency>
			<groupId>ro.isdc.wro4j</groupId>
			<artifactId>wro4j-core</artifactId>
			<version>${wro4j.version}</version>
		</dependency>

		<dependency>
			<groupId>ro.isdc.wro4j</groupId>
			<artifactId>wro4j-extensions</artifactId>
			<version>${wro4j.version}</version>
			<!-- we don't need them at runtime and it brings to many transitive dependencies -->
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>2.5</version>
			<type>jar</type>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>jsp-api</artifactId>
			<version>2.0</version>
			<type>jar</type>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>com.orange.wro4j</groupId>
			<artifactId>wro4j-taglib</artifactId>
			<version>1.2</version>
		</dependency>
	</dependencies>


</project>
```

Be careful as wro4j is only compatible with maven 3 (as of wro4j 1.5). If you need to
use maven 2, use wro4j 1.4.9.

### web.xml configuration
#### Listeners
You need to add two listeners: normal wro4j's `WroServletContextListener`
and this module's `WroServletContextListener`.

#### Tag library configuration
The tag library may be configured through context parameters _or_ using a property file. 
The names of the available parameters are the same:

* `com.orange.wro.base.url` (_required_)
* `com.orange.wro.resource.domain` (optional)
* `com.orange.wro.less.path` (optional)

The first one (`com.orange.wro.base.url`) must be the URI where the combined
files are.

The resource domain parameter (`com.orange.wro.resource.domain`) might be useful if you have a CDN, or if you serve your static resources from a [static domain](https://developers.google.com/speed/docs/best-practices/request#ServeFromCookielessDomain).

Finally, if you are using [LESS](http://lesscss.org/) resources directly, wro4j-taglib is able to include the LESS compiler in the page; you need to provide its path (`com.orange.wro.less.path`).

#### Property file
As an alternative, you may define base Url, resource domain and LESS path in a property file instead. For maximum flexibility in choosing the name and position of the property file, you can add these context parameters to `web.xml`:

* `com.orange.wro.properties.default.location`
* `com.orange.wro.properties.location`
* `com.orange.wro.properties.filename`

All of them are optional, and may be combined to suit your needs. If none is specified, the tag library will try to load a file called `wro4j-taglib.properties` from the default path (`/etc`).

If no such file is there, it will try to load it from the Web Application classpath.

In the end, it will resort to read the context parameters as specified in `web.xml`.


#### Filters
Lastly, you must add a Filter to configure wro4j's Context.
This Filter must be mapped to `/*`.

```XML 
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	id="WebApp_ID" version="2.5">
	<display-name>test-release</display-name>
	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
		<welcome-file>index.htm</welcome-file>
		<welcome-file>index.jsp</welcome-file>
		<welcome-file>default.html</welcome-file>
		<welcome-file>default.htm</welcome-file>
		<welcome-file>default.jsp</welcome-file>
	</welcome-file-list>

	<listener>
		<listener-class>ro.isdc.wro.http.WroServletContextListener</listener-class>
	</listener>
	<listener>
		<listener-class>com.orange.wro.taglib.config.WroContextListener</listener-class>
	</listener>
	
	<context-param>
		<param-name>com.orange.wro.base.url</param-name>
		<param-value>/wro</param-value>
	</context-param>

	<!-- optional: only needed if you intend to use a static domain or a CDN -->
	<context-param>
		<param-name>com.orange.wro.resource.domain</param-name>
		<param-value>//static.company.domain</param-value>
	</context-param>

	<!-- optional: only needed if you intend to use LESS with exploded files -->
	<context-param>
		<param-name>com.orange.wro.less.path</param-name>
		<param-value>/js/less-xxx.js</param-value>
	</context-param>

	<filter>
		<filter-name>wroContextFilter</filter-name>
		<filter-class>ro.isdc.wro.http.WroContextFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>wroContextFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```


And how do I use it ?
---------------------
1. You need to declare the taglib at the beginning
```JSP
<%@ taglib uri="http://orange.com/wro/taglib" prefix="wro" %>
```

2. There are 4 available tags
    * `script`: generates script tags
    * `style`: generates stylesheet link tags
    * `asJsArray`: generates a JavaScript array
    * `optionalScript`: (optional) generates script tags which were
deemed necessary by wro4j-taglib (an example would the inclusion of
`less.js` when exploded LESS stylesheets are included). *NOTE: This
tag should always be the last one of the header*.

### `script` syntax
```JSP
<wro:script groupNames='group1,group2' exploded="true/false" />
```

* `groupNames` is a comma-delimited list of group names
* `exploded` is a boolean value that triggers the _exploded_ mode, which
means it will generate one script tag per individual file in the groups.
In the _normal_ mode, the tag will generate one script tag per group.

#### Output example
```HTML
<script src='/contextPath/wro/group1-1336929561758.js'></script>
<script src='/contextPath/wro/group2-1336930178802.js'></script>
```


### `style` syntax
```JSP
<wro:style groupNames='group1,group2' exploded="true/false" />
```

* `groupNames` is a comma-delimited list of group names
* `exploded` is a boolean value that triggers the _exploded_ mode, which
means it will generate one link tag per individual file in the groups.
In the _normal_ mode, the tag will generate one link tag per group.

#### Output example
```HTML
<link rel='stylesheet' href='/contextPath/wro/group1-1336930178909.css' />
<link rel='stylesheet' href='/contextPath/wro/group2-1336929562445.css' />
```


### `asJsArray` syntax

* `groupNames` is a comma-delimited list of group names
* `groupType` specifies if the tag should get `js` or `css` files.
* `exploded` is a boolean value that triggers the _exploded_ mode, which
means it will generate one element in the array per individual file in the groups.
In the _normal_ mode, the tag will generate one element per group.

#### Output example
```JavaScript
var files = ['\/contextPath\/wro\/group1-1336929561758.js','\/contextPath\/wro\/group2-1336930178802.js'];
```


### `optionalScript` syntax
```JSP
<wro:optionalScript />
```

#### Output example
```JavaScript
<script src='/contextPath/js/less-xxx.js'></script>
```


### Real example
A real example has been made available at this repository: https://github.com/Orange-OpenSource/test-release-wro4j-buildtime

