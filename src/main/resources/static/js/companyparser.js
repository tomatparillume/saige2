function filterInsights(companyJSON, insightsFormQueryJSON) {
    var users = companyJSON.users;
    if(!users || users.length == 0)
        return [];
    
    /****** SEARCH BY TEAM MEMBER **********************************************
     * Finds array elements that are like:
     *  { "name": "userid_teaminsights", "value": "10f3a9d8ffb64c4cab68767931b76fd8" }
     */
    var dupUserIds = new Set();
    var userIdElmts = insightsFormQueryJSON.filter(elmt => {
                            return private_filterArray(elmt, dupUserIds, 'userid_');
                        });
    var userIds = userIdElmts.map(elmt => elmt.value); 
    if(userIds.length > 0) {        
        return users.filter(userElmt => {
                                // Does this user's id match any of the requested userIds?
                                return userIds.includes(userElmt.id);
                            });
    }
    
    /****** SEARCH BY ASSESSMENT RESULTS ***************************************
     * Finds array elements that are like:
     *  { "name": "assessmentresultid", "value": "CS-context" }
     */    
    var dupAssessmentIds = new Set();
    var assessmentIdElmts = insightsFormQueryJSON.filter(elmt => {
                                    return private_filterArray(elmt, dupAssessmentIds, 'assessmentresultid');
                                });  
    var assessmentIds = assessmentIdElmts.map(elmt => elmt.value);
    if(assessmentIds.length > 0) { 
        return users.filter(userElmt => {
                                // Does this user's assessment result ids include any of the requested assessmentIds?
                                return userElmt.assessmentResultIds.some(value => assessmentIds.includes(value));
                            });
    }
    
    return [];
}
function private_filterArray(element, elementSet, namePrefix) {
    var elmtStr = JSON.stringify(element);
    if(elementSet.has(elmtStr) || !element.name.startsWith(namePrefix)) 
        return false;

    elementSet.add(elmtStr);
    return true;    
}

function parseTeams(companyJSON) {
    var teams = [];

    var teamIdToName = new Map();
    var teamsArray = companyJSON.teams;
    $.each(teamsArray, function(index,team) { 
        teamIdToName.set(team.id, team.name);
    });
    
    var usersArray = companyJSON.users;
    $.each(usersArray, function(index,user) { 
        var teamIds = Object.keys(user.teamIdToManagerId);
        teamIds = teamIds.concat( Object.keys(user.teamIdToRoleId) ); 
        
        ////// If this user is not associated with any teams:
        if(teamIds.length == 0) {
            private_processTeam('no-team', 'No team', teams, user);
        
        ////// ... else: This user is associated with one or more teams:
        } else {        
            for(var teamId of teamIds) {
                if(teamIdToName.has(teamId)) 
                    private_processTeam(teamId, teamIdToName.get(teamId), teams, user);
            }
        }
    });
    
    return teams;
}   
function private_processTeam(teamId, teamName, teams, user) {
    var team = teams.find(obj => obj['id'] === teamId);
    if(!team)  {                         
        var team = new Object();
        team.id = teamId;
        team.name = teamName;
        team.users = [];
        teams.push(team);
    }
    
    var hasUser = team.users.some(obj => obj['id'] === user.id);
    if(!hasUser)
        team.users.push(user);    
}