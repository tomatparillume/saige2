/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model;

import com.parillume.model.internal.AssessmentCategoryType;
import com.parillume.model.internal.CSAssessment;
import com.parillume.model.internal.CSResult;
import com.parillume.model.internal.CultureCategory;
import com.parillume.model.internal.EnnAssessment;
import com.parillume.model.internal.EnnResult;
import com.parillume.model.internal.LeadershipTheme;
import com.parillume.model.internal.LeadershipStyle;
import com.parillume.model.internal.MBAssessment;
import com.parillume.model.internal.MBPreference;
import com.parillume.model.internal.MBResult;
import com.parillume.model.internal.SocialStance;
import com.parillume.model.external.Company;
import com.parillume.model.external.Role;
import com.parillume.model.internal.RoleType;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.model.internal.EnnSocialStance;
import com.parillume.model.internal.MBPreferenceTheme;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.model.lexicon.Term;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBPreferenceScore;
import com.parillume.util.Constants;
import com.parillume.util.JSONUtil;
import com.parillume.util.model.CalendarType;
import com.parillume.util.model.EmailSchedule;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

/**
 * This class defines various JSON structures appropriate for posting to the DB.
 * Running this class' main() method prints out such structures; see the 
 * System.out.println statements at the bottom of the class.
 * 
 * Other model CRUD operations are executed through database services.
 * 
 *  * @author tom@parillume.com
 */
public class ModelWriter {
    //TJMTJM NEEDS VERSIONING
    
    public static void main(String[] args) throws Exception {
        Pair<CorpusModel, CompanyModel> models = generateModels();
        CorpusModel corpusModel = models.getLeft();
        CompanyModel parillumeCompanyModel = models.getRight();
        
        JSONObject parent = new JSONObject();
        parent.put("version", CorpusModel.CURRENT_VERSION);
        parent.put("corpusModel", corpusModel);
        parent.put("parillumeCompanyModel", parillumeCompanyModel);
        //parent.put("billTrustCompanyModel", billTrustCompanyModel);//TJMTJM This should be added by another Writer.java class: CompanyModelWriter.java
        System.out.println(JSONUtil.toJSON(corpusModel));
//        System.out.println(JSONUtil.toJSON(parillumeCompanyModel));
//        System.out.println(JSONUtil.toJSON(billTrustCompanyModel));
    }
    
