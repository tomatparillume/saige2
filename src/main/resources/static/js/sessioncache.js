<!-- Display-data caching; caching data avoids unnecessary endpoint calls -->

function cacheSkippedColumn(columnId) {
    var columnIdsArrayStr = getCachedId('skippedTableColumns');
    var arrayToCache = '';

    if(columnIdsArrayStr==null || columnIdsArrayStr.length==0)
        arrayToCache = [];
    else
        arrayToCache = JSON.parse(columnIdsArrayStr);

    arrayToCache.push(columnId);
    cacheId( 'skippedTableColumns', JSON.stringify(arrayToCache) );          
}
function clearCachedSkippedColumn(columnId) {
    var columnIdsArrayStr = getCachedId('skippedTableColumns');
    if(columnIdsArrayStr==null || columnIdsArrayStr.length==0) 
        return;

    var columnIdsArray = JSON.parse(columnIdsArrayStr);
    var index = columnIdsArray.indexOf(columnId);
    if(index > -1) {
        columnIdsArray.splice(index, 1);
        cacheId( 'skippedTableColumns', JSON.stringify(columnIdsArray) );
    }
}
function clearCachedSkippedColumns() {
    clearCachedId('skippedTableColumns');
}
function hasCachedSkippedColumn(columnId) {
    var columnIdsArrayStr = getCachedId('skippedTableColumns');
    return (columnIdsArrayStr==null || columnIdsArrayStr.length==0) ?
            false :
            JSON.parse(columnIdsArrayStr).includes(columnId);
}
function cacheProxyingAdminLoginDTO(jsonStr) {
    if(jsonStr) {
        var jsonObj = JSON.parse(jsonStr);
        jsonStr = JSON.stringify(jsonObj,null,2);
        cacheId('proxyingAdminLoginDTO', jsonStr);
    }
}
function getCachedProxyingAdminLoginDTO() {
    var jsonStr = getCachedId('proxyingAdminLoginDTO');   
    return jsonStr && typeof jsonStr !== "undefined" ? 
           jsonStr : '';
}
function clearCachedProxyingAdminLoginDTO() {
    clearCachedId('proxyingAdminLoginDTO'); 
}
function cacheNewUserTemplate(jsonStr) {
    if(jsonStr) {
        var jsonObj = JSON.parse(jsonStr);
        jsonStr = JSON.stringify(jsonObj,null,2);
        cacheId('newUserTemplate', jsonStr);
    }
}
function getCachedNewUserTemplate() {
    var jsonStr = getCachedId('newUserTemplate');   
    return jsonStr && typeof jsonStr !== "undefined" ? 
           jsonStr : '';
}
function cacheCompanyJSON(json) {       
    if(json)
        cacheId('companyJSON', JSON.stringify(json,null,2)); 
    else 
        clearCachedCompanyJSON();
}
function getCachedCompanyJSON() { 
    var jsonStr = getCachedId('companyJSON'); 
    return jsonStr ? jsonStr : '';
} 
function clearCachedCompanyJSON() {
    clearCachedId('companyJSON');
}
function cacheInsightsTableJSON(json) {
    var jsonStr = JSON.stringify(json);
    cacheId(isFilterByTeamMembers() ? 
            'insightsTableJSON_byTeamMembers' :
            'insightsTableJSON_byAssessments', 
            jsonStr);                
}
function getCachedInsightsTableJSON(filterByTeamMembers) { 
    var jsonStr = (filterByTeamMembers==true) ?
                   getCachedId('insightsTableJSON_byTeamMembers') :
                   getCachedId('insightsTableJSON_byAssessments');
    return jsonStr ? JSON.parse(jsonStr) : '';
}  
function cacheTeamsJSON(json) { 
    cacheId('teamsJSON', JSON.stringify(json)); 
}   
function getCachedTeamsJSON() { 
    var jsonStr = getCachedId('teamsJSON'); 
    return jsonStr ? JSON.parse(jsonStr) : '';
}    
function cacheTeamsJSON(json) { 
    cacheId('teamsJSON', JSON.stringify(json)); 
}   

function cacheLoggedInUserJSON(json) { 
    cacheId('loggedInUserJSON', JSON.stringify(json)); 
}
function getCachedLoggedInUserJSON() { 
    var jsonStr = getCachedId('loggedInUserJSON'); 
    return jsonStr ? JSON.parse(jsonStr) : '';
} 
function clearCachedLoggedInUserJSON() { 
    clearCachedId('loggedInUserJSON'); 
}  

