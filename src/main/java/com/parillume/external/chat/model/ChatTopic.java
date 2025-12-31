/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.external.chat.model;

import com.parillume.util.StringUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum ChatTopic {
    //GENERAL, 
    COMMUNICATION(1, 
                  "How can I best communicate with this person in a business environment?",
                  "How can I best communicate with these people in a business environment?"), 
    GROWTH(2), 
    PRODUCTIVITY(3), 
    CULTURE(4);
    
    private int order;
    private String singleTargetQuestion;
    private String multiTargetQuestion;
    
    private ChatTopic(int order) {
        this.order = order;
    }
    private ChatTopic(int order, String singleTargetQuestion, String multiTargetQuestion) {
        this(order);
        this.singleTargetQuestion = singleTargetQuestion;
        this.multiTargetQuestion = multiTargetQuestion;
    }
    
    public int getOrder() { return order; }
    public String getQuestion(boolean singleTarget) {
        String question = singleTarget ? singleTargetQuestion : multiTargetQuestion;
        if(StringUtil.isEmpty(question))
            throw new NotImplementedException("ChatTopic " +name()+ " is not implemented");
        return question;
    }
    
    public static ChatTopic getDefaultTopic() {
        return COMMUNICATION;
    }
    
    public static ChatTopic getTopic(int order) {
        Optional<ChatTopic> opt = Arrays.asList(values())
                                        .stream()
                                        .filter(t -> order == t.getOrder())
                                        .findFirst();
        if(opt.isEmpty())
            throw new RuntimeException("No ChatTopic has order " + order);
        return opt.get();
    }
    
    public static ChatTopic getNextTopic(ChatTopic topic) {
        LinkedList<ChatTopic> orderedTopics = getOrderedTopics();
        if(topic == orderedTopics.getLast())
            return orderedTopics.getFirst();
        
        return getTopic(topic.getOrder()+1);
    }
    
    public static LinkedList<ChatTopic> getOrderedTopics() {
        return new LinkedList( 
            Arrays.asList(values())
                  .stream()
                  .sorted(Comparator.comparing(ChatTopic::getOrder))
                  .collect(Collectors.toList())
        );
    }
    
    public static LinkedList<ChatTopic> getOrderedTopicsStartingWith(ChatTopic startingWith) {
        LinkedList<ChatTopic> orderedTopics = getOrderedTopics();
        int startIndex = orderedTopics.indexOf(startingWith);
        
        LinkedList<ChatTopic> reorderedTopics = new LinkedList<>();
        int index = startIndex;
        while(true) {
            reorderedTopics.add( orderedTopics.get(index) );
            
            index = (index < reorderedTopics.size()- 1) ?
                     index + 1 :
                     0;
            if(index == startIndex)
                break;
        }
        
        return reorderedTopics;
    }
}