    public static Pair<CorpusModel, CompanyModel> generateModels() {
        Term superpowerTerm = new Term();
        superpowerTerm.setId(Constants.SUPERPOWER_TERMID);
        superpowerTerm.setName("superpower");
        superpowerTerm.setDescription("Innate talents, motivations and personality patterns that consistently produce superior results.");
        
        Term kryptoniteTerm = new Term();
        kryptoniteTerm.setId(Constants.KRYPTONITE_TERMID);
        kryptoniteTerm.setName("kryptonite");
        kryptoniteTerm.setDescription("Tasks, duties, and communication themes that disempower us, fueling avoidance, mediocrity, and silos on our teams.");
        
        ///////////////////////////

        AssessmentCategoryType socialStanceAssessmentCategoryType = new AssessmentCategoryType();
        socialStanceAssessmentCategoryType.setId("ACT-socialstance");
        socialStanceAssessmentCategoryType.setOrder(1);
        socialStanceAssessmentCategoryType.setName("Social Stance");
        socialStanceAssessmentCategoryType.setDescription("TJMTJM Social Stance description.");
                        
        AssessmentCategoryType cultureCategoryAssessmentCategoryType = new AssessmentCategoryType();
        cultureCategoryAssessmentCategoryType.setId("ACT-culturecategory");
        cultureCategoryAssessmentCategoryType.setOrder(2);
        cultureCategoryAssessmentCategoryType.setName("Culture Category");
        cultureCategoryAssessmentCategoryType.setDescription("TJMTJM Culture Category description.");        
        
        AssessmentCategoryType leadershipThemeAssessmentCategoryType = new AssessmentCategoryType();
        leadershipThemeAssessmentCategoryType.setId("ACT-leadershiptheme");
        leadershipThemeAssessmentCategoryType.setOrder(3);
        leadershipThemeAssessmentCategoryType.setName("Leadership Theme");
        leadershipThemeAssessmentCategoryType.setDescription("TJMTJM Leadership Theme description.");

        AssessmentCategoryType leadershipStyleAssessmentCategoryType = new AssessmentCategoryType();
        leadershipStyleAssessmentCategoryType.setId("ACT-leadershipstyle");
        leadershipStyleAssessmentCategoryType.setOrder(4);
        leadershipStyleAssessmentCategoryType.setParentCategoryTypeId(leadershipThemeAssessmentCategoryType.getId());
        leadershipStyleAssessmentCategoryType.setName("Leadership Style");
        leadershipStyleAssessmentCategoryType.setDescription("Each CliftonStrengths® Leadership Theme (Strategic, Influencing, Executing, Relationship-Building) " +
                                                             "comprises three Leadership Styles that manifest the theme in different ways");        
        ///////////////////////////
                        
        LeadershipStyle yesAndStyle = new LeadershipStyle();
        yesAndStyle.setId("LT-yesand");
        yesAndStyle.setOrder(1);
        yesAndStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        yesAndStyle.setName("Yes, And");
        yesAndStyle.setDescription("Need some out-of-the-box ideas? Look to the 'YES, AND' strengths.");
//        yesAndStyle.addTerm(superpowerTerm, "Generating out-of-the-box ideas");
//        yesAndStyle.addTerm(kryptoniteTerm, "YesAnd Style kryptonite goes here TJMTJM");
                        
        LeadershipStyle tellMeMoreStyle = new LeadershipStyle();
        tellMeMoreStyle.setId("LT-tellmemore");
        tellMeMoreStyle.setOrder(2);
        tellMeMoreStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        tellMeMoreStyle.setName("Tell Me More");
        tellMeMoreStyle.setDescription("Already have an idea, but need to ground it with data? The 'TELL ME MORE' types will help.");
//        tellMeMoreStyle.addTerm(superpowerTerm, "Strengthening an idea with grounded data");
//        tellMeMoreStyle.addTerm(kryptoniteTerm, "TellMeMore Style kryptonite goes here TJMTJM");
                        
        LeadershipStyle butWaitStyle = new LeadershipStyle();
        butWaitStyle.setId("LT-butwait");
        butWaitStyle.setOrder(3);
        butWaitStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        butWaitStyle.setName("But, Wait");
        butWaitStyle.setDescription("Want to test the strength of an idea? Ask the 'BUT, WAIT' team members.");
//        butWaitStyle.addTerm(superpowerTerm, "Testing and verifying the strength of an idea");
//        butWaitStyle.addTerm(kryptoniteTerm, "ButWait Style kryptonite goes here TJMTJM");
        
        LeadershipStyle letsGoStyle = new LeadershipStyle(); 
        letsGoStyle.setId("LT-letsgo");
        letsGoStyle.setOrder(4);
        letsGoStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        letsGoStyle.setName("Let's Go");
        letsGoStyle.setDescription("'LET'S GO!' types are powerful communicators and activators.");
//        letsGoStyle.addTerm(superpowerTerm, "Communicating strategies and systems; generating action");
//        letsGoStyle.addTerm(kryptoniteTerm, "LetsGo Style kryptonite goes here TJMTJM");
        
        LeadershipStyle letsConnectStyle = new LeadershipStyle(); 
        letsConnectStyle.setId("LT-letsconnect");
        letsConnectStyle.setOrder(5);
        letsConnectStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        letsConnectStyle.setName("Let's Connect");
        letsConnectStyle.setDescription("'LET'S CONNECT' people create instant rapport and inspiring stories.");
//        letsConnectStyle.addTerm(superpowerTerm, "Connecting with people and inspiring them");
//        letsConnectStyle.addTerm(kryptoniteTerm, "LetsConnect Style kryptonite goes here TJMTJM");
        
        LeadershipStyle makeItMatterStyle = new LeadershipStyle(); 
        makeItMatterStyle.setId("LT-makeitmatter");
        makeItMatterStyle.setOrder(6);
        makeItMatterStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        makeItMatterStyle.setName("Make It Matter");
        makeItMatterStyle.setDescription("'MAKE IT MATTER' types commit to an ideal and bring others along.");
//        makeItMatterStyle.addTerm(superpowerTerm, "Clarifying and elevating an idea's significance");
//        makeItMatterStyle.addTerm(kryptoniteTerm, "MakeItMatter Style kryptonite goes here TJMTJM");
        
        LeadershipStyle structureStyle = new LeadershipStyle(); 
        structureStyle.setId("LT-structure");
        structureStyle.setOrder(7);
        structureStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        structureStyle.setName("Structure");
        structureStyle.setDescription("'STRUCTURE' types are great organizers.");
//        structureStyle.addTerm(superpowerTerm, "Creating and strengthening reliable systems");
//        structureStyle.addTerm(kryptoniteTerm, "Structure Style kryptonite goes here TJMTJM");
        
        LeadershipStyle calibrateStyle = new LeadershipStyle(); 
        calibrateStyle.setId("LT-calibrate");
        calibrateStyle.setOrder(8);
        calibrateStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        calibrateStyle.setName("Calibrate");
        calibrateStyle.setDescription("'CALIBRATE' strengths clarify and refine the process.");
//        calibrateStyle.addTerm(superpowerTerm, "Clarifying and refining systems and processes");
//        calibrateStyle.addTerm(kryptoniteTerm, "Calibrate Style kryptonite goes here TJMTJM");
        
        LeadershipStyle completeStyle = new LeadershipStyle(); 
        completeStyle.setId("LT-complete");
        completeStyle.setOrder(9);
        completeStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        completeStyle.setName("Complete");
        completeStyle.setDescription("'COMPLETE' types make sure you cross the finish line.");
//        completeStyle.addTerm(superpowerTerm, "Generating and maintaining forward movement to get the job done");
//        completeStyle.addTerm(kryptoniteTerm, "Complete Style kryptonite goes here TJMTJM");
        
        LeadershipStyle weAreOneStyle = new LeadershipStyle(); 
        weAreOneStyle.setId("LT-weareone");
        weAreOneStyle.setOrder(10);
        weAreOneStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        weAreOneStyle.setName("We Are One");
        weAreOneStyle.setDescription("'WE ARE ONE' types create harmony and connection.");
//        weAreOneStyle.addTerm(superpowerTerm, "Creating harmony and connection on the team");
//        weAreOneStyle.addTerm(kryptoniteTerm, "WeAreOne Style kryptonite goes here TJMTJM");
        
        LeadershipStyle elevateAndCelebrateStyle = new LeadershipStyle(); 
        elevateAndCelebrateStyle.setId("LT-elevateandcelebrate");
        elevateAndCelebrateStyle.setOrder(11);
        elevateAndCelebrateStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        elevateAndCelebrateStyle.setName("Elevate and Celebrate");
        elevateAndCelebrateStyle.setDescription("\"ELEVATE & CELEBRATE\" types bring out the best in others.");
//        elevateAndCelebrateStyle.addTerm(superpowerTerm, "Recognizing, amplifying, and leveraging people's best selves");
//        elevateAndCelebrateStyle.addTerm(kryptoniteTerm, "ElevateAndCelebrate Style kryptonite goes here TJMTJM");
        
        LeadershipStyle openArmsStyle = new LeadershipStyle(); 
        openArmsStyle.setId("LT-openArms");
        openArmsStyle.setOrder(12);
        openArmsStyle.setAssessmentCategoryTypeId(leadershipStyleAssessmentCategoryType.getId());
        openArmsStyle.setName("Open Arms");
        openArmsStyle.setDescription("'OPEN ARMS' people integrate and unify a diverse team.");
//        openArmsStyle.addTerm(superpowerTerm, "Building bridges one person at a time to unify a diverse team");
//        openArmsStyle.addTerm(kryptoniteTerm, "OpenArms Style kryptonite goes here TJMTJM");
        
        LeadershipTheme strategicLeadershipTheme = new LeadershipTheme();
        strategicLeadershipTheme.setId("LS-strategic");
        strategicLeadershipTheme.setOrder(1);
        strategicLeadershipTheme.setAssessmentCategoryTypeId(leadershipThemeAssessmentCategoryType.getId());
        strategicLeadershipTheme.setName("Strategic");
        strategicLeadershipTheme.setDescription("People with Strategic strengths are energized by possibilities and forging the best path forward. They generate, or fine-tune, new ideas and creative solutions. Make sure you include your Strategic types during the formative stages of a project.");
//        strategicLeadershipTheme.addTerm(superpowerTerm, "Strategic Theme superpowers go here TJMTJM");
//        strategicLeadershipTheme.addTerm(kryptoniteTerm, "Strategic Theme kryptonite goes here TJMTJM");
        
        LeadershipTheme influencingLeadershipTheme = new LeadershipTheme();
        influencingLeadershipTheme.setId("LS-influencing");
        influencingLeadershipTheme.setOrder(2);
        influencingLeadershipTheme.setAssessmentCategoryTypeId(leadershipThemeAssessmentCategoryType.getId());
        influencingLeadershipTheme.setName("Influencing");
        influencingLeadershipTheme.setDescription("Need to sell an idea, internally or externally? Influencers help spread the word and gain buy-in. Once you've strategized a plan, empower your Influencers to gather the people that will make it happen.");
//        influencingLeadershipTheme.addTerm(superpowerTerm, "Influencing Theme superpowers go here TJMTJM");
//        influencingLeadershipTheme.addTerm(kryptoniteTerm, "Influencing Theme kryptonite goes here TJMTJM");
        
        LeadershipTheme executingLeadershipTheme = new LeadershipTheme();
        executingLeadershipTheme.setId("LS-executing");
        executingLeadershipTheme.setOrder(3);
        executingLeadershipTheme.setAssessmentCategoryTypeId(leadershipThemeAssessmentCategoryType.getId());
        executingLeadershipTheme.setName("Executing");
        executingLeadershipTheme.setDescription("Lean on Executors to drive a project forward and get it across the finish line. Give your executing team members the concrete info they need to get 'er done, and then get out of their way.");
//        executingLeadershipTheme.addTerm(superpowerTerm, "Executing Theme superpowers go here TJMTJM");
//        executingLeadershipTheme.addTerm(kryptoniteTerm, "Executing Theme kryptonite goes here TJMTJM");
        
        LeadershipTheme relationshipBuildingLeadershipTheme = new LeadershipTheme();
        relationshipBuildingLeadershipTheme.setId("LS-relationshipbuilding");
        relationshipBuildingLeadershipTheme.setOrder(4);
        relationshipBuildingLeadershipTheme.setAssessmentCategoryTypeId(leadershipThemeAssessmentCategoryType.getId());
        relationshipBuildingLeadershipTheme.setName("Relationship-Building");
        relationshipBuildingLeadershipTheme.setDescription("Throughout the course of a project, leverage Relationship Builders to keep the team bonded: They'll read the room to ensure everyone feels seen, heard, valued and supported.");
//        relationshipBuildingLeadershipTheme.addTerm(superpowerTerm, "RelationshipBuilding Theme superpowers go here TJMTJM");
//        relationshipBuildingLeadershipTheme.addTerm(kryptoniteTerm, "RelationshipBuilding Theme kryptonite goes here TJMTJM");
        
        CultureCategory wannaPlayCultureCategory = new CultureCategory();
        wannaPlayCultureCategory.setId("LT-wannaplay");
        wannaPlayCultureCategory.setOrder(1);
        wannaPlayCultureCategory.setAssessmentCategoryTypeId(cultureCategoryAssessmentCategoryType.getId());
        wannaPlayCultureCategory.setName("Wanna Play?");
        wannaPlayCultureCategory.setDescription("These vibrant Extroverts (E) thrive in an environment of adventure, innovation, connection, and forward momentum. And they wilt when steeped in conflict, rigidity, and seriousness.");
        wannaPlayCultureCategory.addTerm(superpowerTerm, "Adventurous, innovative, and forward-moving");
        wannaPlayCultureCategory.addTerm(kryptoniteTerm, "Conflict, rigidity, and unabated seriousness");
        
        CultureCategory tenStepsAheadCultureCategory = new CultureCategory();
        tenStepsAheadCultureCategory.setId("LT-tenstepsahead");
        tenStepsAheadCultureCategory.setOrder(2);
        tenStepsAheadCultureCategory.setAssessmentCategoryTypeId(cultureCategoryAssessmentCategoryType.getId());
        tenStepsAheadCultureCategory.setName("Ten Steps Ahead");
        tenStepsAheadCultureCategory.setDescription("These Intuitive Thinkers (NT) are quick-thinking, analytical, and highly rational. They love a good debate and want time and space to create. They shrink from emotional processing, people drama and personal vulnerability.");
        tenStepsAheadCultureCategory.addTerm(superpowerTerm, "Quick-thinking, analytical, and highly rational");
        tenStepsAheadCultureCategory.addTerm(kryptoniteTerm, "Emotional processing, people drama, and personal vulnerability");
        
        CultureCategory allForOneCultureCategory = new CultureCategory();
        allForOneCultureCategory.setId("LT-allforone");
        allForOneCultureCategory.setOrder(3);
        allForOneCultureCategory.setAssessmentCategoryTypeId(cultureCategoryAssessmentCategoryType.getId());
        allForOneCultureCategory.setName("All for One & One for All");
        allForOneCultureCategory.setDescription("These Sensory Judging (SJ) types are committed, hard working, organized and dutiful. Cavalier leadership, inefficiency, unnecessary risk and constant customization are kryptonite to them.");
        allForOneCultureCategory.addTerm(superpowerTerm, "Committed, hard working, organized, and dutiful");
        allForOneCultureCategory.addTerm(kryptoniteTerm, "Cavalier leadership, inefficiency, unnecessary risk, and constant customization");
        
        CultureCategory theresMoreInsideCultureCategory = new CultureCategory();
        theresMoreInsideCultureCategory.setId("LT-theresmoreinside");
        theresMoreInsideCultureCategory.setOrder(4);
        theresMoreInsideCultureCategory.setAssessmentCategoryTypeId(cultureCategoryAssessmentCategoryType.getId());
        theresMoreInsideCultureCategory.setName("There's More Inside");
        theresMoreInsideCultureCategory.setDescription("These quieter, creative Introverts (I) need space to create and re-energize on their own terms. Instability, drama, workaholism and stress can hit them harder than most, especially if there's no end in sight or time to reset.");
        theresMoreInsideCultureCategory.addTerm(superpowerTerm, "Creative, introspective, and insightful");
        theresMoreInsideCultureCategory.addTerm(kryptoniteTerm, "Instability, drama, workaholism, and stress");
        
        SocialStance assertiveSocialStance = new SocialStance();
        assertiveSocialStance.setId("SS-assertive");
        assertiveSocialStance.setOrder(1);
        assertiveSocialStance.setAssessmentCategoryTypeId(socialStanceAssessmentCategoryType.getId());
        assertiveSocialStance.setName(EnnSocialStance.ASSERTIVE.getLabel());
        assertiveSocialStance.setDescription("Assertive types create what they want in the world to get their needs met.");
//        assertiveSocialStance.addTerm(superpowerTerm, "Assertive superpowers go here TJMTJM");
//        assertiveSocialStance.addTerm(kryptoniteTerm, "Assertive kryptonite goes here TJMTJM");
        
        SocialStance aligningSocialStance = new SocialStance();
        aligningSocialStance.setId("SS-aligning");
        aligningSocialStance.setOrder(2);
        aligningSocialStance.setAssessmentCategoryTypeId(socialStanceAssessmentCategoryType.getId());
        aligningSocialStance.setName(EnnSocialStance.ALIGNING.getLabel());
        aligningSocialStance.setDescription("Aligning types adapt to the world to get their needs met.");
//        aligningSocialStance.addTerm(superpowerTerm, "Aligning superpowers go here TJMTJM");
//        aligningSocialStance.addTerm(kryptoniteTerm, "Aligning kryptonite goes here TJMTJM");        
        
        SocialStance withdrawingSocialStance = new SocialStance();
        withdrawingSocialStance.setId("SS-withdrawing");
        withdrawingSocialStance.setOrder(3);
        withdrawingSocialStance.setAssessmentCategoryTypeId(socialStanceAssessmentCategoryType.getId());
        withdrawingSocialStance.setName(EnnSocialStance.WITHDRAWING.getLabel());
        withdrawingSocialStance.setDescription("Withdrawing types disengage from the world to get their needs met.");
//        withdrawingSocialStance.addTerm(superpowerTerm, "Withdrawing superpowers go here TJMTJM");
//        withdrawingSocialStance.addTerm(kryptoniteTerm, "Withdrawing kryptonite goes here TJMTJM");
        ///////////////////////////
        
        CSAssessment csAssessment = new CSAssessment();
        csAssessment.setId("CS-assessment"); 
        csAssessment.setSummary("Strengths");
        csAssessment.setDescription("Invitation to contribute powerfully, effectively, and with confidence");
        csAssessment.setDisclaimer("This material contains trademarks of GALLUP®. The non-Gallup information you are receiving has not been approved and is not sanctioned or endorsed by Gallup in any way. Opinions, views and interpretations of CliftonStrengths® are solely the beliefs of Parillume®.");
        
        CSResult futuristicStrength = new CSResult();
        futuristicStrength.setId(CSStrengthScore.FUTURISTIC.getId());
        futuristicStrength.setAssessmentId(csAssessment.getId());
        futuristicStrength.setName(CSStrengthScore.FUTURISTIC.getLabel());
        futuristicStrength.setDescription(CSStrengthScore.FUTURISTIC.getDescription());
        futuristicStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        futuristicStrength.addAssessmentCategoryId(yesAndStyle.getId());
        futuristicStrength.addTerm(superpowerTerm, "Forecasting & envisioning a better future in detail");
        futuristicStrength.addTerm(kryptoniteTerm, "Status quo; others not grasping my vision");        
        
        CSResult ideationStrength = new CSResult();
        ideationStrength.setId(CSStrengthScore.IDEATION.getId());
        ideationStrength.setAssessmentId(csAssessment.getId());
        ideationStrength.setName(CSStrengthScore.IDEATION.getLabel());
        ideationStrength.setDescription(CSStrengthScore.IDEATION.getDescription());
        ideationStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        ideationStrength.addAssessmentCategoryId(yesAndStyle.getId());
        ideationStrength.addTerm(superpowerTerm, "Creative brainstorming and innovative solutions");
        ideationStrength.addTerm(kryptoniteTerm, "Status quo; lack of innovation");      
        
        CSResult strategicStrength = new CSResult();
        strategicStrength.setId(CSStrengthScore.STRATEGIC.getId());
        strategicStrength.setAssessmentId(csAssessment.getId());
        strategicStrength.setName(CSStrengthScore.STRATEGIC.getLabel());
        strategicStrength.setDescription(CSStrengthScore.STRATEGIC.getDescription());
        strategicStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        strategicStrength.addAssessmentCategoryId(yesAndStyle.getId());
        strategicStrength.addTerm(superpowerTerm, "Finding the best path forward; overcoming obstacles");
        strategicStrength.addTerm(kryptoniteTerm, "\"One-way-only\" thinking; outdated systems");     
        
        CSResult contextStrength = new CSResult();
        contextStrength.setId(CSStrengthScore.CONTEXT.getId());
        contextStrength.setAssessmentId(csAssessment.getId());
        contextStrength.setName(CSStrengthScore.CONTEXT.getLabel());
        contextStrength.setDescription(CSStrengthScore.CONTEXT.getDescription());
        contextStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        contextStrength.addAssessmentCategoryId(tellMeMoreStyle.getId());
        contextStrength.addTerm(superpowerTerm, "Post-mortems; using the past to inform the present");
        contextStrength.addTerm(kryptoniteTerm, "Ignorance of, or dismissal of, lessons of the past");    
        
        CSResult inputStrength = new CSResult();
        inputStrength.setId(CSStrengthScore.INPUT.getId());
        inputStrength.setAssessmentId(csAssessment.getId());
        inputStrength.setName(CSStrengthScore.INPUT.getLabel());
        inputStrength.setDescription(CSStrengthScore.INPUT.getDescription());
        inputStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        inputStrength.addAssessmentCategoryId(tellMeMoreStyle.getId());
        inputStrength.addTerm(superpowerTerm, "Expertise through research, collecting information, and leveraging resources");
        inputStrength.addTerm(kryptoniteTerm, "Lack of relevant resources, information, and expertise; \"flying blind\""); 
        
        CSResult learnerStrength = new CSResult();
        learnerStrength.setId(CSStrengthScore.LEARNER.getId());
        learnerStrength.setAssessmentId(csAssessment.getId());
        learnerStrength.setName(CSStrengthScore.LEARNER.getLabel());
        learnerStrength.setDescription(CSStrengthScore.LEARNER.getDescription());
        learnerStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        learnerStrength.addAssessmentCategoryId(tellMeMoreStyle.getId());
        learnerStrength.addTerm(superpowerTerm, "Hunger for knowledge and new frontiers");
        learnerStrength.addTerm(kryptoniteTerm, "Boredom due to lack of learning opportunities");   
        
        CSResult analyticalStrength = new CSResult();
        analyticalStrength.setId(CSStrengthScore.ANALYTICAL.getId());
        analyticalStrength.setAssessmentId(csAssessment.getId());
        analyticalStrength.setName(CSStrengthScore.ANALYTICAL.getLabel());
        analyticalStrength.setDescription(CSStrengthScore.ANALYTICAL.getDescription());
        analyticalStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        analyticalStrength.addAssessmentCategoryId(butWaitStyle.getId());
        analyticalStrength.addTerm(superpowerTerm, "Poking holes in ideas and strategies");
        analyticalStrength.addTerm(kryptoniteTerm, "Lack of data and facts; emotional decisions"); 
        
        CSResult intellectionStrength = new CSResult();
        intellectionStrength.setId(CSStrengthScore.INTELLECTION.getId());
        intellectionStrength.setAssessmentId(csAssessment.getId());
        intellectionStrength.setName(CSStrengthScore.INTELLECTION.getLabel());
        intellectionStrength.setDescription(CSStrengthScore.INTELLECTION.getDescription());
        intellectionStrength.addAssessmentCategoryId(strategicLeadershipTheme.getId());
        intellectionStrength.addAssessmentCategoryId(butWaitStyle.getId());
        intellectionStrength.addTerm(superpowerTerm, "Thinking deeply about an issue or strategy");
        intellectionStrength.addTerm(kryptoniteTerm, "A thoughtless approach; anxious rumination");    
        
        CSResult activatorStrength = new CSResult();
        activatorStrength.setId(CSStrengthScore.ACTIVATOR.getId());
        activatorStrength.setAssessmentId(csAssessment.getId());
        activatorStrength.setName(CSStrengthScore.ACTIVATOR.getLabel());
        activatorStrength.setDescription(CSStrengthScore.ACTIVATOR.getDescription());
        activatorStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        activatorStrength.addAssessmentCategoryId(letsGoStyle.getId());
        activatorStrength.addTerm(superpowerTerm, "Turning thoughts into action and momentum");
        activatorStrength.addTerm(kryptoniteTerm, "All talk and no action");  
        
        CSResult commandStrength = new CSResult();
        commandStrength.setId(CSStrengthScore.COMMAND.getId());
        commandStrength.setAssessmentId(csAssessment.getId());
        commandStrength.setName(CSStrengthScore.COMMAND.getLabel());
        commandStrength.setDescription(CSStrengthScore.COMMAND.getDescription());
        commandStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        commandStrength.addAssessmentCategoryId(letsGoStyle.getId());
        commandStrength.addTerm(superpowerTerm, "Bold and decisive");
        commandStrength.addTerm(kryptoniteTerm, "Passivity; lack of clear leadership");      
        
        CSResult competitionStrength = new CSResult();
        competitionStrength.setId(CSStrengthScore.COMPETITION.getId());
        competitionStrength.setAssessmentId(csAssessment.getId());
        competitionStrength.setName(CSStrengthScore.COMPETITION.getLabel());
        competitionStrength.setDescription(CSStrengthScore.COMPETITION.getDescription());
        competitionStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        competitionStrength.addAssessmentCategoryId(letsGoStyle.getId());
        competitionStrength.addTerm(superpowerTerm, "Working hard to win, every time");
        competitionStrength.addTerm(kryptoniteTerm, "Losing the game; no game to play");      
        
        CSResult selfAssuranceStrength = new CSResult();
        selfAssuranceStrength.setId(CSStrengthScore.SELF_ASSURANCE.getId());
        selfAssuranceStrength.setAssessmentId(csAssessment.getId());
        selfAssuranceStrength.setName(CSStrengthScore.SELF_ASSURANCE.getLabel());
        selfAssuranceStrength.setDescription(CSStrengthScore.SELF_ASSURANCE.getDescription());
        selfAssuranceStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        selfAssuranceStrength.addAssessmentCategoryId(letsGoStyle.getId());
        selfAssuranceStrength.addTerm(superpowerTerm, "Confident risk-taking and self-management");
        selfAssuranceStrength.addTerm(kryptoniteTerm, "Lack of autonomy; hesitancy in others");      
        
        CSResult communicationStrength = new CSResult();
        communicationStrength.setId(CSStrengthScore.COMMUNICATION.getId());
        communicationStrength.setAssessmentId(csAssessment.getId());
        communicationStrength.setName(CSStrengthScore.COMMUNICATION.getLabel());
        communicationStrength.setDescription(CSStrengthScore.COMMUNICATION.getDescription());
        communicationStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        communicationStrength.addAssessmentCategoryId(letsConnectStyle.getId());
        communicationStrength.addTerm(superpowerTerm, "Clear, memorable use of language; storytelling");
        communicationStrength.addTerm(kryptoniteTerm, "Lack of an audience; poor communication by others");
        
        CSResult wooStrength = new CSResult();
        wooStrength.setId(CSStrengthScore.WOO.getId());
        wooStrength.setAssessmentId(csAssessment.getId());
        wooStrength.setName(CSStrengthScore.WOO.getLabel());
        wooStrength.setDescription(CSStrengthScore.WOO.getDescription());
        wooStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        wooStrength.addAssessmentCategoryId(letsConnectStyle.getId());
        wooStrength.addTerm(superpowerTerm, "Creating rapport and positive energy");
        wooStrength.addTerm(kryptoniteTerm, "Being unliked; a shrinking network");
        
        CSResult maximizerStrength = new CSResult();
        maximizerStrength.setId(CSStrengthScore.MAXIMIZER.getId());
        maximizerStrength.setAssessmentId(csAssessment.getId());
        maximizerStrength.setName(CSStrengthScore.MAXIMIZER.getLabel());
        maximizerStrength.setDescription(CSStrengthScore.MAXIMIZER.getDescription());
        maximizerStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        maximizerStrength.addAssessmentCategoryId(makeItMatterStyle.getId());
        maximizerStrength.addTerm(superpowerTerm, "Turning good into great; pursuing perfection");
        maximizerStrength.addTerm(kryptoniteTerm, "Mediocrity; accepting less than the best");
        
        CSResult significanceStrength = new CSResult();
        significanceStrength.setId(CSStrengthScore.SIGNIFICANCE.getId());
        significanceStrength.setAssessmentId(csAssessment.getId());
        significanceStrength.setName(CSStrengthScore.SIGNIFICANCE.getLabel());
        significanceStrength.setDescription(CSStrengthScore.SIGNIFICANCE.getDescription());
        significanceStrength.addAssessmentCategoryId(influencingLeadershipTheme.getId());
        significanceStrength.addAssessmentCategoryId(makeItMatterStyle.getId());
        significanceStrength.addTerm(superpowerTerm, "Natural performer and contributor");
        significanceStrength.addTerm(kryptoniteTerm, "Being a cog in the machine; lack of recognition");
        
        CSResult arrangerStrength = new CSResult();
        arrangerStrength.setId(CSStrengthScore.ARRANGER.getId());
        arrangerStrength.setAssessmentId(csAssessment.getId());
        arrangerStrength.setName(CSStrengthScore.ARRANGER.getLabel());
        arrangerStrength.setDescription(CSStrengthScore.ARRANGER.getDescription());
        arrangerStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        arrangerStrength.addAssessmentCategoryId(structureStyle.getId());
        arrangerStrength.addTerm(superpowerTerm, "Organizing resources for maximum productivity");
        arrangerStrength.addTerm(kryptoniteTerm, "Static environment; others' resistance to change");
        
        CSResult consistencyStrength = new CSResult();
        consistencyStrength.setId(CSStrengthScore.CONSISTENCY.getId());
        consistencyStrength.setAssessmentId(csAssessment.getId());
        consistencyStrength.setName(CSStrengthScore.CONSISTENCY.getLabel());
        consistencyStrength.setDescription(CSStrengthScore.CONSISTENCY.getDescription());
        consistencyStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        consistencyStrength.addAssessmentCategoryId(structureStyle.getId());
        consistencyStrength.addTerm(superpowerTerm, "Creating and sticking to SOPs and fair practices");
        consistencyStrength.addTerm(kryptoniteTerm, "Broken rules; excessive customization");
        
        CSResult disciplineStrength = new CSResult();
        disciplineStrength.setId(CSStrengthScore.DISCIPLINE.getId());
        disciplineStrength.setAssessmentId(csAssessment.getId());
        disciplineStrength.setName(CSStrengthScore.DISCIPLINE.getLabel());
        disciplineStrength.setDescription(CSStrengthScore.DISCIPLINE.getDescription());
        disciplineStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        disciplineStrength.addAssessmentCategoryId(structureStyle.getId());
        disciplineStrength.addTerm(superpowerTerm, "Structuring and managing project details");
        disciplineStrength.addTerm(kryptoniteTerm, "Last-minute chaos; not respecting a plan");
        
        CSResult beliefStrength = new CSResult();
        beliefStrength.setId(CSStrengthScore.BELIEF.getId());
        beliefStrength.setAssessmentId(csAssessment.getId());
        beliefStrength.setName(CSStrengthScore.BELIEF.getLabel());
        beliefStrength.setDescription(CSStrengthScore.BELIEF.getDescription());
        beliefStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        beliefStrength.addAssessmentCategoryId(calibrateStyle.getId());
        beliefStrength.addTerm(superpowerTerm, "Living by core values; altruistic and trustworthy");
        beliefStrength.addTerm(kryptoniteTerm, "Lacking or compromising values");
        
        CSResult deliberativeStrength = new CSResult();
        deliberativeStrength.setId(CSStrengthScore.DELIBERATIVE.getId());
        deliberativeStrength.setAssessmentId(csAssessment.getId());
        deliberativeStrength.setName(CSStrengthScore.DELIBERATIVE.getLabel());
        deliberativeStrength.setDescription(CSStrengthScore.DELIBERATIVE.getDescription());
        deliberativeStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        deliberativeStrength.addAssessmentCategoryId(calibrateStyle.getId());
        deliberativeStrength.addTerm(superpowerTerm, "Conscientious, rational decision-making");
        deliberativeStrength.addTerm(kryptoniteTerm, "Cavalier and last-minute decision-making");
        
        CSResult restorativeStrength = new CSResult();
        restorativeStrength.setId(CSStrengthScore.RESTORATIVE.getId());
        restorativeStrength.setAssessmentId(csAssessment.getId());
        restorativeStrength.setName(CSStrengthScore.RESTORATIVE.getLabel());
        restorativeStrength.setDescription(CSStrengthScore.RESTORATIVE.getDescription());
        restorativeStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        restorativeStrength.addAssessmentCategoryId(calibrateStyle.getId());
        restorativeStrength.addTerm(superpowerTerm, "Getting to the root of problems and solving them");
        restorativeStrength.addTerm(kryptoniteTerm, "Ignoring or dismissing problems");
        
        CSResult achieverStrength = new CSResult();
        achieverStrength.setId(CSStrengthScore.ACHIEVER.getId());
        achieverStrength.setAssessmentId(csAssessment.getId());
        achieverStrength.setName(CSStrengthScore.ACHIEVER.getLabel());
        achieverStrength.setDescription(CSStrengthScore.ACHIEVER.getDescription());
        achieverStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        achieverStrength.addAssessmentCategoryId(completeStyle.getId());
        achieverStrength.addTerm(superpowerTerm, "Hard work and stamina to achieve goals efficiently");
        achieverStrength.addTerm(kryptoniteTerm, "Others' lack of diligence; lack of forward movement");
        
        CSResult focusStrength = new CSResult();
        focusStrength.setId(CSStrengthScore.FOCUS.getId());
        focusStrength.setAssessmentId(csAssessment.getId());
        focusStrength.setName(CSStrengthScore.FOCUS.getLabel());
        focusStrength.setDescription(CSStrengthScore.FOCUS.getDescription());
        focusStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        focusStrength.addAssessmentCategoryId(completeStyle.getId());
        focusStrength.addTerm(superpowerTerm, "Ability to prioritize and commit to goals");
        focusStrength.addTerm(kryptoniteTerm, "Tangents; lack of clear goals");
        
        CSResult responsibilityStrength = new CSResult();
        responsibilityStrength.setId(CSStrengthScore.RESPONSIBILITY.getId());
        responsibilityStrength.setAssessmentId(csAssessment.getId());
        responsibilityStrength.setName(CSStrengthScore.RESPONSIBILITY.getLabel());
        responsibilityStrength.setDescription(CSStrengthScore.RESPONSIBILITY.getDescription());
        responsibilityStrength.addAssessmentCategoryId(executingLeadershipTheme.getId());
        responsibilityStrength.addAssessmentCategoryId(completeStyle.getId());
        responsibilityStrength.addTerm(superpowerTerm, "Dependability, loyalty, and teamwork");
        responsibilityStrength.addTerm(kryptoniteTerm, "Disappointing others; others' lack of integrity");
        
        CSResult connectednessStrength = new CSResult();
        connectednessStrength.setId(CSStrengthScore.CONNECTEDNESS.getId());
        connectednessStrength.setAssessmentId(csAssessment.getId());
        connectednessStrength.setName(CSStrengthScore.CONNECTEDNESS.getLabel());
        connectednessStrength.setDescription(CSStrengthScore.CONNECTEDNESS.getDescription());
        connectednessStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        connectednessStrength.addAssessmentCategoryId(weAreOneStyle.getId());
        connectednessStrength.addTerm(superpowerTerm, "Holistic, global perspective; creating connections between people");
        connectednessStrength.addTerm(kryptoniteTerm, "An \"us vs. them\" or short-sighted mentality");
        
        CSResult empathyStrength = new CSResult();
        empathyStrength.setId(CSStrengthScore.EMPATHY.getId());
        empathyStrength.setAssessmentId(csAssessment.getId());
        empathyStrength.setName(CSStrengthScore.EMPATHY.getLabel());
        empathyStrength.setDescription(CSStrengthScore.EMPATHY.getDescription());
        empathyStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        empathyStrength.addAssessmentCategoryId(weAreOneStyle.getId());
        empathyStrength.addTerm(superpowerTerm, "Creating a safe space for all to express themselves");
        empathyStrength.addTerm(kryptoniteTerm, "Taking on others' feelings; feeling being ignored");
        
        CSResult harmonyStrength = new CSResult();
        harmonyStrength.setId(CSStrengthScore.HARMONY.getId());
        harmonyStrength.setAssessmentId(csAssessment.getId());
        harmonyStrength.setName(CSStrengthScore.HARMONY.getLabel());
        harmonyStrength.setDescription(CSStrengthScore.HARMONY.getDescription());
        harmonyStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        harmonyStrength.addAssessmentCategoryId(weAreOneStyle.getId());
        harmonyStrength.addTerm(superpowerTerm, "Calm mediation; bridging different perspectives");
        harmonyStrength.addTerm(kryptoniteTerm, "Conflict; lack of collaboration");
        
        CSResult developerStrength = new CSResult();
        developerStrength.setId(CSStrengthScore.DEVELOPER.getId());
        developerStrength.setAssessmentId(csAssessment.getId());
        developerStrength.setName(CSStrengthScore.DEVELOPER.getLabel());
        developerStrength.setDescription(CSStrengthScore.DEVELOPER.getDescription());
        developerStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        developerStrength.addAssessmentCategoryId(elevateAndCelebrateStyle.getId());
        developerStrength.addTerm(superpowerTerm, "Seeing and nurturing potential in others");
        developerStrength.addTerm(kryptoniteTerm, "Unfulfilled potential left unaddressed");
        
        CSResult includerStrength = new CSResult();
        includerStrength.setId(CSStrengthScore.INCLUDER.getId());
        includerStrength.setAssessmentId(csAssessment.getId());
        includerStrength.setName(CSStrengthScore.INCLUDER.getLabel());
        includerStrength.setDescription(CSStrengthScore.INCLUDER.getDescription());
        includerStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        includerStrength.addAssessmentCategoryId(elevateAndCelebrateStyle.getId());
        includerStrength.addTerm(superpowerTerm, "Embracing and promoting diversity, equity, and inclusion");
        includerStrength.addTerm(kryptoniteTerm, "Cliques, hierarchy, and injustice");
        
        CSResult positivityStrength = new CSResult();
        positivityStrength.setId(CSStrengthScore.POSITIVITY.getId());
        positivityStrength.setAssessmentId(csAssessment.getId());
        positivityStrength.setName(CSStrengthScore.POSITIVITY.getLabel());
        positivityStrength.setDescription(CSStrengthScore.POSITIVITY.getDescription());
        positivityStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        positivityStrength.addAssessmentCategoryId(elevateAndCelebrateStyle.getId());
        positivityStrength.addTerm(superpowerTerm, "Contagious enthusiasm and encouragement");
        positivityStrength.addTerm(kryptoniteTerm, "Debbie Downers; no space for joy and celebration");
        
        CSResult adaptabilityStrength = new CSResult();
        adaptabilityStrength.setId(CSStrengthScore.ADAPTABILITY.getId());
        adaptabilityStrength.setAssessmentId(csAssessment.getId());
        adaptabilityStrength.setName(CSStrengthScore.ADAPTABILITY.getLabel());
        adaptabilityStrength.setDescription(CSStrengthScore.ADAPTABILITY.getDescription());
        adaptabilityStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        adaptabilityStrength.addAssessmentCategoryId(openArmsStyle.getId());
        adaptabilityStrength.addTerm(superpowerTerm, "Calm in a crisis; flexible and readily supportive");
        adaptabilityStrength.addTerm(kryptoniteTerm, "A static, predictable environment");
        
        CSResult individualizationStrength = new CSResult();
        individualizationStrength.setId(CSStrengthScore.INDIVIDUALIZATION.getId());
        individualizationStrength.setAssessmentId(csAssessment.getId());
        individualizationStrength.setName(CSStrengthScore.INDIVIDUALIZATION.getLabel());
        individualizationStrength.setDescription(CSStrengthScore.INDIVIDUALIZATION.getDescription());
        individualizationStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        individualizationStrength.addAssessmentCategoryId(openArmsStyle.getId());
        individualizationStrength.addTerm(superpowerTerm, "Customizing solutions and opportunities to leverage what's unique in people");
        individualizationStrength.addTerm(kryptoniteTerm, "One-size-fits-all approaches for people and projects");
        
        CSResult relatorStrength = new CSResult();
        relatorStrength.setId(CSStrengthScore.RELATOR.getId());
        relatorStrength.setAssessmentId(csAssessment.getId());
        relatorStrength.setName(CSStrengthScore.RELATOR.getLabel());
        relatorStrength.setDescription(CSStrengthScore.RELATOR.getDescription());
        relatorStrength.addAssessmentCategoryId(relationshipBuildingLeadershipTheme.getId());
        relatorStrength.addAssessmentCategoryId(openArmsStyle.getId());
        relatorStrength.addTerm(superpowerTerm, "Deep, authentic personal connection");
        relatorStrength.addTerm(kryptoniteTerm, "Superficial small talk; impersonal hierarchy");
        
        ///////////////////////////
        
        EnnAssessment ennAssessment = new EnnAssessment();
        ennAssessment.setId("EN-assessment");
        ennAssessment.setSummary("Perspective");
        ennAssessment.setDescription("Invitation to choose your healthiest expression and shine your brightest");
        ennAssessment.setDisclaimer("");
        
        EnnResult reformer = EnneagramScore.REFORMER.getResultTemplate();
        reformer.setAssessmentId(ennAssessment.getId());
        reformer.setDescription("'"+EnneagramScore.REFORMER.getBurningQuestion() +
                                "' Aligns with the rules. Avoid harsh criticism and blame, and make mistakes safe. Empower them to repair and improve systems, so they feel principled.");
        reformer.addAssessmentCategoryId(aligningSocialStance.getId());
        reformer.addTerm(superpowerTerm, "Consistent excellence; optimizing systems; growth mindset");
        reformer.addTerm(kryptoniteTerm, "Mistakes, mediocrity; lack of integrity and accountability");
        
        EnnResult helper = EnneagramScore.HELPER.getResultTemplate();
        helper.setAssessmentId(ennAssessment.getId());
        helper.setDescription("'"+EnneagramScore.HELPER.getBurningQuestion() +
                              "' Aligns with relationships. Provide room for them to be build personal relationships. Explicitly communicate your appreciation through words, gifts and tangible actions so they feel connected.");
        helper.addAssessmentCategoryId(aligningSocialStance.getId());
        helper.addTerm(superpowerTerm, "Generous, supportive, and kind; community-builder");
        helper.addTerm(kryptoniteTerm, "Criticism; feeling overwhelmed by expectations");
                
        EnnResult achiever = EnneagramScore.ACHIEVER.getResultTemplate();
        achiever.setDescription("'"+EnneagramScore.ACHIEVER.getBurningQuestion() +
                                "' Assertive about success. Give them clear goals and tangible rewards for their hard work. Gold stars keep them motivated and moving forward.");
        achiever.setAssessmentId(ennAssessment.getId());
        achiever.addAssessmentCategoryId(assertiveSocialStance.getId());
        achiever.addTerm(superpowerTerm, "Goal-driven; public face; people skills");
        achiever.addTerm(kryptoniteTerm, "Criticism; looking bad; conflict; lack of goals");
        
        EnnResult individualist = EnneagramScore.INDIVIDUALIST.getResultTemplate();
        individualist.setAssessmentId(ennAssessment.getId());
        individualist.setDescription("'"+EnneagramScore.INDIVIDUALIST.getBurningQuestion() +
                                     "' Withdraws from the present. Create structure around their creativity, and then value and garner their insights. Give their work vision and substance so they feel meaningful.");
        individualist.addAssessmentCategoryId(withdrawingSocialStance.getId());
        individualist.addTerm(superpowerTerm, "Insight, emotional intelligence, and unique perspectives");
        individualist.addTerm(kryptoniteTerm, "Cog in the machine; inauthenticity; lack of aestheticism");
        
        EnnResult investigator = EnneagramScore.INVESTIGATOR.getResultTemplate();
        investigator.setAssessmentId(ennAssessment.getId());
        investigator.setDescription("'"+EnneagramScore.INVESTIGATOR.getBurningQuestion() +
                                    "' Withdraws from people. Allow them 'lab time' to explore a challenge so they can show up prepared-but keep them to deadlines. Value their timely results, even if incomplete, so they feel masterful.");
        investigator.addAssessmentCategoryId(withdrawingSocialStance.getId());
        investigator.addTerm(superpowerTerm, "Innovative, pioneering, studious, and masterful");
        investigator.addTerm(kryptoniteTerm, "Improvisation; risk-taking; violation of personal space/time");
        
        EnnResult loyalist = EnneagramScore.LOYALIST.getResultTemplate();
        loyalist.setAssessmentId(ennAssessment.getId());
        loyalist.setDescription("'"+EnneagramScore.LOYALIST.getBurningQuestion() +
                                "' Aligns with expectations. Clearly communicate what you need, give them the support to do it, and reinforce that they are on the right track to keep them centered.");
        loyalist.addAssessmentCategoryId(aligningSocialStance.getId());
        loyalist.addTerm(superpowerTerm, "Healthy skepticism; team cohesion; supportive leadership");
        loyalist.addTerm(kryptoniteTerm, "Risk, instability, and neglect; lack of clear expectations");
       
        EnnResult enthusiast = EnneagramScore.ENTHUSIAST.getResultTemplate();
        enthusiast.setAssessmentId(ennAssessment.getId());
        enthusiast.setDescription("'"+EnneagramScore.ENTHUSIAST.getBurningQuestion() +
                                  "' Assertive about freedom. Avoid micromanagement and give them new adventures to keep them engaged and happy.");
        enthusiast.addAssessmentCategoryId(assertiveSocialStance.getId());
        enthusiast.addTerm(superpowerTerm, "Fun, positive energy; inventive and adventurous");
        enthusiast.addTerm(kryptoniteTerm, "Routine, restriction and micromanagement; boredom");
        
        EnnResult challenger = EnneagramScore.CHALLENGER.getResultTemplate();
        challenger.setAssessmentId(ennAssessment.getId());
        challenger.setDescription("'"+EnneagramScore.CHALLENGER.getBurningQuestion() +
                                  "' Assertive about the truth. Keep them up-to-date about issues, challenges or performance breakdowns. Be direct and honest to maintain their trust.");
        challenger.addAssessmentCategoryId(assertiveSocialStance.getId());
        challenger.addTerm(superpowerTerm, "Heroic leadership; efficient resolution; standing up for others");
        challenger.addTerm(kryptoniteTerm, "Indirectness, inefficiency, injustice, and irresponsibility");
        
        EnnResult peacemaker = EnneagramScore.PEACEMAKER.getResultTemplate();
        peacemaker.setAssessmentId(ennAssessment.getId());
        peacemaker.setDescription("'"+EnneagramScore.PEACEMAKER.getBurningQuestion() +
                                  "' Withdraws from conflict. Encourage them to speak up, even if they feel uncomfortable doing so. Model respectfully rocking the boat, and then leverage their insights to reestablish stability, so they feel grounded.");
        peacemaker.addAssessmentCategoryId(withdrawingSocialStance.getId());
        peacemaker.addTerm(superpowerTerm, "Grounded leadership; mediation and holistic bridge-building");
        peacemaker.addTerm(kryptoniteTerm, "Conflict and disconnection; being put on the spot");        
        
        ///////////////////////////
        
        MBAssessment mbAssessment = new MBAssessment();
        mbAssessment.setId("MB-assessment");
        mbAssessment.setSummary("Personality");
        mbAssessment.setDescription("Invitation to create great relationships");
        mbAssessment.setDisclaimer("Myers-Briggs® is a registered trademark of Myers & Briggs Foundation in the United States and other countries.");

        MBPreference introvert = new MBPreference();
        introvert.setId(MBPreferenceScore.INTROVERT.getId());
        introvert.setName(MBPreferenceScore.INTROVERT.getLabel());
        introvert.setNickName(MBPreferenceScore.INTROVERT.getLabel());
        introvert.setDescription(MBPreferenceScore.INTROVERT.getDescription());
        
        MBPreference extrovert = new MBPreference();
        extrovert.setId(MBPreferenceScore.EXTROVERT.getId());
        extrovert.setName(MBPreferenceScore.EXTROVERT.getLabel());
        extrovert.setNickName(MBPreferenceScore.EXTROVERT.getLabel());
        extrovert.setDescription(MBPreferenceScore.EXTROVERT.getDescription());
        
        MBPreference intuitive = new MBPreference();
        intuitive.setId(MBPreferenceScore.INTUITIVE.getId());
        intuitive.setName(MBPreferenceScore.INTUITIVE.getLabel());
        intuitive.setNickName(MBPreferenceScore.INTUITIVE.getLabel());
        intuitive.setDescription(MBPreferenceScore.INTUITIVE.getDescription());
        
        MBPreference sensory = new MBPreference();
        sensory.setId(MBPreferenceScore.SENSORY.getId());
        sensory.setName(MBPreferenceScore.SENSORY.getLabel());
        sensory.setNickName(MBPreferenceScore.SENSORY.getLabel());
        sensory.setDescription(MBPreferenceScore.SENSORY.getDescription());
        
        MBPreference thinking = new MBPreference();
        thinking.setId(MBPreferenceScore.THINKING.getId());
        thinking.setName(MBPreferenceScore.THINKING.getLabel());
        thinking.setNickName(MBPreferenceScore.THINKING.getLabel());
        thinking.setDescription(MBPreferenceScore.THINKING.getDescription());
        
        MBPreference feeling = new MBPreference();
        feeling.setId(MBPreferenceScore.FEELING.getId());
        feeling.setName(MBPreferenceScore.FEELING.getLabel());
        feeling.setNickName(MBPreferenceScore.FEELING.getLabel());
        feeling.setDescription(MBPreferenceScore.FEELING.getDescription());
        
        MBPreference judging = new MBPreference();
        judging.setId(MBPreferenceScore.JUDGING.getId());
        judging.setName(MBPreferenceScore.JUDGING.getLabel());
        judging.setNickName(MBPreferenceScore.JUDGING.getLabel());
        judging.setDescription(MBPreferenceScore.JUDGING.getDescription());
        
        MBPreference perceiving = new MBPreference();
        perceiving.setId(MBPreferenceScore.PERCEIVING.getId());
        perceiving.setName(MBPreferenceScore.PERCEIVING.getLabel());
        perceiving.setNickName(MBPreferenceScore.PERCEIVING.getLabel());
        perceiving.setDescription(MBPreferenceScore.PERCEIVING.getDescription());
        
        MBResult intj = new MBResult();
        intj.setId(MBTypeScore.INTJ.getId());
        intj.setAssessmentId(mbAssessment.getId());
        intj.setName(MBTypeScore.INTJ.name());
        intj.setNickname("Octopus");
        intj.addAssessmentCategoryId(tenStepsAheadCultureCategory.getId());
        intj.addTerm(superpowerTerm, "Creative, imaginative; strategic, efficient problem-solving");
        intj.addTerm(kryptoniteTerm, "Cog in the machine; too much outside world");

        intj.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        intj.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        intj.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        intj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult entj = new MBResult();
        entj.setId(MBTypeScore.ENTJ.getId());
        entj.setAssessmentId(mbAssessment.getId());
        entj.setName(MBTypeScore.ENTJ.name());
        entj.setNickname("Bear");
        entj.addAssessmentCategoryId(tenStepsAheadCultureCategory.getId());
        entj.addTerm(superpowerTerm, "Strong, charismatic leadership; quick, strategic thinking; creativitiy; organization and structured expression");
        entj.addTerm(kryptoniteTerm, "Vulnerability and emotional expression");

        entj.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        entj.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        entj.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        entj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult istj = new MBResult();
        istj.setId(MBTypeScore.ISTJ.getId());
        istj.setAssessmentId(mbAssessment.getId());
        istj.setName(MBTypeScore.ISTJ.name());
        istj.setNickname("Beaver");
        istj.addAssessmentCategoryId(allForOneCultureCategory.getId());
        istj.addTerm(superpowerTerm, "Hardworking and dutiful; Jack of all trades");
        istj.addTerm(kryptoniteTerm, "Thinking creatively, on the spot");

        istj.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        istj.addPreference(MBPreferenceTheme.Input, sensory.getId());
        istj.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        istj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult estj = new MBResult();
        estj.setId(MBTypeScore.ESTJ.getId());
        estj.setAssessmentId(mbAssessment.getId());
        estj.setName(MBTypeScore.ESTJ.name());
        estj.setNickname("Lion");
        estj.addAssessmentCategoryId(allForOneCultureCategory.getId());
        estj.addTerm(superpowerTerm, "Strong, loyal leadership; organization; strategic, efficient planning");
        estj.addTerm(kryptoniteTerm, "People drama and emotional outbursts; unconventional situations; constant customization");

        estj.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        estj.addPreference(MBPreferenceTheme.Input, sensory.getId());
        estj.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        estj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult intp = new MBResult();
        intp.setId(MBTypeScore.INTP.getId());
        intp.setAssessmentId(mbAssessment.getId());
        intp.setName(MBTypeScore.INTP.name());
        intp.setNickname("Owl");
        intp.addAssessmentCategoryId(tenStepsAheadCultureCategory.getId());
        intp.addTerm(superpowerTerm, "Imaginative, original, open-minded, and analytical");
        intp.addTerm(kryptoniteTerm, "Micromanagement; emotional processing");

        intp.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        intp.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        intp.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        intp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult entp = new MBResult();
        entp.setId(MBTypeScore.ENTP.getId());
        entp.setAssessmentId(mbAssessment.getId());
        entp.setName(MBTypeScore.ENTP.name());
        entp.setNickname("Fox");
        entp.addAssessmentCategoryId(tenStepsAheadCultureCategory.getId());
        entp.addTerm(superpowerTerm, "Creative communication and brainstorming; original and strategic; devil's advocate");
        entp.addTerm(kryptoniteTerm, "Practical matters; boredom");

        entp.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        entp.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        entp.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        entp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult istp = new MBResult();
        istp.setId(MBTypeScore.ISTP.getId());
        istp.setAssessmentId(mbAssessment.getId());
        istp.setName(MBTypeScore.ISTP.name());
        istp.setNickname("Cat");
        istp.addAssessmentCategoryId(theresMoreInsideCultureCategory.getId());
        istp.addTerm(superpowerTerm, "Creative, practical, and mechanical; calm in a crisis");
        istp.addTerm(kryptoniteTerm, "Emotional processing; adhering to social norms");

        istp.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        istp.addPreference(MBPreferenceTheme.Input, sensory.getId());
        istp.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        istp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult estp = new MBResult();
        estp.setId(MBTypeScore.ESTP.getId());
        estp.setAssessmentId(mbAssessment.getId());
        estp.setName(MBTypeScore.ESTP.name());
        estp.setNickname("Cheetah");
        estp.addAssessmentCategoryId(wannaPlayCultureCategory.getId());
        estp.addTerm(superpowerTerm, "Enterpreneurial; social-and action-oriented; creative thinking and problem-solving");
        estp.addTerm(kryptoniteTerm, "Long-term planning; too much structure");

        estp.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        estp.addPreference(MBPreferenceTheme.Input, sensory.getId());
        estp.addPreference(MBPreferenceTheme.Decisions, thinking.getId());
        estp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult infj = new MBResult();
        infj.setId(MBTypeScore.INFJ.getId());
        infj.setAssessmentId(mbAssessment.getId());
        infj.setName(MBTypeScore.INFJ.name());
        infj.setNickname("Wolf");
        infj.addAssessmentCategoryId(theresMoreInsideCultureCategory.getId());
        infj.addTerm(superpowerTerm, "Creative, insightful; kind, helpful, loyal and altruistic");
        infj.addTerm(kryptoniteTerm, "Too much outside world; dry data and facts; conflict, negativity, and lack of planning");

        infj.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        infj.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        infj.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        infj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult enfj = new MBResult();
        enfj.setId(MBTypeScore.ENFJ.getId());
        enfj.setAssessmentId(mbAssessment.getId());
        enfj.setName(MBTypeScore.ENFJ.name());
        enfj.setNickname("Dog");
        enfj.addAssessmentCategoryId(wannaPlayCultureCategory.getId());
        enfj.addTerm(superpowerTerm, "Friendly and charismatic; leadership and social skills; creativity; creating structure");
        enfj.addTerm(kryptoniteTerm, "Impersonal reasoning and logical thinking; conflict, negativity; lack of planning");

        enfj.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        enfj.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        enfj.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        enfj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult isfj = new MBResult();
        isfj.setId(MBTypeScore.ISFJ.getId());
        isfj.setAssessmentId(mbAssessment.getId());
        isfj.setName(MBTypeScore.ISFJ.name());
        isfj.setNickname("Deer");
        isfj.addAssessmentCategoryId(allForOneCultureCategory.getId());
        isfj.addTerm(superpowerTerm, "Kind, helpful, loyal, altruistic; high standards; consistent systems");
        isfj.addTerm(kryptoniteTerm, "Speaking up and requesting what's needed; improvisation; breaking the rules");

        isfj.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        isfj.addPreference(MBPreferenceTheme.Input, sensory.getId());
        isfj.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        isfj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult esfj = new MBResult();
        esfj.setId(MBTypeScore.ESFJ.getId());
        esfj.setAssessmentId(mbAssessment.getId());
        esfj.setName(MBTypeScore.ESFJ.name());
        esfj.setNickname("Horse");
        esfj.addAssessmentCategoryId(allForOneCultureCategory.getId());
        esfj.addTerm(superpowerTerm, "Warm and friendly; excellent social skills; organization, planning, and administration");
        esfj.addTerm(kryptoniteTerm, "Impersonal reasoning and logical thinking; conflict, negativity; lack of planning");

        esfj.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        esfj.addPreference(MBPreferenceTheme.Input, sensory.getId());
        esfj.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        esfj.addPreference(MBPreferenceTheme.Structure, judging.getId());
        
        MBResult infp = new MBResult();
        infp.setId(MBTypeScore.INFP.getId());
        infp.setAssessmentId(mbAssessment.getId());
        infp.setName(MBTypeScore.INFP.name());
        infp.setNickname("Swan");
        infp.addAssessmentCategoryId(theresMoreInsideCultureCategory.getId());
        infp.addTerm(superpowerTerm, "Creative collaboration and brainstorming; clear beliefs, core values, and decisions");
        infp.addTerm(kryptoniteTerm, "Too much structure and time management; conflict");

        infp.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        infp.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        infp.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        infp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult enfp = new MBResult();
        enfp.setId(MBTypeScore.ENFP.getId());
        enfp.setAssessmentId(mbAssessment.getId());
        enfp.setName(MBTypeScore.ENFP.name());
        enfp.setNickname("Dolphin");
        enfp.addAssessmentCategoryId(wannaPlayCultureCategory.getId());
        enfp.addTerm(superpowerTerm, "Creative communication and brainstorming; clear beliefs, core values, and decisions");
        enfp.addTerm(kryptoniteTerm, "Administrative details; micromanagement");

        enfp.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        enfp.addPreference(MBPreferenceTheme.Input, intuitive.getId());
        enfp.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        enfp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult isfp = new MBResult();
        isfp.setId(MBTypeScore.ISFP.getId());
        isfp.setAssessmentId(mbAssessment.getId());
        isfp.setName(MBTypeScore.ISFP.name());
        isfp.setNickname("Panda");
        isfp.addAssessmentCategoryId(theresMoreInsideCultureCategory.getId());
        isfp.addTerm(superpowerTerm, "Artistic and imaginative; charming and sensitive");
        isfp.addTerm(kryptoniteTerm, "Too much structure and time management; long-term planning");

        isfp.addPreference(MBPreferenceTheme.Focus, introvert.getId());
        isfp.addPreference(MBPreferenceTheme.Input, sensory.getId());
        isfp.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        isfp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());
        
