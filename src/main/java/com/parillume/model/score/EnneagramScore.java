/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.score;

import com.parillume.model.internal.EnnResult;
import com.parillume.model.internal.EnnSocialStance;
import com.parillume.util.StringUtil;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum EnneagramScore {    
    // This order matches the order in the CliftonStrengths team chart:
    ACHIEVER(3, "Achiever", "Am I worthy and accepted?",
             EnnSocialStance.ASSERTIVE),
    ENTHUSIAST(7, "Enthusiast", "Am I satisfied and happy?",
             EnnSocialStance.ASSERTIVE),
    CHALLENGER(8, "Challenger", "Am I safe and in control?",
             EnnSocialStance.ASSERTIVE),
    
    REFORMER(1, "Reformer", "Am I in integrity?",
             EnnSocialStance.ALIGNING),
    HELPER(2, "Helper", "Am I loved?",
             EnnSocialStance.ALIGNING),
    LOYALIST(6, "Loyalist", "Am I secure and supported?",
             EnnSocialStance.ALIGNING),
    
    INDIVIDUALIST(4, "Individualist", "Am I unique and significant?",
             EnnSocialStance.WITHDRAWING),
    INVESTIGATOR(5, "Investigator", "Am I capable and competent?",
             EnnSocialStance.WITHDRAWING),
    PEACEMAKER(9, "Peacemaker", "Am I at peace?",
             EnnSocialStance.WITHDRAWING);
    
    private int type;
    private String label;
    private String burningQuestion;
    private EnnSocialStance socialStance;
    
    private EnneagramScore(int type, String label, String burningQuestion,
                           EnnSocialStance socialStance) {
        this.type = type;
        this.label = label;
        this.burningQuestion = burningQuestion;
        this.socialStance = socialStance;
    }
    
    public int getType() { return type; }
    public String getLabel() { return label; }
    public String getBurningQuestion() { return burningQuestion; }
    public EnnSocialStance getSocialStance() { return socialStance; }
        
    public String getName() {
        return String.valueOf(getType());
    }
    public String getId() {
        return "EN-" + getType();
    }   
    
    public EnnResult getResultTemplate() {
        EnnResult result = new EnnResult();
        result.setId(getId());
        result.setName(getName());
        result.setNickname(getLabel());
        return result;
    }
    
    public static boolean isLabel(String s) {
        return Arrays.asList(EnneagramScore.values())
                     .stream()
                     .map(score -> score.getLabel().toUpperCase())
                     .collect(Collectors.toList())
                     .contains(s.toUpperCase());
    }

    
    public static EnneagramScore getScoreById(String id) {
        Optional<EnneagramScore> opt = Arrays.asList(EnneagramScore.values())
                                             .stream()
                                             .filter(s -> StringUtil.nullEquals(s.getId(), id))
                                             .findFirst();
        return opt.isPresent() ? opt.get() : null;
    }    
    public static EnneagramScore getScoreByType(int type) throws Exception {
        for(EnneagramScore score: EnneagramScore.values()) {
            if(type == score.getType())
                return score;
        }
        throw new Exception(type + " is not a valid Enneagram type");
    }
}