function cacheUserSupKrypCount(count) {
    cacheId('supkrypCount', count);
}
// Options to select team members in order to display their insights
function cacheTeamMembersInsightsSelection(data) { cacheId('teamMembersInsightsSelection', data); }
function getCachedTeamMembersInsightsSelection() { return getCachedId('teamMembersInsightsSelection'); }
// Options to select team members in order to define chat targets
function cacheTeamChatTargetsSelection(data) { cacheId('teamChatTargetsSelection', data); }
function getCachedTeamChatTargetsSelection() { return getCachedId('teamChatTargetsSelection'); }
// Options for an Admin to select team members in order to edit their profiles     
function cacheUserProfileLinks(data) { cacheId('userProfileLinks', data); }
function getCachedUserProfileLinks() { return getCachedId('userProfileLinks'); }             
// Options to select team members in order to delete their profiles   
function cacheUserDeletionLinks(data) { cacheId('userDeletionLinks', data); }
function getCachedUserDeletionLinks() { return getCachedId('userDeletionLinks'); } 
function hasCachedCompanyData() {
    return getCachedCompanyJSON() && getCachedTeamsJSON() && 
           getCachedTeamMembersInsightsSelection() &&
           getCachedUserProfileLinks() && getCachedUserDeletionLinks();
}
function clearCachedCompanyData() {
    clearCachedId('companyJSON');
    clearCachedId('teamsJSON');
    clearCachedId('teamMembersInsightsSelection'); 
    clearCachedId('userProfileLinks'); 
    clearCachedId('userDeletionLinks'); 
}

function cacheCompanyTeamsSelection(data) { cacheId('companyTeamsSelection', data); }
function getCachedCompanyTeamsSelection() { return getCachedId('companyTeamsSelection'); }

// Options for a user to self-edit their assessment insights:
function cacheAssmtMyProfileSelectionDisplay(data) { cacheId('assmtMyProfileSelectionDisplay', data); }
function getCachedAssmtMyProfileSelectionDisplay() { return getCachedId('assmtMyProfileSelectionDisplay'); }
// Options for a user to self-edit their superpowers:
function cacheSupMyProfileSelectionDisplay(data) { cacheId('supMyProfileSelectionDisplay', data); }
function getCachedSupMyProfileSelectionDisplay() { return getCachedId('supMyProfileSelectionDisplay'); }
// Options for a user to self-edit their kryptonite:
function cacheKrypMyProfileSelectionDisplay(data) { cacheId('krypMyProfileSelectionDisplay', data); }
function getCachedKrypMyProfileSelectionDisplay() { return getCachedId('krypMyProfileSelectionDisplay'); }
// Options for a user to self-edit their team membership:
function cacheTeamMembershipMyProfileDisplay(data) { cacheId('teamMembershipMyProfileDisplay', data); }
function getCachedTeamMembershipMyProfileDisplay() { return getCachedId('teamMembershipMyProfileDisplay'); }
function hasCachedMyProfileDisplay() {
    return getCachedAssmtMyProfileSelectionDisplay() &&
           getCachedSupMyProfileSelectionDisplay() && 
           getCachedKrypMyProfileSelectionDisplay() &&
           getCachedTeamMembershipMyProfileDisplay();
}
function clearCachedMyProfileDisplay() {
    clearCachedId('assmtMyProfileSelectionDisplay'); 
    clearCachedId('supMyProfileSelectionDisplay'); 
    clearCachedId('krypMyProfileSelectionDisplay'); 
    clearCachedId('teamMembershipMyProfileDisplay'); 
}

// Options to select assessments in order to filter the insights table.            
function cacheAssessmentFiltersDisplay(data) { cacheId('assessmentOptionsDisplay', data); }
function getCachedAssessmentFiltersDisplay() { return getCachedId('assessmentOptionsDisplay'); }

function cacheGlossaryDisplay(data) { cacheId('glossaryDisplay', data); }
function getCachedGlossaryDisplay() { return getCachedId('glossaryDisplay'); } 
function clearCachedGlossaryDisplay() { clearCachedId('glossaryDisplay'); }

