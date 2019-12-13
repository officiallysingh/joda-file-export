# Joda File Export
Generic Java library based on Joda Beans and Spring reactor to export/download or dump files in various formats such as CSV, Excel etc.

_**<sub>Know more about [Joda Beans](https://www.joda.org/joda-beans/)!<sub>**_

_**<sub>Know more about [Spring Reactor](https://projectreactor.io/docs/core/release/reference/)!</sub>**_

## Why generic file export library?

In most of the applications there is a business requirement to export/download or dump files from a data set either queried from database or any other source. Mostly developers implement this feature by writing separate Java bean to File column mapping for each use case and format of file or somewhat home grown generic library using java reflection etc. Both of these approaches either lead to a lot of boilerplate code or performance issues with Java reflection.

The idea is to have the ability to access the properties of a Java bean by name without using reflection and iterating over the properties of file to write them in file columns one by one. Apart from ability to treat Java beans as a Collection, Joda Beans offers a lot more which is used to develop a generic file export library, which can be used by any Java based application. 
Another challenge is to have a mechanism to provide the metadata such as file header column names and extendable/customisable file writing strategies such exporting to any required format CSV, Excel, PDF etc. It can perform better if the data collection part and writing this data to file happen in separate threads (Producer/Consumer).

## Getting started

### Building joda-file-export

```bash
git clone https://github.com/officiallysingh/joda-file-export.git
cd joda-file-export
mvn clean install
```

### Adding joda-file-export to Your Maven Project

Add below maven depenencies into your project's pom.xml

```xml
		<properties>
			<java.version>1.8</java.version>
			<joda.beans.version>2.5.0</joda.beans.version>
			<joda.beans.maven.plugin.version>1.2.1</joda.beans.maven.plugin.version>
			<lombok.version>1.18.4</lombok.version>
			<joda.beans.version>2.5.0</joda.beans.version>
			<joda.beans.maven.plugin.version>1.2.1</joda.beans.maven.plugin.version>
			<super.csv.version>2.4.0</super.csv.version>
			<apache.poi.version>3.17</apache.poi.version>
			<owasp.encode.version>1.2.1</owasp.encode.version>
		</properties>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.joda</groupId>
			<artifactId>joda-beans</artifactId>
			<version>${joda.beans.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>${apache.poi.version}</version>
		</dependency>
		<dependency>
			<groupId>net.sf.supercsv</groupId>
			<artifactId>super-csv-dozer</artifactId>
			<version>${super.csv.version}</version>
		</dependency>
		<dependency>
			<groupId>net.sf.supercsv</groupId>
			<artifactId>super-csv</artifactId>
			<version>${super.csv.version}</version>
		</dependency>
		<dependency>
			<groupId>org.owasp.encoder</groupId>
			<artifactId>encoder</artifactId>
			<version>${owasp.encode.version}</version>
		</dependency>
		<dependency>
			<groupId>io.projectreactor</groupId>
			<artifactId>reactor-core</artifactId>
		</dependency>
```

Git source contain the library code into below two packages package com.xebia.util.export

* **com.xebia.util.export** Complete joda-file-export library code
* **com.zcompany.example** All example code elaborating the use of library


