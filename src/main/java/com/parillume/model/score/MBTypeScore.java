/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.score;

import com.parillume.util.StringUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum MBTypeScore {       
    // Culture Category: Wanna Play?
    ENFJ("DOG",     MBPreferenceScore.EXTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.FEELING, MBPreferenceScore.JUDGING),
    ENFP("DOLPHIN", MBPreferenceScore.EXTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.FEELING, MBPreferenceScore.PERCEIVING),
    ESFP("SEAL",    MBPreferenceScore.EXTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.FEELING, MBPreferenceScore.PERCEIVING),
    ESTP("CHEETAH", MBPreferenceScore.EXTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.THINKING, MBPreferenceScore.PERCEIVING),
    
    // Culture Category: Ten Steps Ahead
    ENTJ("BEAR",    MBPreferenceScore.EXTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.THINKING, MBPreferenceScore.JUDGING),
    ENTP("FOX",     MBPreferenceScore.EXTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.THINKING, MBPreferenceScore.PERCEIVING),
    INTP("OWL",     MBPreferenceScore.INTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.THINKING, MBPreferenceScore.PERCEIVING),
    INTJ("OCTOPUS", MBPreferenceScore.INTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.THINKING, MBPreferenceScore.JUDGING),
    
    // Culture Category: All for One and One for All
    ESFJ("HORSE",   MBPreferenceScore.EXTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.FEELING, MBPreferenceScore.JUDGING),
    ESTJ("LION",    MBPreferenceScore.EXTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.THINKING, MBPreferenceScore.JUDGING),
    ISTJ("BEAVER",  MBPreferenceScore.INTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.THINKING, MBPreferenceScore.JUDGING),
    ISFJ("DEER",    MBPreferenceScore.INTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.FEELING, MBPreferenceScore.JUDGING),
    
    // Culture Category: There's More Inside
    INFJ("WOLF",    MBPreferenceScore.INTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.FEELING, MBPreferenceScore.JUDGING),
    INFP("SWAN",    MBPreferenceScore.INTROVERT, MBPreferenceScore.INTUITIVE, MBPreferenceScore.FEELING, MBPreferenceScore.PERCEIVING),
    ISFP("PANDA",   MBPreferenceScore.INTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.FEELING, MBPreferenceScore.PERCEIVING),
    ISTP("CAT",     MBPreferenceScore.INTROVERT, MBPreferenceScore.SENSORY, MBPreferenceScore.THINKING, MBPreferenceScore.PERCEIVING);
    
    private String animal;
    private MBPreferenceScore[] preferences;
    
    private MBTypeScore(String animal, MBPreferenceScore... preferences) {
        this.animal = animal;
        this.preferences = preferences;
    }
    
    public String getId() {
        return "MB-" + name().toLowerCase();
    }   
    
    public String getAnimal() {
        return animal;
    }
    
    public MBPreferenceScore[] getPreferences() {
        return preferences;
    }
    
    public static MBTypeScore getScoreById(String id) {
        Optional<MBTypeScore> opt = Arrays.asList(MBTypeScore.values())
                                          .stream()
                                          .filter(s -> StringUtil.nullEquals(s.getId(), id))
                                          .findFirst();
        return opt.isPresent() ? opt.get() : null;
    }   
    
    public static MBTypeScore getType(List<MBPreferenceScore> preferences) 
    throws Exception {
        List<String> submittedPrefs = preferences.stream()
                                                 .map(p -> p.name())
                                                 .collect(Collectors.toList());
        for(MBTypeScore typeScore: values()) {
            List<String> thisPrefs = Arrays.asList(typeScore.preferences)
                                           .stream()
                                           .map(p -> p.name())
                                           .collect(Collectors.toList());
            if(StringUtil.collectionsEqual(thisPrefs, submittedPrefs)) 
                return typeScore;
        }
        throw new Exception("No MBTypeScore corresponds to preferences " + preferences);
    }
}
