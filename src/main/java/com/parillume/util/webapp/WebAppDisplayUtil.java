/*
 * Copyright(c) 2024, Parillume Inc., All rights reserved worldwide
 */
package com.parillume.util.webapp;

import com.parillume.model.external.User;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.TeamDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class WebAppDisplayUtil {
    /**
     * Return Map of key=User, value=[display name = first name + last initials]
     */
    public static Map<User,String> getDisplayNames(List<User> users) {
        sortUsersByName(users);
        
        Map<String,List<User>> firstNameToUsers = new HashMap<>();
        for(User user: users) {
            String firstName = user.getNameFirst();
            
            List<User> usersWithFirstName = firstNameToUsers.get(firstName);
            if(usersWithFirstName == null) {
                usersWithFirstName = new ArrayList<>();
                firstNameToUsers.put(firstName, usersWithFirstName);
            }
            usersWithFirstName.add(user);
        }
        
        Map<User,String> retMap = new HashMap<>();
        
        for(String firstName: firstNameToUsers.keySet()) {
            List<User> usersWithFirstName = firstNameToUsers.get(firstName);

            if(usersWithFirstName.size() == 1) {
                User user = usersWithFirstName.get(0);
                retMap.put(user, user.getNameFirst());
                continue;
            }
            
            int sharedLastNameLetterCount = getSharedLastNameLetterCount(usersWithFirstName);
            int lastIndex = sharedLastNameLetterCount + 1;

            Map<User,String> map = usersWithFirstName.stream().collect(
                Collectors.toMap(
                    u -> u, 
                    u -> u.getNameFirst() + " " + 
                         u.getNameLast().substring(0, ( u.getNameLast().length() >= lastIndex ?
                                                        lastIndex : u.getNameLast().length() )
                                                  )
                ) 
            );
            
            retMap.putAll(map);
        }
        
        return retMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                     .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
    }
    
    private static int getSharedLastNameLetterCount(List<User> usersWithFirstName) {
        sortUsersByName(usersWithFirstName);
        
        int sharedLastNameLetterCount = 0;
        
        String previousLastName = null;
        for(User user: usersWithFirstName) {
            String lastName = user.getNameLast();
            
            if(previousLastName != null) {
                int sharedPrefixCount = StringUtil.getSharedPrefixCount(previousLastName, lastName);
                if(sharedPrefixCount > sharedLastNameLetterCount)
                    sharedLastNameLetterCount = sharedPrefixCount;
            }
                    
            previousLastName = lastName;
        }
        
        return sharedLastNameLetterCount;
    }
    
    public static void sortUsersByName(List<User> users) {
        if(users == null)
            return;
        
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return (u1.getNameFirst()+u1.getNameLast())
                       .compareTo( u2.getNameFirst()+u2.getNameLast() );
            }
        });        
    }
}