        MBResult esfp = new MBResult();
        esfp.setId(MBTypeScore.ESFP.getId());
        esfp.setAssessmentId(mbAssessment.getId());
        esfp.setName(MBTypeScore.ESFP.name());
        esfp.setNickname("Seal");
        esfp.addAssessmentCategoryId(wannaPlayCultureCategory.getId());
        esfp.addTerm(superpowerTerm, "Bold, original, and entertaining; people skills; clear beliefs, core values, and decisions");
        esfp.addTerm(kryptoniteTerm, "Long-term planning; conflict");

        esfp.addPreference(MBPreferenceTheme.Focus, extrovert.getId());
        esfp.addPreference(MBPreferenceTheme.Input, sensory.getId());
        esfp.addPreference(MBPreferenceTheme.Decisions, feeling.getId());
        esfp.addPreference(MBPreferenceTheme.Structure, perceiving.getId());        
        ///////////////////////////
        
        RoleType individualContributorRoleType = new RoleType();
        individualContributorRoleType.setId("rt-indivcontributor");
        individualContributorRoleType.setName("Individual Contributor");
        individualContributorRoleType.setDescription("Individual Contributor description goes here TJMTJM");
        
        RoleType executiveRoleType = new RoleType();
        executiveRoleType.setId("rt-executive");
        executiveRoleType.setName("Executive");
        executiveRoleType.setDescription("Executive description goes here TJMTJM");
        
