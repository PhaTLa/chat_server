package com.example.test_server.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDetail {
    String content;
    String sender;
    String contentType;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    Date timeSent;
}
