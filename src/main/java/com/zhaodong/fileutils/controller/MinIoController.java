package com.zhaodong.fileutils.controller;


import com.zhaodong.fileutils.base.ResponseMessage;
import com.zhaodong.fileutils.base.Status;
import com.zhaodong.fileutils.exceptions.InvalidParamException;
import com.zhaodong.fileutils.exceptions.ServiceException;
import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/minio")
//@ConditionalOnProperty(prefix = "minio",name = "controller",havingValue = "true")
@ConditionalOnClass(io.minio.MinioClient.class)
public class MinIoController {
    @Autowired
    private MinioClient minioClient;
    @Value("${file.bucket}")
    private String bucket;
    private DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Logger LOG= LoggerFactory.getLogger(MinIoController.class);


    @PostMapping("/upload")
    public ResponseMessage upload(@RequestParam("files") MultipartFile[] files)  {
        String now = LocalDate.now().format(dtf);
        String[] filePaths=new String[files.length];
        for(int i=0;i<files.length;i++) {
            String fileName = UUID.randomUUID().toString() + files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
            String pathName =  now + "/" + fileName;

            try {
                // Check if the bucket already exists.
                boolean isExist = minioClient.bucketExists(bucket);
                if(!isExist) {
                    minioClient.makeBucket(bucket);
                }

                // Upload the zip file to the bucket with putObject
                Map<String,String> param=new HashMap<>();
                param.put("cache-control","max-age=360000");
                minioClient.putObject(bucket,pathName, files[i].getInputStream(),Long.valueOf(files[i].getInputStream().available()),param,null,files[i].getContentType());
                filePaths[i]="/"+bucket+"/"+pathName;
            } catch (Exception e) {
                LOG.error("文件上传错误:{}",e.getMessage());
                throw new ServiceException(Status.INNER_SERVER_ERROR);
            }
        }
        return ResponseMessage.ofSuccess(filePaths);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam  String filePath, @RequestParam String fileName)  {

        try (InputStream is = minioClient.getObject(bucket, filePath.substring(("/"+bucket+"/").length()));) {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            header.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] bytes = StreamUtils.copyToByteArray(is);
            return new ResponseEntity<>(bytes,
                    header, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof ErrorResponseException){
                ErrorResponseException exception = (ErrorResponseException) e;
                ErrorCode errorCode = exception.errorResponse().errorCode();
                if("NO_SUCH_OBJECT".equals(errorCode.name())){
                    throw new InvalidParamException("filePath 文件路径没有找到！");
                }

            }
            LOG.error("文件下载错误:{}",e.getMessage());
            throw new ServiceException(Status.INNER_SERVER_ERROR);
        }

    }
}