function cacheLoggingOut() {
    cacheId('loggingout', true);
}
function isLoggingOut() {
    return getCachedId('loggingout');
}
function clearLoggingOut() {
    clearCachedId('loggingout');
}
function cacheClickedAdminEditLink(userId) {
    cacheId('clickedAdminEditLink', userId);
}
function getCachedClickedAdminEditLink() {
    return getCachedId('clickedAdminEditLink');
}

// As opposed to a logged-in company user:
function cacheLoggedInUserId(userId) {
    cacheId('loggedInUserId', userId);
}
function getCachedLoggedInUserId() {
    return getCachedId('loggedInUserId');
}
function cacheAssessmentData(assessmentId, data) {
    cacheId(assessmentId, data);         
}
function getCachedAssessmentData(assessmentId) {
    return getCachedId(assessmentId);
}
function cacheCurrentPageId(currentPageId) {
    cacheId('currentPageId', currentPageId);
}
function getCachedCurrentPageId() {
    return getCachedId('currentPageId');
}
function cacheDestinationPageId(destinationPageId) {
    // The previous destination becomes our current page before 
    // we switch to the NEW destination.
    cacheCurrentPageId(getCachedDestinationPageId());

    cacheId('destinationPageId', destinationPageId);
}
function getCachedDestinationPageId() {
    return getCachedId('destinationPageId');
}
function cacheSessionId(sessionId) {
    cacheId('sessionId', sessionId);                 
}
function getCachedSessionId() {
    return getCachedId('sessionId');
} 
/****** Username: The email address (username used to log in) ******/
function cacheUsername(username) {
    cacheId('username', username);
}
function getCachedUsername() {
    return getCachedId('username');
} 
/*******************************************************************/
function cacheCompanyName(companyName) {
    cacheId('companyName', companyName);                 
}
function getCachedCompanyName() {
    return getCachedId('companyName');
} 

<!-- The logged-in company, which may differ from the company being managed -->
function cacheLoggedInCompanyName(companyName) { cacheId('loggedInCompanyName', companyName); }
function getCachedLoggedInCompanyName() { return getCachedId('loggedInCompanyName'); } 
function cacheLoggedInCompanyId(companyId) { cacheId('loggedInCompanyId', companyId); }
function getCachedLoggedInCompanyId() { return getCachedId('loggedInCompanyId'); }             

<!-- The company being managed, which may differ from the logged-in company -->
function cacheManagedCompanyId(companyId) { cacheId('managedCompanyId', companyId); }
function getCachedManagedCompanyId() { return getCachedId('managedCompanyId'); }
function clearCachedManagedCompanyId() { clearCachedId('managedCompanyId'); }

function cacheManagedCompanyUsername(companyId) { cacheId('managedCompanyUsername', companyId); }
function getCachedManagedCompanyUsername() { return getCachedId('managedCompanyUsername'); }

function cacheLogoCompanyId(logoCompanyId) {
    cacheId('logoCompanyId', logoCompanyId);
}
function getCachedLogoCompanyId() {
    return getCachedId('logoCompanyId');
}
function clearCachedLogoCompanyId() {
    clearCachedId('logoCompanyId');
}
function cacheManagedCompanyReferences(managedCompanyReferences) {
    cacheId('managedCompanyReferences', managedCompanyReferences ? JSON.stringify(managedCompanyReferences) : '');
}
function getCachedManagedCompanyReferences() {
    var cachedReferences = getCachedId('managedCompanyReferences');
    return cachedReferences ? new Map( Object.entries(JSON.parse(cachedReferences)) ) : new Map();
} 
function clearCachedManagedCompanyReferences() {
    clearCachedId('managedCompanyReferences');
}
function cacheMaxMultisheetsCount(count) {
    cacheId('maxMultisheetsCount', count); 
}
function getCachedMaxMultisheetsCount() {
    return getCachedId('maxMultisheetsCount');
} 
function cacheMaxTeamchartsCount(count) {
    cacheId('maxTeamchartsCount', count);
}
function getCachedTeamchartsCount() {
    return getCachedId('maxTeamchartsCount');
} 
function cacheId(name, value) {
    // Cache sessionId in storage so page reload maintains the session:
    window.sessionStorage.setItem(name, value);                 
}
function getCachedId(name) {        
    return window.sessionStorage.getItem(name);                  
}
function clearCachedId(name) {
    window.sessionStorage.removeItem(name);                
}