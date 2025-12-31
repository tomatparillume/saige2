function showInsightsTable(insightsJSON) {   
    $("#insightstable").html('<pre>' + JSON.stringify(insightsJSON, null, 2) + '</pre>');
    $("#insightstable").show();                  
}
function hideInsightsTable() {
    $("#screenshotTooltip").hide();
    $("#insightstable").hide();                 
}
// Called by the "by team members" and "by assessments" links on the Team
// Insights page to conditionally display the insights table if appropriate
function toggleInsightsDisplay(filterByTeamMembers) {                

    if(filterByTeamMembers) {
        showAccordionLink('teamMembersInsightsSelection', 'link_team_members', 'openclose_team_members');
        hideAccordionLink('assessmentsSelection', 'link_assessments', 'openclose_assessments');   
    } else {
        showAccordionLink('assessmentsSelection', 'link_assessments', 'openclose_assessments');
        hideAccordionLink('teamMembersInsightsSelection', 'link_team_members', 'openclose_team_members');                      
    }

    var activeFormId = getInsightsFormId(filterByTeamMembers);
    var inActiveFormId = getInsightsFormId(!filterByTeamMembers);

    var activeForm = $("#"+activeFormId);
    activeForm.find(":hidden").prop("disabled", false);
    activeForm.find(":input").prop("disabled", false);

    var inactiveForm = $("#"+inActiveFormId);
    inactiveForm.find(":hidden").prop("disabled", true);
    inactiveForm.find(":input").prop("disabled", true);

    submitInsightsQuery(filterByTeamMembers);
}
function clearInsightsTable(maintainColumnLinks) {
    if(_insightsTable)
        _insightsTable.clearData();
    if(_csvResultsTable)
        _csvResultsTable.clearData();

    hideInsightsTable();

    if(!maintainColumnLinks) {
        for(var[hiddenColumnId,color] of _columnColorMap) { 
            resetInsightsTableColumn(hiddenColumnId);
        };  
    }
}
function resetInsightsTableColumn(hiddenColumnId) {  
    // The ids and values for hidden column fields are the same string;
    // set the field value to the id, to support form submission:
    $("#"+hiddenColumnId).val(hiddenColumnId);

    // Reset the toggleable column links to be active
    var activeColor = _columnColorMap.get(hiddenColumnId);
    $("#"+hiddenColumnId+"_columnLink").css('color',activeColor);                
}   