        Role founderRole = new Role();
        founderRole.setName("Founder");
        founderRole.setDescription("Founder description goes here TJMTJM");
        founderRole.setRoleTypeId(executiveRoleType.getId());
        founderRole.setId("plume-fndr-role");
        
        Role educDesignerRole = new Role();
        educDesignerRole.setName("Educational Designer");
        educDesignerRole.setDescription("Educational Designer description goes here TJMTJM");
        educDesignerRole.setRoleTypeId(individualContributorRoleType.getId());
        educDesignerRole.setId("plume-ed-role");
        
        Role opsManagerRole = new Role();
        opsManagerRole.setName("Operations Manager");
        opsManagerRole.setDescription("Operations Manager description goes here TJMTJM");
        opsManagerRole.setRoleTypeId(individualContributorRoleType.getId());
        opsManagerRole.setId("plume-opsmgr-role");
        
        Role softwareDevRole = new Role();
        softwareDevRole.setName("Software Developer");
        softwareDevRole.setDescription("Software Developer description goes here TJMTJM");
        softwareDevRole.setRoleTypeId(individualContributorRoleType.getId());
        softwareDevRole.setId("bt-sd-role");
        
        ///////////////////////////
        
        Team parillumeDevTeam = new Team();
        parillumeDevTeam.setName("Parillume Dev Team");
        parillumeDevTeam.setDescription("Parillume Dev Team description goes here TJMTJM");
        parillumeDevTeam.setId("plume-devteam");
        
