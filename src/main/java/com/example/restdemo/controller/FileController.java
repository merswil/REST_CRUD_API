package com.example.restdemo.controller;

import com.example.restdemo.entity.AsyncJob;
import com.example.restdemo.service.FileService;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.restdemo.repository.JobRepository;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private JobRepository jobRepository;

    @PostMapping("/import")
    public ResponseEntity<AsyncJob> importFile(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        AsyncJob job = fileService.importFile(fileBytes);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/import/{id}")
    public ResponseEntity<AsyncJob> getImportJob(@PathVariable Long id) {
        AsyncJob job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return ResponseEntity.ok(job);
    }

    @GetMapping("/export")
    public ResponseEntity<AsyncJob> exportFile() {
        AsyncJob job = fileService.exportFile();
        return ResponseEntity.ok(job);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<AsyncJob> getExportJob(@PathVariable Long id) {
        AsyncJob job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return ResponseEntity.ok(job);
    }

    @GetMapping("/export/{id}/file")
    public ResponseEntity<byte[]> downloadExportedFile(@PathVariable Long id) {
        AsyncJob job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getStatus().equals(AsyncJob.JobStatus.DONE)) {
            throw new IllegalStateException("Export job is still in progress");
        }
        byte[] fileBytes = job.getFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("sections.xlsx").build());
        return ResponseEntity.ok().headers(headers).body(fileBytes);
    }
}
