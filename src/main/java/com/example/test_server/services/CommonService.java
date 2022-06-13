package com.example.test_server.services;

import com.example.test_server.models.MessageDetail;

public interface CommonService {
    Integer testImpl(Integer a, Integer b);
    String updateChat(MessageDetail message);
}