        Team parillumeOperationsTeam = new Team();
        parillumeOperationsTeam.setName("Parillume Operations Team");
        parillumeOperationsTeam.setDescription("Parillume Operations Team description goes here TJMTJM");
        parillumeOperationsTeam.setId("plume-opsteam");
        
        Team billtrustTeam = new Team();
        billtrustTeam.setName("Billtrust Ecomm Team");
        billtrustTeam.setDescription("Billtrust Ecomm Team description goes here TJMTJM");
        billtrustTeam.setId("bt-team");
        
        ///////////////////////////
        
        Company parillume = new Company();
        parillume.setName("Parillume");
        parillume.setId("plume-company");
        parillume.setGoogleFolderURL("TJMTJM Google folder URL");
        parillume.setUsername("lisa@parillume.com");
        parillume.setPassword("LSFAbundance");
        
        parillumeDevTeam.addCompanyId(parillume.getId());
        parillumeOperationsTeam.addCompanyId(parillume.getId());
        
        Company billtrust = new Company();
        billtrust.setName("Billtrust");
        billtrust.setId("bt-company");
        billtrust.setGoogleFolderURL("TJMTJM Google folder URL");
        billtrust.setUsername("tmargolis@billtrust.com");
        billtrust.setPassword("Qwerty$12");
        
