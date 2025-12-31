    function generateMyProfileAssessmentOptionsHTML(assessmentsJSONStr) {
        var assessmentsParentJSON = $.parseJSON(assessmentsJSONStr).message;

        var typeLabelToResults = assessmentsParentJSON.typeLabelToResults;  

        var assessmentMyProfileSelectionDisplay = '';
        var supMyProfileSelectionDisplay = '';
        var krypMyProfileSelectionDisplay = '';

        $.each(typeLabelToResults, function(assessmentLabel,assessmentResults) {
            var assessmentTypeId = assessmentResults[0].assessmentId;
            var isCSAssessment = (assessmentTypeId == 'CS-assessment');

            var accordionLinkId = "link-accordion-" + assessmentTypeId;
            var openCloseSpanId = "open-close-" + assessmentTypeId;
            var accordionContentId = "myprofile-assessmentoptions-"+assessmentTypeId;

            supMyProfileSelectionDisplay += "<h5 style='padding-left:20px;'>"+assessmentLabel+"</h5>";
            krypMyProfileSelectionDisplay += "<h5 style='padding-left:20px;'>"+assessmentLabel+"</h5>";

            assessmentMyProfileSelectionDisplay += "<span>"; 

            ////// The Assessment section header - togglable to display the assessment-score options:
            assessmentMyProfileSelectionDisplay += 
                    "<span class='link-togglable'" + 
                          "onclick='toggleAccordionLink(\""+accordionContentId+"\"," +
                                                      " \""+accordionLinkId+"\"," +
                                                      " \""+openCloseSpanId+"\" )'" +
                    ">" +
                        "<span id='"+accordionLinkId+"' style='color:grey;'>" +
                           "<h5><i id='"+openCloseSpanId+"' " +
                                   "class='fa fa-caret-up openclose-icon' aria-hidden='true'>" + 
                               "</i>&nbsp;" +
                           assessmentLabel;         

            assessmentMyProfileSelectionDisplay += "</h5></span></span>";

            /************** The accordion content **************/

            ////// Assessment-result options to select
            assessmentMyProfileSelectionDisplay += 
                    "<span class='grid-centered'>" +
                        "<div class='w3-container w3-hide' id='"+accordionContentId+"'>" +
                        (isCSAssessment ? "Select your top five strengths:":"") +
                        "<ul class='ul-columns'>";

            $.each(assessmentResults, function(index2,assessmentResult) {                                               
                assessmentMyProfileSelectionDisplay += "<li>";

                if(isCSAssessment) {
                    assessmentMyProfileSelectionDisplay += 
                        "<select class='csnumberedinput_myprofile' " +
                                "id='"+assessmentResult.id+"' name='"+assessmentResult.id+"' " +
                                "onchange=toggleMyProfileNumberDropdowns(this);>" +
                            "<option value=''> -- </option>" +
                            "<option value='1'>1st</option>" +
                            "<option value='2'>2nd</option>" +
                            "<option value='3'>3rd</option>" +
                            "<option value='4'>4th</option>" +
                            "<option value='5'>5th</option>" +
                        "</select>";
                } else {
                    assessmentMyProfileSelectionDisplay += 
                        "<input id='"+assessmentResult.id+"' name='"+assessmentResult.id+"' " +
                                "onclick='toggleMyProfileAssessmentCheckboxes(\""+assessmentResult.id+"\", \""+assessmentTypeId+"\");' " +
                                "class='myprofile_assessmentcheckbox_"+assessmentTypeId+"' type='checkbox'>";                                      
                }
                assessmentMyProfileSelectionDisplay += "&nbsp;<label id='label_"+assessmentResult.id+"'>" + 
                                                      assessmentResult.label + "</label>";
                assessmentMyProfileSelectionDisplay += "</li>"; 

                var cachedResultDataJSON = getCachedAssessmentData(assessmentResult.id)
                var resultData = $.parseJSON(cachedResultDataJSON);

                var id = resultData.id;
                var label = resultData.label;
                var superpower = resultData.termIdToValue["TM-superpower"];
                var kryptonite = resultData.termIdToValue["TM-kryptonite"];   

                supMyProfileSelectionDisplay += "<div id='sup-div-"+id+"' " +
                                                 "style='padding-left:50px;' " +
                                                 "class='parentdiv w3-container w3-hide'>" +
                                                    "<span style='color:grey'>" + label + "</span>" +
                                                    "<br/><input class='supcheckbox' type=checkbox id='sup-"+id+"' name='sup-"+id+"'>&nbsp;" +
                                                    superpower +
                                                "</div>";
                krypMyProfileSelectionDisplay += "<div id='kryp-div-"+id+"' " +
                                                 "style='padding-left:50px;' " +
                                                 "class='parentdiv w3-container w3-hide'>" +
                                                    "<span style='color:grey'>" + label + "</span>" +
                                                    "<br/><input class='krypcheckbox' type=checkbox id='kryp-"+id+"' name='kryp-"+id+"'>&nbsp;" +
                                                    kryptonite +
                                                "</div>";
            }); 
            assessmentMyProfileSelectionDisplay += "</ul>";
            ////// END assessment-result options to select

            assessmentMyProfileSelectionDisplay += "</div></span>";
            /************** END accordion content **************/
        });

        return Array.of(assessmentMyProfileSelectionDisplay,
                        supMyProfileSelectionDisplay,
                        krypMyProfileSelectionDisplay);
    }            