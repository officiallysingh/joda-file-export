# Joda File Export
**Generic Java library based on Joda Beans and Spring Reactor to export/download or dump files in various formats such as CSV, Excel, PDF etc.**

_**<sub>Know more about [Joda Beans](https://www.joda.org/joda-beans/)!<sub>**_

_**<sub>Know more about [Spring Reactor](https://projectreactor.io/docs/core/release/reference/)!</sub>**_

## Why generic file export library?

In most of the applications there is a business requirement to export/download or dump files from a data set either queried from database or any other source. Mostly developers implement this feature by writing separate Java bean to File column mapping for each use case and format of file or somewhat home grown generic library using java reflection etc. Both of these approaches either lead to a lot of boilerplate code or performance issues with Java reflection.

The idea is to have the ability to access the properties of a Java bean by name without using reflection and iterating over the properties of file to write them in file columns one by one. Apart from ability to treat Java beans as a Collection, Joda Beans offers a lot more which is used to develop a generic file export library, which can be used by any Java based application. 
Another challenge is to have a mechanism to provide the metadata such as file header column names and extendable/customizable file writing strategies as exporting to any required format CSV, Excel, PDF etc. 
It can perform better if the data collection part and writing this data to file happen in separate threads (Producer/Consumer).

## Getting started

### Building joda-file-export

```bash
git clone https://github.com/officiallysingh/joda-file-export.git
cd joda-file-export
mvn clean install
```

### Adding joda-file-export to Your Maven Project

The library uses [Lombok](https://projectlombok.org/) and needs at least java 8 to compile and run. So you need to setup lombok in your favorite IDE

Add below maven depenencies into your project's pom.xml

```xml
		<properties>
			<java.version>1.8</java.version>
			<joda.beans.version>2.5.0</joda.beans.version>
			<joda.beans.maven.plugin.version>1.2.1</joda.beans.maven.plugin.version>
			<lombok.version>1.18.4</lombok.version>
			<reactor.version>3.1.12.RELEASE</reactor.version>
			<super.csv.version>2.4.0</super.csv.version>
			<apache.poi.version>3.17</apache.poi.version>
			<owasp.encode.version>1.2.1</owasp.encode.version>
		</properties>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>${lombok.version}</version>
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
			<version>${reactor.version}</version>
		</dependency>
```

Add following build plugins

```xml
	<build>
		<defaultGoal>spring-boot:run</defaultGoal>
		<testResources>
			<testResource>
				<directory>src/test/resources/</directory>
			</testResource>
			<testResource>
				<directory>src/test/features</directory>
			</testResource>
		</testResources>
		<plugins>
			<plugin>
				<groupId>org.joda</groupId>
				<artifactId>joda-beans-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>joda-beans-generate</id>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
							<version>${lombok.version}</version>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
				
				....
				...
				
		</plugins>

		<pluginManagement>
			<plugins>
				<plugin>
					<groupId>org.joda</groupId>
					<artifactId>joda-beans-maven-plugin</artifactId>
					<version>${joda.beans.maven.plugin.version}	</version>
				</plugin>
				
				....
				...
				
			</plugins>
		</pluginManagement>
```

#### Git source contain the library code into below two packages

* **com.xebia.util.export** Complete joda-file-export library code, copy this code into your source directory
* **com.zcompany.example** All example code elaborating the use of library, play with the examples to understand the usage

Code is bundled as Spring boot application, So simply run the application and browse through swagger at

http://localhost:8080/swagger-ui.html

Hit following URL to test file download

http://localhost:8080/api/jodaExport/collectionDownload?fileName=inter_bank_rates&downloadFileType=CSV

http://localhost:8080/api/jodaExport/reactiveDownload?fileName=rx_sample&downloadFileType=CSV

http://localhost:8080/api/jodaExport/withContextDownload?contextName=bank_specific&fileName=var_rates&downloadFileType=EXCEL

http://localhost:8080/api/jodaExport/withContextDownload?contextName=agent_specific&fileName=var_rates&downloadFileType=CSV

## Using joda-file-export

### Executing the file export

Downloading a CSV file with name sample in default context, writing blank if a property's value is found to be null. Using global `StringConvert` (`jodaConverter`), to convert properties to string while writing to file, which ever property class's type converter is registered with `jodaConverter`.

```java
@Autowired private StringConvert jodaConverter;
    ...
    .....
    
final HttpServletResponse response
......

ExportContext<InterBankRate> exportContext = FileExportContext.<InterBankRate>of(true)
                    .withJodaConverter(this.jodaConverter)
                    .exportAsCSV("sample", response)
                    .from(DataProvider.getInterBankRates());
exportContext.export();
```

### Defining POJO as Joda Bean

**The export candidate Java bean to be used as data container while writing data to file must be written in an opinionated manner making it as a Joda bean**, as follows. 

* POJO must be annotated with **@BeanDefinition** and extend either **Bean** or **ImmutableBean** class. 
* All properties whose data is to be written in file must be annotated with **@PropertyDefinition**. If any property is to be ignored while file writing then do not annotate the property with **@PropertyDefinition**, hence you can ignore any properties if required.
* All primitive properties or non Joda bean properties must be annotated with **@Export** annotation.
* A property which is itself a Joda bean must not be annotated with **@Export** annotation, but just with **@PropertyDefinition**
* The column name in the file where the POJO property would be exported must be given as **columnName** attribute of the **@Export**

```java
@BeanDefinition
@NoArgsConstructor
public class Cost implements Bean {

    @PropertyDefinition
    @Export(columnName = "Cost Sell")
    private BigDecimal sellValue;

    @PropertyDefinition
    @Export(columnName = "Cost Buy")
    private BigDecimal buyValue;
    
    
    //------------------------- AUTOGENERATED START -------------------------
    .....
    
    // Code generated by Joda generator maven plugin to make simple POJO feature rich.
    
    // Never write your custom code in this section otherwise would be removed by joda generator
    .....
    
    //-------------------------- AUTOGENERATED END --------------------------

```
* You may have some reusable DTOs being using in multiple export candidate Bean. So depending upon use case, you may want to have the given column name in composed class or may want to override them with different column names in another export candidate Bean. The same can be done by using **@ExportOverride** annotation. If single property column name is to be overridden then use **@ExportOverride** or if multiple properties of the composed bean needs to be overridden then use the companion **@ExportOverrides** annotation.

```java
@BeanDefinition
@NoArgsConstructor
public class ValueDateWise implements Bean {

    @PropertyDefinition
    private Cost margin;

    // @formatter:off
    @ExportOverrides({
            @ExportOverride(fieldName = "sellValue", export = @Export(columnName = "Settlement Rate Sell")),
            @ExportOverride(fieldName = "buyValue", export = @Export(columnName = "Settlement Rate Buy")) 
        }
    )
    // @formatter:on
    @PropertyDefinition
    private Cost settlement;
```

* The nested composed classes export metadata can also be overridden by providing the complete property path as follows. Ex. Overriding nested beans property metadata **fieldName = "currency.source"**

```java
@AllArgsConstructor(staticName = "of")
@BeanDefinition
public class ValueAtRisk implements ImmutableBean {

    @Getter
    private String id;

    // @formatter:off
    @ExportOverrides({
            @ExportOverride(fieldName = "currency.source", export = @Export(columnName = "Base Currency")),
            @ExportOverride(fieldName = "currency.target", export = @Export(columnName = "Target Currency")) 
        }
    )
    // @formatter:on
    @PropertyDefinition
    private final Exchange exchange;
```

* There could be some use cases where a class is being used in multiple scenarios but you may need different file structures exported. In such cases you can give a context name to given property as follows. While exporting the file you can optionally give a context name as an attribute of **@Export** annotation in **contexts**. If you need to exclude some properties in exported file in a specific context or scenario but include the same in another context or scenario then you can mark the property with a unique context name.

While export only the properties with no context given (default context) and specified context will be included in exported file whose context names matches the given Context in **ExportContext**. 
The properties with no given contexts i.e. in default context would always be exported but the properties given with one or multiple context names would only be exported if the export is executed in a given context.

```java
@AllArgsConstructor(staticName = "of")
@BeanDefinition
public class InterBankRate implements ImmutableBean {

    @Getter
    private String id;

    @PropertyDefinition
    private final Exchange exchange;

    @Export(columnName = "Agent", contexts = "agent_specific")
    @PropertyDefinition
    private final String agent;

    @Export(columnName = "Bank", contexts = {"bank_specific", "HDFC"})
    @PropertyDefinition
    private final String bank;

```

Exporting the file with above data bean in a context is as follows, So the following code would export agent property along with all other with default or no context properties but ignore bank property in the exported file

```java
FileExportContext.<ValueAtRisk>of("agent_specific", true).withJodaConverter(this.jodaConverter)
                    .exportAsCSV("context_based", response)
                    .from(DataProvider.getValueAtRiskRates()).export();
```

### File writing strategies

You may either want to download a file in a web application or just dump the same at a given location. These are just different strategies of file writing. By default there are two strategies bundled out of box with library i.e. **DownloadCSVFileStrategy.java** and **DownloadExcelFileStrategy.java** to download file either in CSV or Excel format. But you may need your own custom file writing strategy in following cases

* Need to export file in a different format such as PDF or RTF
* Need to customize excel sheet column styles
* Need to externalize file header column names in a properties file
* Rather than downloading the file, you may need to dump a file at a given location.
* By default the order of columns in file would be as per the order of properties defined in bean, in case you need to change the default order.
* Or any other reason as per your need

So you can define any new strategy of file writing simply by implementing **FileWriterStrategy** interface. 
One such custom strategy is given in the examples, **ExternalizedHeaderLabelsDumpCSVStrategy.java** as follows.

```java
@Slf4j
public class ExternalizedHeaderLabelsDumpCSVStrategy implements FileWriterStrategy {

    private ICsvListWriter csvWriter;

    private FileWriter fileWriter;

    private MessageSource messageSource;

    private ExternalizedHeaderLabelsDumpCSVStrategy(final String fileName, final String location,
            final MessageSource messageSource) {
        try {
            this.fileWriter = new FileWriter(
                    location + System.getProperty("file.separator") + fileName + "." + FileType.CSV.extension());
            this.csvWriter = new CsvListWriter(fileWriter, CsvPreference.STANDARD_PREFERENCE);
        } catch (IOException e) {
            throw ExportException.ioException(e);
        }
        this.messageSource = messageSource;
    }

    public static ExternalizedHeaderLabelsDumpCSVStrategy of(final String fileName, final String location,
            final MessageSource messageSource) {
        return new ExternalizedHeaderLabelsDumpCSVStrategy(fileName, location, messageSource);
    }

    @Override
    public void writeHeader(final String[] columnHeaders) {
        String[] extLabelColumnHeaders = Arrays.stream(columnHeaders)
                .map(messageKey -> this.messageSource.getMessage(messageKey, null, Locale.ENGLISH))
                .toArray(String[]::new);
        try {
            this.csvWriter.writeHeader(extLabelColumnHeaders);
        } catch (final IOException e) {
            throw ExportException.ioException(e);
        }
    }

    @Override
    public void writeRow(final List<String> rowData) {
        try {
            this.csvWriter.write(rowData);
        } catch (final IOException e) {
            throw ExportException.ioException(e);
        }
    }

    @Override
    public void cleanUp() {
        try {
            this.csvWriter.close();
            this.fileWriter.close();
        } catch (final IOException e) {
            log.error("Error while closing writers", e);
        }
    }
}
```

And then use above strategy in your file export as follows.

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomWriterStrategyTest {

    @Autowired
    private StringConvert jodaConverter;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void test() {

        final String fileName = "Custom_Header_test";

        final String location = "D:\\export_dump";

        ExportContext<Timesheet> exportContext = FileExportContext.<Timesheet>of().withJodaConverter(this.jodaConverter)
                .export(ExternalizedHeaderLabelsDumpCSVStrategy.of(fileName, location, this.messageSource))
                .from(DataProvider.getTimesheets());

        exportContext.export();
    }
}
```

The order of columns in the file can be customized as per your needs by providing a custom File writer strategy and reordering the columns headers and row data in following methods of the strategy implementation.

```java
@Override
public void writeHeader(final String[] columnHeaders) {
// Change the order of column's headers by sorting the columnHeaders array as per your sorting logic
}

@Override
public void writeRow(final List<String> rowData) {
// Change the order of column's data by sorting the rowData list as per your sorting logic
}
```

### Type converters

The properties of bean are written in file in String format only. So it is a very basic requirement to convert the bean properties to a string in a particular format. For example you may need to format date time to a specific format while writing to file. So you can define your custom type converters as follows. All such type converters then needs to be registered in Joda StringConvert as follows. The library then automatically converts the data in a bean property depending upon the Class of property. For example by defining following type converter and registering it to global **StringConert** joda converter, each property of **BigDecimal** type would be precised to 8 decimal points in exported file.

```java
public class BigDecimalJodaConverter implements TypedStringConverter<BigDecimal> {

    @Override
    public String convertToString(final BigDecimal value) {
        return String.format("%." + RateValue.DECIMAL_POINTS_8 + "f", value);
    }

    @Override
    public BigDecimal convertFromString(final Class<? extends BigDecimal> cls, String str) {
        return new BigDecimal(str);
    }

    @Override
    public Class<?> getEffectiveType() {
        return BigDecimal.class;
    }
}
```

Similar to above type converter you may have others also. After defining all such type converters register them in global Joda converter **StringConvert** as follows. **ExportContext** expects an instance of **StringConvert** while exporting the file. **It is recommended to have a singleton instance of StringConvert**, but in case you need different converters for same class such as in one case you want to write Boolean value as YES/NO but in other case ENABLED/DISABLED, then you may need to create multiple instances of StringConvert and use respective instance as per the conversion required. 
You can find following type converters in source code.

```java
@Configuration
public class JodaConfig {

    @Bean
    public StringConvert jodaConverter() {
        StringConvert stringConvert = StringConvert.create();
        stringConvert.register(BigDecimal.class, new BigDecimalJodaConverter());
        stringConvert.register(Boolean.class, new BooleanJodaConverter());
        stringConvert.register(CurrencyUnit.class, new CurrencyUnitJodaConverter());
        stringConvert.register(ZonedDateTime.class, new ZonedDateTimeJodaConverter());
        return stringConvert;
    }
}
```

### Going the Reactive way

Normally the data to export is fetched from some database. If data set is small then you can just fetch a collection and pass the collection to export API. Or if the data set is large you may need to fetch the data page by page or in batches and sequentially push the data into a **Flux** using any of programmatically generating **Flux** strategy. There are some databases like Postgres or MongoDB, which have native reactive supporting JDBC drivers and you can simply get a **Flux** from their Spring Data repository. 

Or you can **programmatically push your data into Flux** and pass the same to Export API as follows.

```java
Iterator<InterBankRate> itr = DataProvider.getInterBankRates().iterator();
        Flux<InterBankRate> dataStream = Flux.generate(() -> itr, (state, sink) -> {
            if (state.hasNext()) {
                sink.next(state.next());
            } else {
                sink.complete();
            }
            return state;
        });

        FileExportContext.<InterBankRate>of(true).withJodaConverter(this.jodaConverter)
                .exportAsCSV(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), response).from(dataStream)
                .export();
```

If there are multiple data sources such as some Queue, API and DB etc., then you can you Spring Reactor’s thread safe **EmitterProcessor** and **FluxSink** as follows.

```java
EmitterProcessor<RateRecord> emitterFlux = EmitterProcessor.create();
  
  .....
  ....
   
FluxSink<RateRecord> rateSink = emitterFlux .sink();

....

// Push data into the Flux by calling the next(data) on sink as follows

RateRecord record = new ......
rateSink.next(record);
...

FileExportContext.<InterBankRate>of().withJodaConverter(this.jodaConverter)
                    .exportAsExcel(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), "Sample sheet",
                            response)
                    .from(emitterFlux).export();

```
The **EmitterProcessor** is thread safe so you can push data into **FluxSink** simultaneously,the same would be available in the **Flux** for consumption. File writing happens in single thread to retain the order of rows.
**As with Spring Reactor's Flux, nothing happens until  the Flux is subscribed**. **Hence the export is only started once you call the export() method on ExportContex**.

### Error handling
There could be **IO** related exceptions or **Joda mis-configuration** of beans. In all error scenarios, all exceptions are wrapped into a single unchecked exception with different error codes and description message. In case any exception occurs the same is logged. You may get the error code and description from logs and take the required measures but you can not catch any of the exceptions as they occur in separate thread while processing the **Flux**.

```java
public class ExportException extends RuntimeException {

    public enum ExportExceptionType implements ErrorCodeType {

        //@formatter:off
        IO_EXCEPTION("IO exception"),
        EMPTY_DATA_SET_EXCEPTION("Empty data set"),
        INVALID_META_DATA_EXCEPTION("Invalid meta data");
        //@formatter:on

		.....
		....
    }

	.....
	....
	
    public ErrorCodeType exceptionType() {
        return this.exceptionType;
    }

}
```

### Collection support

Ideally the exported file would have a fixed structure. In case the data is in different structure, you need to normalize the data as per the model to be exported. As of now no collection is supported in POJO bean except Map, that too with restrictions. Even its highly unlikely to have a collection in the file export candidate bean, because in that case you may not have a fixed number of columns in the file, which is almost always required in file export use cases.
For example if data bean is supposed to have a list then you may not be able to have a fixed number of columns in the file as list content may vary for different records.

A Map collection is supported in file export candidate bean with following measures. 

* It is recommended be used only with a fixed number of entries otherwise the number of columns in file would be too many
* Both Key and Value of the map must be custom class object.
* The Key class must implement **Distinguishable** interface, overriding two methods.

```java
public interface Distinguishable {
    
    public String label();

    public String descriminator();
}
```

* The **descriminator()** method must return a unique String for each Key in the Map.
* The **label()** method must return a String to be prefixed in all columns of corresponding Key's value columns.
* The value must be a Joda bean, refer to below class for reference.

```java
@AllArgsConstructor(staticName = "of")
@BeanDefinition
public class ValueAtRisk implements ImmutableBean {

    .....
    .......

    @PropertyDefinition
    private final Map<VDWType, ValueDateWise> valueDateWises;
```
* The exported file would contain data for each Map entry's value object sequentially, Key's label would be used as a header column prefix to differentiate between the columns for different entries. Refer to below exported file for one of the examples in source code, where export candidate bean contains a **Map** with four entries where Key's label() method returns Cash, Tom, Spot and Future for each key respectively.

![Sample exported file screenshot](https://github.com/officiallysingh/joda-file-export/blob/master/Sample_File_With_Map.jpg)

### Known issues

The export candidate bean may also compose other beans till any depth. But if while file export any of the composed joda bean is found as null, then the export would fail. So you need to make sure none of the joda bean objects in the data set is null. 
The primitives and non Joda beans properties can obviously be null. So as given in the examples **Cost.java** is a joda bean composed in multiple export candidate classes, the value of Cost should never be null. If you do not have any Cost value then simply initialize it with null sell and buy values.