        billtrustTeam.addCompanyId(billtrust.getId());
        
        ///////////////////////////  
        
        User lisaUser = new User();
        lisaUser.setId("U-LisaId");
        lisaUser.setNameFirst("Lisa");
        lisaUser.setNameLast("Foster");
        lisaUser.setEmailAddress("lisa@parillume.com");
        lisaUser.setPhoneNumber("720-481-8505");
        lisaUser.setCalendarType(CalendarType.GOOGLE);
        lisaUser.setEmailSchedule(EmailSchedule.WEEKLY);
        
        lisaUser.addRole(parillumeDevTeam.getId(), founderRole.getId());
        lisaUser.addRole(parillumeOperationsTeam.getId(), founderRole.getId());
        
        lisaUser.setPlayfullPractices(Arrays.asList("Work out regularly", "Put less food on plate"));
        lisaUser.setSuperpowers(Arrays.asList("My first superpower", "My second superpower"));
        lisaUser.setKryptonite(Arrays.asList("My first kryptonite", "My second kryptonite"));
        
        lisaUser.setAssessmentResultIds(Arrays.asList(empathyStrength.getId(), maximizerStrength.getId(), achiever.getId(), enfj.getId())); 
       
        User reneeUser = new User();
        reneeUser.setId("U-ReneeId");
        reneeUser.setNameFirst("Renee");
        reneeUser.setNameLast("Marino");
        reneeUser.setEmailAddress("renee@parillume.com");
        reneeUser.setPhoneNumber("123-456-7890");
        reneeUser.setCalendarType(CalendarType.MICROSOFT);
        reneeUser.setEmailSchedule(EmailSchedule.NEVER);

