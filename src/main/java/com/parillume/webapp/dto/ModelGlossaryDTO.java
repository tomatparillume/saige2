/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.model.internal.AssessmentType;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.util.JSONUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *               
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class ModelGlossaryDTO {
    private static DescriptorListComparator comparator = new DescriptorListComparator();
    
    /*************************************
     * Descriptor Pairs are: {id, label} */
    private Map<AssessmentType, List< Pair<String,String> >> assessmentToDescriptors = new HashMap<>();
    
                 // {companyId, companyLabel}
    private Map< String, // Descriptor key is a String to support javascript interpretation
                 // {roleId, roleLabel}
                 List<Pair<String,String>> > companyRoleDescriptors = new TreeMap<>();
    
                 // {companyId, companyLabel}
    private Map< String, // Descriptor key is a String to support javascript interpretation
                 // {teamId, teamLabel}
                 List<Pair<String,String>> > companyTeamDescriptors = new TreeMap<>();
    
                 // {companyId, companyLabel}
    private Map< String, // Descriptor key is a String to support javascript interpretation
                 // {userId, userLabel}
                 List<Pair<String,String>> > companyUserDescriptors = new TreeMap<>();

    public ModelGlossaryDTO(CompanyModel companyModel, List<CompanyModel> allCompanies) 
    throws Exception {
        populateAssessmentDescriptors();
        populateCompanyDescriptors(allCompanies);
    }
    
    private void populateAssessmentDescriptors() {
        List<Pair<String,String>> csDescriptors = Arrays.asList(CSStrengthScore.values())
                                                        .stream()
                                                        .map(cs -> MutablePair.of(cs.getId(), cs.getLabel()))
                                                        .collect(Collectors.toList());
        List<Pair<String,String>> mbDescriptors = Arrays.asList(MBTypeScore.values())
                                                        .stream()
                                                        .map(mb -> MutablePair.of(mb.getId(), mb.name()))
                                                        .collect(Collectors.toList());
        List<Pair<String,String>> enDescriptors = Arrays.asList(EnneagramScore.values())
                                                        .stream()
                                                        .map(en -> MutablePair.of(en.getId(), en.getLabel()))
                                                        .collect(Collectors.toList());
        Collections.sort(csDescriptors, comparator);
        Collections.sort(mbDescriptors, comparator);
        Collections.sort(enDescriptors, comparator);
        
        assessmentToDescriptors.put(AssessmentType.CliftonStrengths, csDescriptors);
        assessmentToDescriptors.put(AssessmentType.MyersBriggs, mbDescriptors);
        assessmentToDescriptors.put(AssessmentType.Enneagram, enDescriptors);
    }
    
    private void populateCompanyDescriptors(List<CompanyModel> allCompanies) 
    throws Exception {
        for(CompanyModel companyModel: allCompanies) {
            Company company = companyModel.getCompany();
            
            Pair<String,String> companyDescriptor = MutablePair.of(company.getId(), company.getName());
            String companyDescriptorStr = JSONUtil.toJSON(companyDescriptor);
            
            List<Pair<String,String>> roleDescriptors = companyModel.getRoles()
                                                                    .stream()
                                                                    .map(r -> MutablePair.of(r.getId(), r.getName()))
                                                                    .collect(Collectors.toList());       
            companyRoleDescriptors.put(companyDescriptorStr, roleDescriptors);
            
            List<Pair<String,String>> teamDescriptors = companyModel.getTeams()
                                                                    .stream()
                                                                    .map(t -> getTeamDescriptor(t))
                                                                    .collect(Collectors.toList());
            companyTeamDescriptors.put(companyDescriptorStr, teamDescriptors);
            
            List<User> teamUsers = companyModel.getUsers();
            List<User> noTeamUsers = companyModel.getNoTeamUsers();
            teamUsers.removeAll(noTeamUsers);
            
            List<Pair<String,String>> userDescriptors = teamUsers.stream()
                                                                 .map(u -> getUserDescriptor(u))
                                                                 .collect(Collectors.toList()); 
            companyUserDescriptors.put(companyDescriptorStr, userDescriptors);
             
            if(!noTeamUsers.isEmpty()) {
                NoTeamDTO noTeamDTO = new NoTeamDTO(noTeamUsers);
                //teamDescriptors.add( getTeamDescriptor(noTeamDTO.toNoTeam()) );
                userDescriptors.addAll( noTeamDTO.getUsers()
                                                 .stream()
                                                 .map(u -> getUserDescriptor(u))
                                                 .collect(Collectors.toList()) );
            }
        }
    }
    
    private Pair<String,String> getTeamDescriptor(Team team) {
        return MutablePair.of(team.getId(), team.getName());        
    }
    
    private Pair<String,String> getUserDescriptor(User user) {
        return MutablePair.of(user.getId(), user.getNameLast()+", "+user.getNameFirst());
    }

    private static class DescriptorListComparator implements Comparator<Pair<String,String>> {
        @Override
        public int compare(Pair<String, String> p1, Pair<String, String> p2) {
            return p1.getLeft().compareTo(p2.getLeft());
        }
    }
}