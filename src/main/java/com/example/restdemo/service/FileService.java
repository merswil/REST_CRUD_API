package com.example.restdemo.service;

import com.example.restdemo.entity.GeologicalClass;
import com.example.restdemo.entity.Section;
import com.example.restdemo.entity.AsyncJob;
import com.example.restdemo.repository.SectionRepository;
import com.example.restdemo.repository.JobRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private JobRepository jobRepository;

    public byte[] compressFile(byte[] file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(file);
        gzipOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] decompressFile(byte[] compressedFileBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedFileBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = gzipInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        gzipInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Async
    public AsyncJob importFile(byte[] fileBytes) throws IOException {
        final AsyncJob[] job = {new AsyncJob()};
        job[0].setType(AsyncJob.JobType.IMPORT);
        job[0].setStatus(AsyncJob.JobStatus.IN_PROGRESS);
        job[0].setFile(compressFile(fileBytes));
        job[0] = jobRepository.save(job[0]);

        CompletableFuture.runAsync(() -> {
            try {
                Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes));
                Sheet sheet = workbook.getSheetAt(0);
                int numRows = 0;
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    Section section = new Section();
                    section.setName(row.getCell(0).getStringCellValue());
                    List<GeologicalClass> geologicalClasses = new ArrayList<>();
                    for (int i = 1; i < row.getLastCellNum(); i += 2) {
                        GeologicalClass geologicalClass = new GeologicalClass();
                        geologicalClass.setName(row.getCell(i).getStringCellValue());
                        geologicalClass.setCode(row.getCell(i + 1).getStringCellValue());
                        geologicalClass.setSection(section);
                        geologicalClasses.add(geologicalClass);
                    }
                    section.setGeologicalClasses(geologicalClasses);
                    sectionRepository.save(section);
                    numRows++;
                }
                job[0].setStatus(AsyncJob.JobStatus.DONE);
                job[0].setResult("Imported " + numRows + " rows");
            } catch (IOException e) {
                job[0].setStatus(AsyncJob.JobStatus.ERROR);
                job[0].setResult(e.getMessage());
            }
            job[0] = jobRepository.save(job[0]);
        });
        return job[0];
    }


    @Async
    public AsyncJob exportFile() {
        final AsyncJob[] job = {new AsyncJob()};
        job[0].setType(AsyncJob.JobType.EXPORT);
        job[0].setStatus(AsyncJob.JobStatus.IN_PROGRESS);
        job[0] = jobRepository.save(job[0]);
        System.out.println("Export job created with ID: " + job[0].getId());

//        CompletableFuture.runAsync(() -> {
            try {
                List<Section> sections = sectionRepository.findAll();
                System.out.println("Retrieved " + sections.size() + " sections for export");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("sections");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Section name");
                headerRow.createCell(1).setCellValue("Class 1 name");
                headerRow.createCell(2).setCellValue("Class 1 code");
                headerRow.createCell(3).setCellValue("Class 2 name");
                headerRow.createCell(4).setCellValue("Class 2 code");
                headerRow.createCell(5).setCellValue("Class M name");
                headerRow.createCell(6).setCellValue("Class M code");
                int rowNum = 1;
                for (Section section : sections) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(section.getName());
                    int colNum = 1;
                    for (GeologicalClass geologicalClass : section.getGeologicalClasses()) {
                        row.createCell(colNum++).setCellValue(geologicalClass.getName());
                        row.createCell(colNum++).setCellValue(geologicalClass.getCode());
                    }
                }
                System.out.println("Created Excel file with " + rowNum + " rows");

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                workbook.close();
                job[0].setFile(outputStream.toByteArray());
                job[0].setStatus(AsyncJob.JobStatus.DONE);
                job[0].setResult("Exported " + sections.size() + " sections");
                System.out.println("Export job completed successfully");
            } catch (IOException e) {
                job[0].setStatus(AsyncJob.JobStatus.ERROR);
                job[0].setResult(e.getMessage());
                System.out.println("Error in export job: " + e.getMessage());
            }
            job[0] = jobRepository.save(job[0]);
//        });
        return job[0];
    }
}