        reneeUser.addRole(billtrustTeam.getId(), softwareDevRole.getId());
        reneeUser.addRole(parillumeOperationsTeam.getId(), opsManagerRole.getId());
        reneeUser.addManager(parillumeOperationsTeam.getId(), lisaUser.getId());
        
        reneeUser.setPlayfullPractices(Arrays.asList("Renee PFP 1", "Renee PFP 2"));
        reneeUser.setSuperpowers(Arrays.asList("Renee first superpower", "Renee second superpower"));
        reneeUser.setKryptonite(Arrays.asList("Renee first kryptonite", "Renee second kryptonite"));
        
        reneeUser.setAssessmentResultIds(Arrays.asList(learnerStrength.getId(), includerStrength.getId(), achiever.getId(), infj.getId()));         
        
        User tomUser = new User();
        tomUser.setId("U-TomId");
        tomUser.setNameFirst("Tom");
        tomUser.setNameLast("Margolis");
        tomUser.setEmailAddress("tom@billtrust.com");
        tomUser.setPhoneNumber("720-346-4897");
        tomUser.setCalendarType(CalendarType.GOOGLE);
        tomUser.setEmailSchedule(EmailSchedule.WEEKDAYS);

        tomUser.addRole(billtrustTeam.getId(), softwareDevRole.getId());
        tomUser.addRole(parillumeDevTeam.getId(), educDesignerRole.getId());
        tomUser.addManager(billtrustTeam.getId(), reneeUser.getId());
        tomUser.addManager(parillumeDevTeam.getId(), lisaUser.getId());
        
