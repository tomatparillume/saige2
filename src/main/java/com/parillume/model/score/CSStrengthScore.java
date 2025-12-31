/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.score;

import com.parillume.util.StringUtil;
import com.parillume.util.content.CSContentUtil;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum CSStrengthScore {    
    // This order matches the order in the CliftonStrengths team chart:
    ANALYTICAL("Analytical", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They poke holes, find gaps and flaws, and reveal inconsistencies."),
    CONTEXT("Context", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They draw from past patterns and lessons learned."),
    FUTURISTIC("Futuristic", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They are visionary and creative about future possibilities."),
    IDEATION("Ideation", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They excel at generating creative new approaches and connections."),
    INPUT("Input", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They are curious collectors of information with a rich library of facts."),
    INTELLECTION("Intellection", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They love intellectual challenges and deep cognitive reflection."),
    LEARNER("Learner", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They love to explore unfamiliar systems and master new concepts."),
    STRATEGIC("Strategic", CSContentUtil.LeadershipTheme.STRATEGIC,
            "They find the best path forward and how to overcome obstacles."),

    ACTIVATOR("Activator", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They turn thought into action and discover while doing."),
    COMMAND("Command", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They are confident leaders with a powerful presence, unafraid to speak up."),
    COMMUNICATION("Communication", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They are natural educators who clearly voice concepts and systems."),
    COMPETITION("Competition", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They love clear goals and challenges where success is quantifiable."),
    MAXIMIZER("Maximizer", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They focus on positive possibilities to transform good into great."),
    SELF_ASSURANCE("Self-Assurance", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They know who they are and what they believe, and exude confidence."),
    SIGNIFICANCE("Significance", CSContentUtil.LeadershipTheme.INFLUENCING,
            "They want work that matters and respect for their contributions."),
    WOO("Woo", CSContentUtil.LeadershipTheme.INFLUENCING,
            "Charming and engaging, they raise the energy and create rapport."),

    ACHIEVER("Achiever", CSContentUtil.LeadershipTheme.EXECUTING,
            "High-energy and focused, they tackle a goal and move on to the next."),
    ARRANGER("Arranger", CSContentUtil.LeadershipTheme.EXECUTING,
            "Like orchestra conductors, they know who and what belongs where."),
    BELIEF("Belief", CSContentUtil.LeadershipTheme.EXECUTING,
            "They live and work from their core values and internal integrity."),
    CONSISTENCY("Consistency", CSContentUtil.LeadershipTheme.EXECUTING,
            "They crave, and create, clear standards, rules, and processes."),
    DELIBERATIVE("Deliberative", CSContentUtil.LeadershipTheme.EXECUTING,
            "Careful, thorough planners, they need to time to consider an issue."),
    DISCIPLINE("Discipline", CSContentUtil.LeadershipTheme.EXECUTING,
            "They commit to a goal and push through adversity to reach it."),
    FOCUS("Focus", CSContentUtil.LeadershipTheme.EXECUTING,
            "They avoid tangents and distractions to hone in on achieving a goal."),
    RESPONSIBILITY("Responsibility", CSContentUtil.LeadershipTheme.EXECUTING,
            "They take ownership and accept obligation and culpability."),
    RESTORATIVE("Restorative", CSContentUtil.LeadershipTheme.EXECUTING,
            "They right wrongs, repair what's broken, and fill in what's missing."),

    ADAPTABILITY("Adaptability", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They remain calm and adjust to change in a crisis."),
    CONNECTEDNESS("Connectedness", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They see hidden relationships and clarify system dependencies."),
    DEVELOPER("Developer", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They see potential in others and know how to help people grow."),
    EMPATHY("Empathy", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "Highly intuitive, they create a space of acceptance and trust."),
    HARMONY("Harmony", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They see all sides of an issue and build bridges during conflict."),
    INCLUDER("Includer", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They invite all to the table, encouraging and celebrating diversity."),
    INDIVIDUALIZATION("Individualization", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They see, value, and celebrate the unique nature of each person."),
    POSITIVITY("Positivity", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They see the glass as half-full; they bring hope and possibilities."),
    RELATOR("Relator", CSContentUtil.LeadershipTheme.RELATIONSHIPS,
            "They value deep, genuine relationships, and create equal footing.");
    
    private String label;    
    private CSContentUtil.LeadershipTheme theme;
    private String description;
    
    private CSStrengthScore(String label, CSContentUtil.LeadershipTheme theme,
                            String description) {
        this.label = label;
        this.theme = theme;
        this.description = description;
    }
    
    public String getId() {
        return "CS-" + getLabel().toLowerCase();
    }

    public String getLabel() { return label; }
    public CSContentUtil.LeadershipTheme getTheme() { return theme; }
    public String getDescription() { return description; }
    
    public static CSStrengthScore getStrengthById(String id) {
        Optional<CSStrengthScore> opt = Arrays.asList(CSStrengthScore.values())
                                                .stream()
                                                .filter(s -> StringUtil.nullEquals(s.getId(), id))
                                                .findFirst();
        return opt.isPresent() ? opt.get() : null;
    }
}
