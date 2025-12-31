/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.parillume.external.chat.model.ChatTopic;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class ChatTopicConverter implements AttributeConverter<ChatTopic, String> {

    @Override
    public String convertToDatabaseColumn(ChatTopic chatTopic) {
        return chatTopic.name();
    }

    @Override
    public ChatTopic convertToEntityAttribute(String chatTopicStr) {
        return ChatTopic.valueOf(chatTopicStr.toUpperCase());
    }
}
