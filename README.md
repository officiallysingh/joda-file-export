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


#### Git source contain the library code into below two packages

* **com.xebia.util.export** Complete joda-file-export library code, copy this code into your source directory
* **com.zcompany.example** All example code elaborating the use of library, play with the examples to understand the usage

#### Git source contain the library code into below two packages
Code is bundled as Spring boot based application, So simply run the application and browse through swagger at
http://localhost:8080/swagger-ui.html

Hit following URL to test file download

http://localhost:8080/api/jodaExport/collectionDownload?fileName=inter_bank_rates&downloadFileType=CSV

http://localhost:8080/api/jodaExport/reactiveDownload?fileName=rx_sample&downloadFileType=CSV

http://localhost:8080/api/jodaExport/withContextDownload?contextName=bank_specific&fileName=var_rates&downloadFileType=EXCEL

http://localhost:8080/api/jodaExport/withContextDownload?contextName=agent_specific&fileName=var_rates&downloadFileType=CSV

## Using joda-file-export

### Executing the file export

Downloading a CSV file with name sample, writing blank if a property's value is found to be null. Using global StringConvert (jodaConverter), to convert properties to string while writing to file, which ever property class's type converter is registered with jodaConverter.

```java
@Autowired private StringConvert jodaConverter;
    ...
    .....
    
final HttpServletResponse response
......

ExportContext<InterBankRate> exportContext = FileExportContext.<InterBankRate>of(true)
                    .withJodaConverter(this.jodaConverter)
                    .downloadAsCSV("sample", response)
                    .from(DataProvider.getInterBankRates());
exportContext.export();
```

### Defining POJO as Joda Bean

The Java bean to be used as data container must be written in an opinionated manner making it as a Joda bean, as follows. 

* POJO must be annotated with @BeanDefinition and extend either Bean or ImmutableBean class. 
* All properties whose data is to be written in file must be annotated with @PropertyDefinition. If any property is to be ignored while file writing then do not annotate the property with @PropertyDefinition, so you can ignore any properties if required.
* All primitive properties or non Joda bean properties must be annotated with @Download annotation.
* A property which is itself a Joda bean must not be annotated with @Download annotation, but just with @PropertyDefinition
* The column name of the file where the POJO property would be exported must be given as columnName attribute of the @Download
* The optional context name in which the property value would be exported must be given as attribute of @Download annotation. If the property is always to be exported than no need to specify an context name

```java
@BeanDefinition
@NoArgsConstructor
public class Cost implements Bean {

    @PropertyDefinition
    @Download(columnName = "Cost Sell")
    private BigDecimal sellValue;

    @PropertyDefinition
    @Download(columnName = "Cost Buy")
    private BigDecimal buyValue;
    
    
    //------------------------- AUTOGENERATED START -------------------------
    .....
    
    // Code generated by Joda generator maven plugin to make simple POJO feature rich.
    
    // Never write your custom code in this section otherwise would be removed by joda generator
    .....
    
    //-------------------------- AUTOGENERATED END --------------------------

```
