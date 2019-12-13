package com.zcompany.example.controller;

import static org.springframework.http.MediaType.*;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.joda.convert.StringConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xebia.util.export.ExportContext;
import com.xebia.util.export.FileExportContext;
import com.xebia.util.export.FileType;
import com.zcompany.example.domain.model.InterBankRate;
import com.zcompany.example.domain.model.ValueAtRisk;
import com.zcompany.example.domain.service.DataProvider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import reactor.core.publisher.Flux;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Joda Export", description = "Joda export testing APIs", tags = "Joda Export")
@RestController
@RequestMapping("/api/jodaExport")
public class FileExportController {

    @Autowired
    private StringConvert jodaConverter;

    @GetMapping("collectionDownload")
    // @formatter:off
    @ApiOperation(nickname = "", 
        consumes = APPLICATION_JSON_VALUE, 
        produces = APPLICATION_JSON_VALUE,
        value = "", 
        response = Void.class, 
        notes = "")
    @Valid
    public void collectionDownload(
            @ApiParam(type = "fileName", value = "Download File name, if not provided default will be taken")
            @RequestParam(value = "fileName", required = false) final String fileName,
            @ApiParam(type = "downloadFileType", value = "Download File type as CSV or EXCEL", allowableValues = "CSV,EXCEL", required = true)
            @RequestParam(value = "downloadFileType", required = true) @NotNull final FileType downloadFileType,
            @ApiIgnore final HttpServletResponse response) {
     // @formatter:on
        if (downloadFileType.isCSV()) {
            ExportContext<InterBankRate> exportContext = FileExportContext.<InterBankRate>of(true)
                    .withJodaConverter(this.jodaConverter)
                    .downloadAsCSV(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), response)
                    .from(DataProvider.getInterBankRates());
            exportContext.export();
        } else {
            FileExportContext.<InterBankRate>of().withJodaConverter(this.jodaConverter)
                    .downloadAsExcel(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), "Sample sheet",
                            response)
                    .from(DataProvider.getInterBankRates()).export();
        }
    }

    @GetMapping("reactiveDownload")
    // @formatter:off
    @ApiOperation(nickname = "", 
        consumes = APPLICATION_JSON_VALUE, 
        produces = APPLICATION_JSON_VALUE,
        value = "", 
        response = Void.class, 
        notes = "")
    @Valid
    public void reactiveDownload(
            @ApiParam(type = "fileName", value = "Download File name, if not provided default will be taken")
            @RequestParam(value = "fileName", required = false) final String fileName,
            @ApiParam(type = "downloadFileType", value = "Download File type as CSV or EXCEL", allowableValues = "CSV,EXCEL", required = true)
            @RequestParam(value = "downloadFileType", required = true) @NotNull final FileType downloadFileType,
            @ApiIgnore final HttpServletResponse response) {
     // @formatter:on

        Iterator<InterBankRate> itr = DataProvider.getInterBankRates().iterator();
        Flux<InterBankRate> dataStream = Flux.generate(() -> itr, (state, sink) -> {
            if (state.hasNext()) {
                sink.next(state.next());
            } else {
                sink.complete();
            }
            return state;
        });

        if (downloadFileType.isCSV()) {
            FileExportContext.<InterBankRate>of(true).withJodaConverter(this.jodaConverter)
                    .downloadAsCSV(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), response)
                    .from(dataStream).export();
        } else {
            FileExportContext.<InterBankRate>of().withJodaConverter(this.jodaConverter)
                    .downloadAsExcel(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), "Sample sheet",
                            response)
                    .from(dataStream).export();
        }
    }

    @GetMapping("withContextDownload")
    // @formatter:off
    @ApiOperation(nickname = "", 
        consumes = APPLICATION_JSON_VALUE, 
        produces = APPLICATION_JSON_VALUE,
        value = "", 
        response = Void.class, 
        notes = "")
    @Valid
    public void reactiveDownload(
            @ApiParam(type = "contextName", value = "The contextName for file download", allowableValues = "bank_specific,agent_specific")
            @RequestParam(value = "contextName", required = false) final String contextName,
            @ApiParam(type = "fileName", value = "Download File name, if not provided default will be taken")
            @RequestParam(value = "fileName", required = false) final String fileName,
            @ApiParam(type = "downloadFileType", value = "Download File type as CSV or EXCEL", allowableValues = "CSV,EXCEL", required = true)
            @RequestParam(value = "downloadFileType", required = true) @NotNull final FileType downloadFileType,
            @ApiIgnore final HttpServletResponse response) {
     // @formatter:on

        if (downloadFileType.isCSV()) {
            FileExportContext.<ValueAtRisk>of(contextName, true).withJodaConverter(this.jodaConverter)
                    .downloadAsCSV(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), response)
                    .from(DataProvider.getValueAtRiskRates()).export();
        } else {
            FileExportContext.<ValueAtRisk>of(contextName, true).withJodaConverter(this.jodaConverter)
                    .downloadAsExcel(StringUtils.isEmpty(fileName) ? "Sample" : fileName.trim(), "Sample sheet",
                            response)
                    .from(DataProvider.getValueAtRiskRates()).export();
        }
    }
}
