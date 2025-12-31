/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.util.external.chat;

import com.parillume.external.chat.model.ChatMetadata;
import com.parillume.external.chat.model.ChatTopic;
import com.parillume.model.internal.AssessmentType;
import com.parillume.util.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class ChatUtilTest {
    @Test
    public void generateNextChatMetadata_noChatHistory_returnsFirstUser_returnsDefaultTopic()
    throws Exception {
        List<String> targetUserIds = Arrays.asList("a", "b", "c");
        
        ChatMetadata expected = new ChatMetadata("chatter", targetUserIds, null, ChatTopic.getDefaultTopic());
        expected.setTargetUserIds(targetUserIds.subList(0,1));
        
        ChatMetadata generated = ChatUtil.generateNextChatMetadata(null, "chatter", targetUserIds);
        assertEquals(expected, generated);
                
        generated = ChatUtil.generateNextChatMetadata(new ArrayList<ChatMetadata>(), "chatter", targetUserIds);
        assertEquals(expected, generated);
    }
    @Test
    public void generateNextChatMetadata_userAssessmentTypesCovered_returnsUncoveredAssessmentTypes() 
    throws Exception {
        List<ChatTopic> topics = ChatTopic.getOrderedTopics();
        
        List<String> targetUserIds = new ArrayList();
        for(int i=0; i < topics.size(); i++) {
            targetUserIds.add("user"+i);
        }
        
        List<ChatMetadata> chatHistory = new ArrayList<>();
        for(int i=0; i < topics.size(); i++) {
            ChatTopic topic = topics.get(i);
            ChatMetadata metadata = new ChatMetadata("chatter", 
                                                     new ArrayList( Arrays.asList(targetUserIds.get(i)) ), 
                                                     null, topic,
                                                     AssessmentType.CliftonStrengths,
                                                     AssessmentType.MyersBriggs);
            chatHistory.add(metadata);
        }   
        
        for(String userId: targetUserIds) {
            ChatMetadata generated = ChatUtil.generateNextChatMetadata(chatHistory, "chatter", 
                                                                       new ArrayList(Arrays.asList(userId)));
            assertFalse(generated.getAssessmentTypes().contains(AssessmentType.CliftonStrengths));
            assertFalse(generated.getAssessmentTypes().contains(AssessmentType.MyersBriggs));
        }
    }
    
    @Test
    public void generateNextChatMetadata_allUsersCovered_someTopicsUncovered_returnsUncoveredTopic()
    throws Exception {
        List<String> targetUserIds = new ArrayList( Arrays.asList("a", "b", "c") );
        
        List<ChatMetadata> chatHistory = new ArrayList<>();
        List<ChatTopic> topics = ChatTopic.getOrderedTopics();
        // Remove one topic at random:
        topics.remove( Constants.RANDOM.nextInt(topics.size()) );  
        for(int i=0; i < topics.size(); i++) {
            ChatTopic topic = topics.get(i);
            ChatMetadata metadata = new ChatMetadata("chatter", 
                                                     new ArrayList( Arrays.asList(targetUserIds.get(i)) ), 
                                                     null, topic);
            chatHistory.add(metadata);
        }
        List<ChatTopic> coveredTopics = chatHistory.stream().map(h->h.getChatTopic()).collect(Collectors.toList());
        ChatMetadata generated = ChatUtil.generateNextChatMetadata(chatHistory, "chatter", targetUserIds);
        assertFalse( coveredTopics.contains(generated.getChatTopic()) );
    }
    
    @Test
    public void generateNextChatMetadata_someUsersCovered_allTopicsCovered_returnsUncoveredUser()
    throws Exception {
        List<String> targetUserIds = new ArrayList( Arrays.asList("a", "b", "c") );
        
        List<ChatMetadata> chatHistory = new ArrayList<>();
        LinkedList<ChatTopic> topics = ChatTopic.getOrderedTopics();
        for(int i=0; i < topics.size(); i++) {
            ChatTopic topic = topics.get(i);
            ChatMetadata metadata = new ChatMetadata("chatter", targetUserIds, null, topic);
            chatHistory.add(metadata);
        }
        
        List<String> allUserIds = new ArrayList(targetUserIds);
        allUserIds.add("d");
        allUserIds.add("e");
        
        ChatMetadata generated = ChatUtil.generateNextChatMetadata(chatHistory, "chatter", allUserIds);
        assertFalse( targetUserIds.removeAll(generated.getTargetUserIds()) );
        assertEquals( ChatTopic.getNextTopic(topics.getLast()), generated.getChatTopic() );
    }   
    
    @Test
    public void generateNextChatMetadata_allUsersCovered_allTopicsCovered_returnsNewUserTopicCombo()
    throws Exception {
        List<String> targetUserIds1 = new ArrayList( Arrays.asList("a", "b", "c") );
        List<String> targetUserIds2 = new ArrayList( Arrays.asList("b", "c", "d", "e") );
        List<String> targetUserIds3 = new ArrayList( Arrays.asList("d", "e", "f", "g") );
        List<List<String>> userIdLists = new ArrayList( Arrays.asList(targetUserIds1, targetUserIds2, targetUserIds3) );
        
        List<ChatMetadata> chatHistory = new ArrayList<>();
        LinkedList<ChatTopic> topics = ChatTopic.getOrderedTopics();        

        int userIdListIndex = -1;
        for(int i=0; i < topics.size(); i++) {
            ChatTopic topic = topics.get(i);
            
            userIdListIndex++;
            if(userIdListIndex >= userIdLists.size())
               userIdListIndex = 0; 
            
            ChatMetadata metadata = new ChatMetadata("chatter", userIdLists.get(userIdListIndex), null, topic);
            chatHistory.add(metadata);
        }
            
        ChatMetadata generated = ChatUtil.generateNextChatMetadata(chatHistory, "chatter", 
                                                                   userIdLists.stream()
                                                                              .flatMap(l->l.stream())
                                                                              .distinct()
                                                                              .collect(Collectors.toList())
                                                                  );
        assertEquals( ChatTopic.getNextTopic(topics.getLast()), generated.getChatTopic() );
        
        // Confirm that the generated topic's user id list is a NEW combo of topic + user id
        Optional<ChatMetadata> opt = chatHistory.stream()
                                                .filter(c -> c.getChatTopic() == generated.getChatTopic())
                                                .findFirst();
        assertTrue(opt.isPresent());        
        assertFalse( opt.get().getTargetUserIds().removeAll(generated.getTargetUserIds()) );
    }  
}