        tomUser.setPlayfullPractices(Arrays.asList("Speak up more in meetings", "Create a daily plan each morning"));
        tomUser.setSuperpowers(Arrays.asList("My first superpower", "My second superpower"));
        tomUser.setKryptonite(Arrays.asList("My first kryptonite", "My second kryptonite"));
        
        tomUser.setAssessmentResultIds(Arrays.asList(ideationStrength.getId(), communicationStrength.getId(), challenger.getId(), intj.getId()));         
        
        ///////////////////////////  
        CorpusModel corpusModel = new CorpusModel();
        corpusModel.setTerms(Arrays.asList(superpowerTerm, kryptoniteTerm));
        corpusModel.setAssessmentCategoryTypes(Arrays.asList(leadershipThemeAssessmentCategoryType,
                                                             leadershipStyleAssessmentCategoryType,                                                                                
                                                             cultureCategoryAssessmentCategoryType, 
                                                             socialStanceAssessmentCategoryType));
        corpusModel.setAssessmentCategories(Arrays.asList(strategicLeadershipTheme, influencingLeadershipTheme,
                                                          executingLeadershipTheme, relationshipBuildingLeadershipTheme,

                                                          yesAndStyle, tellMeMoreStyle, butWaitStyle, letsGoStyle,
                                                          letsConnectStyle, makeItMatterStyle, structureStyle,
                                                          calibrateStyle, completeStyle, weAreOneStyle, elevateAndCelebrateStyle,
                                                          openArmsStyle,

                                                          wannaPlayCultureCategory, tenStepsAheadCultureCategory,
                                                          allForOneCultureCategory, theresMoreInsideCultureCategory,

                                                          assertiveSocialStance, aligningSocialStance, withdrawingSocialStance));
        corpusModel.setAssessments(Arrays.asList(csAssessment, ennAssessment, mbAssessment));
        corpusModel.setAssessmentResults(Arrays.asList(futuristicStrength, ideationStrength, strategicStrength,
                                                       contextStrength, inputStrength, learnerStrength,
                                                       analyticalStrength, intellectionStrength, 
                                                       activatorStrength, commandStrength, competitionStrength, selfAssuranceStrength,
                                                       communicationStrength, wooStrength,
                                                       maximizerStrength, significanceStrength,
                                                       arrangerStrength, consistencyStrength, disciplineStrength,
                                                       beliefStrength, deliberativeStrength, restorativeStrength,
                                                       achieverStrength, focusStrength, responsibilityStrength,
                                                       connectednessStrength, empathyStrength, harmonyStrength,
                                                       developerStrength, includerStrength, positivityStrength,
                                                       adaptabilityStrength, individualizationStrength, relatorStrength,

                                                       reformer, helper, achiever, individualist, investigator,
                                                       loyalist, enthusiast, challenger, peacemaker,

                                                       intj, entj, istj, estj,
                                                       intp, entp, istp, estp,
                                                       infj, enfj, isfj, esfj,
                                                       infp, enfp, isfp, esfp));
        corpusModel.setRoleTypes(Arrays.asList(individualContributorRoleType));

        CompanyModel parillumeCompanyModel = new CompanyModel();
        parillumeCompanyModel.setCompany(parillume);
        parillumeCompanyModel.setTeams(Arrays.asList(parillumeDevTeam, parillumeOperationsTeam));
        parillumeCompanyModel.setRoles(Arrays.asList(founderRole, educDesignerRole, opsManagerRole));
        parillumeCompanyModel.setUsers(Arrays.asList(lisaUser, tomUser, reneeUser));

//        CompanyModel billTrustCompanyModel = new CompanyModel();
//        billTrustCompanyModel.setCompany(billtrust);
//        billTrustCompanyModel.setTeams(Arrays.asList(billtrustTeam));
//        billTrustCompanyModel.setRoles(Arrays.asList(softwareDevRole));
//        billTrustCompanyModel.setUsers(Arrays.asList(tomUser));

        return MutablePair.of(corpusModel, parillumeCompanyModel);
    }
}