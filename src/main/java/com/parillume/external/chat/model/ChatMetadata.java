/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.external.chat.model;

import com.parillume.model.internal.AssessmentType;
import com.parillume.util.db.AssessmentTypesConverter;
import com.parillume.util.db.ChatTopicConverter;
import com.parillume.util.db.StringListConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Entity
@Table(name = "chatmetadata")
public class ChatMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "queryDateMs")
    private long queryDateMs;
    
    @Column(name = "chatterUserId")
    private String chatterUserId;
    
    @Convert(converter = StringListConverter.class)
    @Column(name = "targetUserIds")
    private List<String> targetUserIds = new ArrayList<>();
    
    @Column(name = "chatQuery")
    private String chatQuery;
    
    /**
     * For auto-generated chats
     */
    @Convert(converter = ChatTopicConverter.class)
    @Column(name = "chatTopic")
    private ChatTopic chatTopic;
    
    /**
     * For auto-generated chats
     */
    @Convert(converter = AssessmentTypesConverter.class)
    @Column(name = "assessmentTypes")
    private List<AssessmentType> assessmentTypes = new ArrayList<>();
    
    public ChatMetadata() {}
    
    /**
     * For custom chats, wherein the user defined the chatQuery
     */
    public ChatMetadata(String chatterUserId, List<String> targetUserIds) {
        this(chatterUserId, targetUserIds, null, null, null);
    }
    
    public ChatMetadata(String chatterUserId, String targetUserId, 
                        ChatTopic chatTopic, AssessmentType assessmentType) {
        this(chatterUserId, Arrays.asList(targetUserId), null, chatTopic, assessmentType);
    }
    
    public ChatMetadata(String chatterUserId, List<String> targetUserIds,
                        String chatQuery, ChatTopic chatTopic,
                        AssessmentType... assessmentTypes) {
        setChatterUserId(chatterUserId);
        setTargetUserIds(new ArrayList(targetUserIds));
        setChatQuery(chatQuery);
        setChatTopic(chatTopic);
        if(assessmentTypes != null && assessmentTypes.length > 0)
            setAssessmentTypes( new ArrayList(Arrays.asList(assessmentTypes)) );
        
        setQueryDateMs(System.currentTimeMillis());
    }

    /**
     * We don't compare queryDateMs
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatMetadata other = (ChatMetadata) obj;
        if (!Objects.equals(this.chatterUserId, other.chatterUserId)) {
            return false;
        }
        if (!Objects.equals(this.chatQuery, other.chatQuery)) {
            return false;
        }
        if (!Objects.equals(this.targetUserIds, other.targetUserIds)) {
            return false;
        }
        return this.chatTopic == other.chatTopic;
    }
    
}
