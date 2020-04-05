package com.zhaodong.fileutils.controller;

import com.zhaodong.fileutils.base.ResponseMessage;
import com.zhaodong.fileutils.base.Status;
import com.zhaodong.fileutils.exceptions.InvalidParamException;
import com.zhaodong.fileutils.exceptions.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/file")
//@ConditionalOnProperty(prefix = "file",name = "controller",havingValue = "true")
@ConditionalOnMissingClass("io.minio.MinioClient")
public class FileController {
    private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Logger LOG= LoggerFactory.getLogger(FileController.class);
    @PostMapping("/upload")
    public ResponseMessage upload(@RequestParam("files") MultipartFile[] files) throws FileNotFoundException {
        String now = LocalDate.now().format(dtf);
        String[] filePaths=new String[files.length];
        for(int i=0;i<files.length;i++) {
            String fileName = UUID.randomUUID().toString() + files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
            String pathName = System.getProperty("user.home") + "/upload/" + now + "/" + fileName;

            try {
                File newfile = new File(pathName);
                if (!newfile.getParentFile().exists()) {
                    newfile.mkdirs();
                }
//            Files.copy(io, Paths.get(pathName));
                files[i].transferTo(newfile);
                filePaths[i]="/upload/"+now+"/"+fileName;
            } catch (IOException e) {
                LOG.error("文件上传错误:{}",e.getMessage());
                throw new ServiceException(Status.INNER_SERVER_ERROR);
            }
        }
        return ResponseMessage.ofSuccess(filePaths);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam  String filePath,@RequestParam String fileName)  {

        if(Files.exists(Paths.get(System.getProperty("user.home")+filePath))) {
            byte[] bytes = new byte[0];
            HttpHeaders header = new HttpHeaders();
            try {
                header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                header.set(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + URLEncoder.encode( fileName, "UTF-8"));
                bytes = Files.readAllBytes(Paths.get(System.getProperty("user.home") + filePath));
            } catch (IOException e) {
                LOG.error("文件下载错误:{}",e.getMessage());
                throw new ServiceException(Status.INNER_SERVER_ERROR);
            }
            return new ResponseEntity<>(bytes,
                    header, HttpStatus.OK);
        }else {
            throw new InvalidParamException("filepath 这个文件没有找到!");
        }
    }
}
