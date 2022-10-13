package com.example.test_server.controllers;

import com.example.test_server.constants.CommonConstant;
import com.example.test_server.models.MathParamsRequest;
import com.example.test_server.models.MessageDetail;
import com.example.test_server.services.TestService;
import com.example.test_server.util.FileUploadUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import javax.servlet.ServletContext;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.ByteArrayInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log
@RestController
@RequestMapping("/test")
@CrossOrigin(value = "*")
public class TestController {

    private TestService testService;
    private MessageSource messageSource;
    private ServletContext servletContext;

    @Autowired
    public TestController(TestService testService, MessageSource messageSource, ServletContext servletContext) {
        this.testService = testService;
        this.messageSource = messageSource;
        this.servletContext = servletContext;
    }

    @GetMapping("/test-call")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, String> testCall(@RequestParam(name = "lang", required = false) Locale locale,
                                        @RequestHeader(name = "Accept-Language", required = false) Locale langCode) {
        Map<String, String> map = new HashMap<>();
        map.put("response",messageSource.getMessage("test.message.default_message", null,
                locale!=null?locale:langCode));
        return map;
    }

    @PostMapping("/add_two_number")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, String> addTwoNumber(@RequestParam(name = "lang", required = false) Locale locale,
                                            @RequestBody MathParamsRequest request){
        Map<String, String> map = new HashMap<>();
        Integer sum = testService.testImpl(request.getNumberA(), request.getNumberB());
        String message = messageSource.getMessage("test.message.formatted_clause",
                new Object[] {request.getNumberA(), request.getNumberB(), sum},
                locale);
        map.put("result-is",message);
        return map;
    }

    @GetMapping("/chat_stream")
    public Flux<ServerSentEvent<MessageDetail>> chatStream(){
        MessageDetail mesDetail;
        if(testService.getRoom().size()>0){
            mesDetail = testService.getRoom().get(testService.getRoom().size()-1);
        }else{
            mesDetail = new MessageDetail();
        }
        return Flux.just(mesDetail)
                .map(sequence -> ServerSentEvent.<MessageDetail>builder()
                        .data(mesDetail)
                        .build());
    }

    @PostMapping("/sent_message")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDetail receiveMessage(@RequestBody MessageDetail mesDetail){
        String message = testService.updateChat(mesDetail);
        if(!StringUtils.hasText(message)){
            throw new RuntimeException();
        }
        return mesDetail;
    }

    @PostMapping("/upload-file")
    @ResponseBody
    public MessageDetail uploadFile(@RequestParam("file") MultipartFile file,
                                    @RequestParam("sender") String sender){

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension;
        if(FileUploadUtil.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()).toLowerCase()).isPresent()){
            fileExtension = FileUploadUtil.getFileExtension(file.getOriginalFilename().toLowerCase()).get();
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"File is invalid");
        }
        try {
            String fileMediaType = CommonConstant.IMG_EXTENSIONS.contains(fileExtension)?
                    CommonConstant.MESSAGE_TYPE_MEDIA
                    :
                    CommonConstant.MESSAGE_TYPE_DOC;

            String uploadDir = CommonConstant.IMG_EXTENSIONS.contains(fileExtension)?
                    CommonConstant.UPLOAD_DIR_PHOTO
                    :
                    CommonConstant.UPLOAD_DIR_OTHER_FILE;

            String messageContent = FileUploadUtil.saveFile(fileName,uploadDir,file);
            MessageDetail messagePhoto =
                    new MessageDetail(messageContent,sender,fileMediaType,new Date());
            String message = testService.updateChat(messagePhoto);
            if(!StringUtils.hasText(message)){
                throw new RuntimeException();
            }
            return messagePhoto;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Could not create file - ", e);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam(value = "fileName") String fileName) throws IOException {
        MediaType mediaType = FileUploadUtil.getMediaTypeOfFile(this.servletContext,fileName);
        log.info(mediaType.getType());
        Path path = Paths.get(CommonConstant.UPLOAD_DIR_OTHER_FILE+"/"+fileName);
        byte[] data = Files.readAllBytes(path);
        ByteArrayResource response = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(response);
    }
}
