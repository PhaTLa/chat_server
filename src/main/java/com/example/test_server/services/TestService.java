package com.example.test_server.services;

import com.example.test_server.models.MessageDetail;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Log
@Service
public class TestService implements CommonService{

    List<MessageDetail> room;
    SimpleDateFormat formatter;

    @Value("${chat_message.format}")
    private String messageFormat;

    public TestService() {
        this.room = new ArrayList<>();
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @Override
    public Integer testImpl(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public String updateChat(MessageDetail message) {
        this.room.add(message);
        return String.format(this.messageFormat,
                message.getSender(),formatter.format(message.getTimeSent()),message.getContent());
    }

//    @Scheduled(fixedRate = 10000L,initialDelay = 5000L)
//    public void scheduledTask(){
//        log.info("=====================> "+ System.currentTimeMillis());
//    }

    public List<MessageDetail> getRoom() {
        return room;
    }
}
