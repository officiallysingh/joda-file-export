package com.zcompany.example.jodaExport.ext;

import org.joda.convert.StringConvert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import com.xebia.util.export.ExportContext;
import com.xebia.util.export.FileExportContext;
import com.zcompany.example.domain.model.Timesheet;
import com.zcompany.example.domain.service.DataProvider;
import com.zcompany.example.jodaExport.ext.ExternalizedHeaderLabelsDumpCSVStrategy;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Ignore
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
