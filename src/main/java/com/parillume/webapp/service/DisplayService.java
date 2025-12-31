/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.service;

import org.springframework.stereotype.Service;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.external.User;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import com.parillume.model.internal.EnnResult;
import com.parillume.model.internal.MBResult;
import com.parillume.util.Constants;
import com.parillume.util.StringUtil;
import com.parillume.util.model.CorpusUtil;
import com.parillume.util.webapp.WebAppDisplayUtil;
import com.parillume.webapp.dto.RowDataDTO;
import com.parillume.webapp.dto.TableCellDTO;
import com.parillume.webapp.dto.TableCellLineDTO;
import com.parillume.webapp.dto.TableDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class DisplayService {
    public TableDTO generateTable(CompanyModel company, CorpusModel corpus, Collection<String> columnIds) {

        TableDTO tableDTO = new TableDTO(company);

        List<String> columnHeaders = new ArrayList( Arrays.asList(
                                                    "Name",
                                                    AssessmentType.CliftonStrengths.getLabel(),
                                                    AssessmentType.MyersBriggs.getLabel(),
                                                    AssessmentType.Enneagram.getLabel(), 
                                                    "Superpowers","Kryptonite","PFPs") );
//        if(columnIds != null) {
//            for(Iterator<String> iter = columnHeaders.iterator(); iter.hasNext();) {
//                String columnId = StringUtil.toId(iter.next());
//                if( !columnIds.contains(columnId) )
//                    iter.remove();
//            }
//        } else { 
            // We return data about all columnHeaders to the UI, and the UI
            // caches that data and filters it when column headers are clicked.
            columnIds = columnHeaders.stream() 
                                     .map( c -> StringUtil.toId(c) ) 
                                     .collect( Collectors.toList() ) ;
//        }
        
        List<TableCellDTO> columnContentList = columnHeaders.stream()
                                                            .map(c -> new TableCellDTO(c))
                                                            .collect(Collectors.toList());        
        tableDTO.setColumns(columnContentList);

        List<User> users = company.getUsers();
        // This also calls sortByName:
        Map<User,String> userToDisplayName = WebAppDisplayUtil.getDisplayNames(users);

        for(User user: users) {
            RowDataDTO row = new RowDataDTO();
            tableDTO.addRow(row);

            if(columnIds == null || columnIds.contains("name")) 
                row.addCell("name", userToDisplayName.get(user));

            List<String> userResultIds = new ArrayList(user.getAssessmentResultIds());
            Collections.sort(userResultIds);

            List<TableCellLineDTO> superpowers = new ArrayList<>();
            List<TableCellLineDTO> kryptonite = new ArrayList<>();
            
            // Get the history of percent-in-superpowers, in ascending time order
            Integer latestPercentageSuperpowers = user.getLatestPercentageSuperpowers();
            TableCellLineDTO percentSupLine = new TableCellLineDTO("% in superpowers: " + 
                                                                   (latestPercentageSuperpowers != null ? String.valueOf(latestPercentageSuperpowers) : "N/A"));
            superpowers.add(percentSupLine);
            
            /*******************************************************************
             * The following 3 "if" statements suppress the display of superpowers
             * and kryptonite in the results table if the associated assessment
             * column (e.g. enneagram) is not displayed. 
             * However, we want to display sup/kryp regardless of whether the
             * associated assessment column is displayed; thus, "if" is commented out.
             */
            //if(columnIds == null || columnIds.contains("cliftonstrengths")) {
                List<String> csLines = getCSLines(corpus, user, superpowers, kryptonite);
                row.addCell("cliftonstrengths", csLines);
            //}
            
            //if(columnIds == null || columnIds.contains("myersbriggs")) {                
                TableCellLineDTO mbLine = getMBLine(corpus, user, superpowers, kryptonite);
                row.addCell("myersbriggs", mbLine);
            //}
            
            //if(columnIds == null || columnIds.contains("enneagram")) {
                TableCellLineDTO ennLine = getEnnLine(corpus, user, superpowers, kryptonite);
                row.addCell("enneagram", ennLine);
            //}
            /*******************************************************************/
            
            if(columnIds == null || columnIds.contains("superpowers")) 
                row.addCellDTOs("superpowers", superpowers);
            
            if(columnIds == null || columnIds.contains("kryptonite")) 
                row.addCellDTOs("kryptonite", kryptonite); 
            
            if(columnIds == null || columnIds.contains("pfps")) {
                List<String> pfps = user.getPlayfullPractices();
                if(pfps == null)
                    pfps = new ArrayList<>();
                
                row.addCellDTOs("pfps", pfps.stream()
                                            .map(p -> new TableCellLineDTO(p))
                                            .collect(Collectors.toList()) );
            }
        }

        return tableDTO;        
    }
    
    private TableCellLineDTO getEnnLine(CorpusModel corpus,
                                        User user,
                                        List<TableCellLineDTO> superpowers, 
                                        List<TableCellLineDTO> kryptonite) {        
        Map<String,AssessmentResult> ennResultsCorpus = CorpusUtil.getAssessmentResultsById(corpus, AssessmentType.Enneagram);
        List<String> userEnnResultIds = CorpusUtil.getResultIdsForUser(ennResultsCorpus, user);
        
        if(!userEnnResultIds.isEmpty()) {            
            List<String> superpowerResultIds = user.getSuperpowerAssessmentResultIds();                  
            collectSuperpowers(superpowers, superpowerResultIds, ennResultsCorpus);
            
            
            List<String> kryptoniteResultIds = user.getKryptoniteAssessmentResultIds(); 
            collectKryptonite(kryptonite, kryptoniteResultIds, ennResultsCorpus);
            
            EnnResult ennResult = (EnnResult) ennResultsCorpus.get(userEnnResultIds.get(0));
            return new TableCellLineDTO(ennResult.getName()+": "+ennResult.getNickname());
        } else {
            return new TableCellLineDTO("N/A");
        }
    }
    
    private TableCellLineDTO getMBLine(CorpusModel corpus,
                                       User user,
                                       List<TableCellLineDTO> superpowers, 
                                       List<TableCellLineDTO> kryptonite) {
        Map<String,AssessmentResult> mbResultsCorpus = CorpusUtil.getAssessmentResultsById(corpus, AssessmentType.MyersBriggs);
        List<String> userMBResultIds = CorpusUtil.getResultIdsForUser(mbResultsCorpus, user);
        
        if(!userMBResultIds.isEmpty()) {
            List<String> superpowerResultIds = user.getSuperpowerAssessmentResultIds();                  
            collectSuperpowers(superpowers, superpowerResultIds, mbResultsCorpus);
            
            List<String> kryptoniteResultIds = user.getKryptoniteAssessmentResultIds(); 
            collectKryptonite(kryptonite, kryptoniteResultIds, mbResultsCorpus);
            
            MBResult mbResult = (MBResult) mbResultsCorpus.get(userMBResultIds.get(0));
            return new TableCellLineDTO(mbResult.getName()+" "+mbResult.getNickname());
        } else {
            return new TableCellLineDTO("N/A");
        }
    }
    
    private List<String> getCSLines(CorpusModel corpus,
                                    User user,
                                    List<TableCellLineDTO> superpowers, 
                                    List<TableCellLineDTO> kryptonite) {
        
        Map<String,AssessmentResult> csResultsCorpus = CorpusUtil.getAssessmentResultsById(corpus, AssessmentType.CliftonStrengths);
        List<String> userCSResultIds = CorpusUtil.getResultIdsForUser(csResultsCorpus, user);
        
        if(!userCSResultIds.isEmpty()) { 
            List<String> superpowerResultIds = user.getSuperpowerAssessmentResultIds();                  
            collectSuperpowers(superpowers, superpowerResultIds, csResultsCorpus);
            Collections.sort(superpowers, TableCellLineDTO.LINE_COMPARATOR);
            
            List<String> kryptoniteResultIds = user.getKryptoniteAssessmentResultIds(); 
            collectKryptonite(kryptonite, kryptoniteResultIds, csResultsCorpus);
            Collections.sort(kryptonite, TableCellLineDTO.LINE_COMPARATOR);
            
            return userCSResultIds.stream()
                                  .map(s -> csResultsCorpus.get(s).getName())
                                  .collect(Collectors.toList());
        } else {
            return Arrays.asList("N/A");
        }
    }
            
    private void collectSuperpowers(List<TableCellLineDTO> superpowers,
                                    List<String> userAssessmentResultIds,
                                    Map<String,AssessmentResult> assessmentResultsCorpus) {
        if(superpowers != null) {
            List<TableCellLineDTO> superpowersLines = getTermValues(userAssessmentResultIds, assessmentResultsCorpus, Constants.SUPERPOWER_TERMID);
            superpowers.addAll(superpowersLines);
        }
    }
    private void collectKryptonite(List<TableCellLineDTO> kryptonite,
                                   List<String> userAssessmentResultIds,
                                   Map<String,AssessmentResult> assessmentResultsCorpus) {
        if(kryptonite != null) {
            List<TableCellLineDTO> kryptoniteLines = getTermValues(userAssessmentResultIds, assessmentResultsCorpus, Constants.KRYPTONITE_TERMID);
            kryptonite.addAll(kryptoniteLines);
        }
    }    
    private List<TableCellLineDTO> getTermValues(List<String> userResultIds,
                                                 Map<String,AssessmentResult> resultsCorpus,
                                                 String termId) {
        return userResultIds.stream()
                            .filter(s -> resultsCorpus.containsKey(s))
                            .map(s -> 
                                 new TableCellLineDTO(resultsCorpus.get(s).getName(),
                                                      resultsCorpus.get(s).getTermIdToValue().get(termId))
                            )
                            .collect(Collectors.toList());        
    }    
}
