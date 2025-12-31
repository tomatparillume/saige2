    // Generates options for the main Team Insights page, and also calls
    // cacheAssessmentData(assessmentId, assessmentJSON) so the My Profile
    // page can use this cached assessmentJSON to create its own options.
    function generateAssessmentFiltersHTML(assessmentsJSONStr) {
        var assessmentsParentJSON = $.parseJSON(assessmentsJSONStr).message;
        var assessmentsJSON = assessmentsParentJSON.assessments; 

//                // e.g. Let's Go is a subcategory of the CliftonStrengths:Influencing category
//                var assessmentSubcategories = assessmentsParentJSON.assessmentSubcategories;   
//                var subcategoryMap = new Map();
//                $.each(assessmentSubcategories, function(i,subcategory) {             
//                    subcategoryMap.set(subcategory.id, subcategory);
//                });

        var assessmentFiltersHTML = "<div id='assessmentsSelection' class='w3-hide'>";    
        assessmentFiltersHTML += "<div class='divtable' style='margin-left:75px;'>";
                                   
        // Assessment buttons: CliftonStrengths, Myers-Briggs, Enneagram
        var firstRowButtons = '';     
//        var secondRowCategories = ''; 

        $.each(assessmentsJSON, function(index,assessmentData) {
            // References the accordion button AND the class that will
            // hold selection-status text ("Some selected", "none selected"...):
            var accordionBtnId = 'accordionBtn-' + index;                       

            firstRowButtons += "<div class='divcell align-center'>" +
                                    "<span class='link-unemphasized " +
                                          // toggleAssessmentAccordion uses this class to change this text's style:
                                          accordionBtnId+"-assessmentCategoryDisplay' " + 
                                          "style='text-shadow:1px 1px 1px rgb(180,180,180);'>" + 
                                          assessmentData.label + // e.g. CliftonStrengths
                                    "</span><br/>" +
                                    // Clicking a first-row button toggles accordion content for that assessment:
                                    "<span onclick='toggleAssessmentAccordion(\""+assessmentData.id+"\", \""+ accordionBtnId+"\")' " +
                                            "id='"+accordionBtnId+"', class='btn-purple-small assessments-btn' style='margin-top:-1px;'>" +
                                             "<span class='field-tip assessment-field-text'>" +
                                                  assessmentData.summary +                                                   
                                                  "<span class='tip-content'>"+assessmentData.description+"</span>" +
                                             "</span><p class='button-subtitle selection-status "+accordionBtnId+"'></p>" + 
                                     "</span>" +                                             
                                "</div>";

//                    secondRowCategories += "<div class='divcell link-unemphasized align-left " + 
//                                            // toggleAssessmentAccordion uses this class to change this text's style:
//                                            accordionBtnId+"-assessmentCategoryDisplay' " +
//                                           "style='padding-top:10px;padding-left:20px;'>";  
//                    // e.g. Social Stance
//                    var assessmentCategoryType = assessmentData.assessmentCategoryType;
//                    secondRowCategories += "<div class='fontsize-90'>" + assessmentCategoryType.name.toUpperCase() + "</div>";
//
//                    var categoriesArray = assessmentData.categories;  
//                    $.each(categoriesArray, function(index2,categoryData) {      
//                        secondRowCategories += "<div class='fontsize-90'>" + 
//                                                categoryData.assessmentCategory.name + 
//                                               "</div>";            
//                    });
//                    secondRowCategories += "</div>";  
        });

        assessmentFiltersHTML += "<div style='display:table-row;'>" + firstRowButtons + "</div>" + 
                                    //"<div style='display: table-row'>" + secondRowCategories + "</div>" + 
                                "</div>" // END class='divtable'

        // Assessment accordion content:
        var uniqueId = 0;
        var accordionContent = '';
        
        // For some assessmentTypes (e.g. MyersBriggs), we do NOT allow direct selection of fine-grained
        // assessment results (e.g INTJ) to filter insight queries, and so we hide these checkboxes.
        // 
        // Clicking a parent/group checkbox - e.g. clicking the "Ten Steps Ahead" Myers-Briggs checkbox -
        // dynamically selects the hidden result checkboxes in that category: ENTJ, INTJ, etc.
        // 
        // fineGrainedDisplayClass determines whether such fine-grained checkboxes are hidden.
        var fineGrainedDisplayClass = '';
        
        $.each(assessmentsJSON, function(index,assessmentData) {
            var assessmentType = assessmentData.assessmentType; // e.g. Enneagram
            fineGrainedDisplayClass = _assessmentTypesFineGrainedDisplayClass.get(assessmentType.toLowerCase());

            // e.g. Enneagram accordion content
            accordionContent += "<div id='" + assessmentData.id + "' class='assessment-accordion-content w3-container w3-hide'>";

            var assessmentClass = (++index) + '_class';

            // START row for the assessment (e.g. CliftonStrengths)
            accordionContent += "<table style='width:100%;'><tr>"; 
 
//            var assessmentCategoryType = assessmentData.assessmentCategoryType;
            // e.g. Social Stance
            var categoriesArray = assessmentData.categories; 
            accordionContent += 
                    // e.g. "Enneagram Social Stance":
//                            "<td class='blue fontsize-110 align-center' colspan="+categoriesArray.length+">" +
//                                "<span class='fontsize-90'>" + assessmentData.label + "</span> " + 
//                                "<em>" + assessmentCategoryType.name + "</em>" +
//                            "</td></tr>" +
                    "<tr><td><input type='checkbox' class='assessment-checkbox' " +
                        "name='"+index+"' id='"+index+"' value='"+index+"' " +
                        "onclick=unCheckGroup('"+index+"','"+assessmentClass+"') " +
                        "onchange='submitInsightsQuery(false);'>" +
                        "&nbsp;<span class='fontsize-90' style='color:dimgray'>SELECT ALL</span>" +
                    "</td</tr>";

            accordionContent +=  "<tr><ul>"; 

            $.each(categoriesArray, function(index2,categoryData) {
                // START cell for the category
                accordionContent += "<td>"; 

                // e.g. Aligning, Assertive, Withdrawing
                var assessmentCategory = categoryData.assessmentCategory;
                //var desc = assessmentCategory.description.replace(/'/g, "\\'");

                var categoryClassFlag = (index) + '_' + (++index2);
                var assessmentCategoryClass = categoryClassFlag + '_class';    

                accordionContent += "<li style='list-style-type:none;'>" +
                                "<input type='checkbox' name='"+categoryClassFlag+"' id='"+categoryClassFlag+"' " +
                                   "class='"+assessmentClass+" assessment-checkbox' " +
                                   "value='"+assessmentCategory.id+"' " +
                                   "onclick=unCheckGroup('"+categoryClassFlag+"','"+assessmentCategoryClass+"') " +
                                   "onchange='submitInsightsQuery(false);'>" +
                            "<span class='purple fontsize-110 field-tip field-text'>" +
                                    "&nbsp;<strong>" + assessmentCategory.name + "</strong>" +
                                    "<span class='tip-content'>"+assessmentCategory.description+"</span>" +
                            "</span></li>";

                accordionContent +=  "<ul>";

                var assessmentResultsArray = categoryData.assessmentResults;
                $.each(assessmentResultsArray, function(index3,assessmentData) {
                    // We cache this assessmentData so the My Profile page 
                    // can use it to build its own options.
                    cacheAssessmentData(assessmentData.id, JSON.stringify(assessmentData));

                    var desc = assessmentData.description;
                    if(!desc) {
                        var superpower = assessmentData.termIdToValue["TM-superpower"];
                        var kryptonite = assessmentData.termIdToValue["TM-kryptonite"];                   
                        if(superpower && kryptonite) {
                            desc = '<strong>Superpowers</strong>: ' + superpower + '<br/>' +
                                   '<strong>Kryptonite</strong>: ' + kryptonite;                                 
                        }
                    }
                    desc = '<em style="font-size:120%">' + assessmentData.label + '</em><br/>' + desc;

//                            // Display a subcategory header (e.g. "Yes, And",  "Tell Me More") if available
//                            var categoryIds = assessmentData.assessmentCategoryIds;
//                            $.each(categoryIds, function(index4,categoryId) {                               
//                                if(subcategoryMap.get(categoryId)) {
//                                    var subcategory = subcategoryMap.get(categoryId);
//                                    
//                                    if(subcategoryId) {
//                                        if(subcategoryId === subcategory.id)
//                                            return false; // We've already displayed this subcategory header
//
//                                        // Separate each subcategory group:
//                                        accordionContent += "<hr class='"+fineGrainedDisplayClass+"' style='margin-left:0;max-width:50%'/>"; 
//                                    }
//                                    subcategoryId = subcategory.id;
//                                    
//                                    accordionContent += "<li class='"+fineGrainedDisplayClass+"' >" +
//                                                    "<span class='field-tip field-text'>" +
//                                                    "<em>" + subcategory.name + "</em>" +
//                                                    "<span class='tip-content'>" +
//                                                    subcategory.description + 
//                                                    "</span></span></li>";  
//                                }
//                            });                     

                    accordionContent += "<li class='"+fineGrainedDisplayClass+"' >" +
                                    "<input type='checkbox' name='assessmentresultid' id='unique_"+(uniqueId++)+"' " +
                                      "class='"+assessmentClass+" "+assessmentCategoryClass+" assessment-checkbox' " +
                                      "onchange='submitInsightsQuery(false);' " +
                                      "value='"+assessmentData.id+"'>";

                    accordionContent += "<span class='field-tip field-text'>" +
                               "&nbsp;" + assessmentData.label +
                               "<span class='tip-content'>" +
                               desc + 
                               "</span></span></li>";
                })
                accordionContent += "</ul>"; // END assessmentResultsArray
                accordionContent += "</td>"; // END cell for the category (e.g. LeadershipStyle:Influencing)
            })

            accordionContent += "</ul>"; // END categoriesArray

            accordionContent += "</tr></table>";// END row for the assessment (e.g. CliftonStrengths)
            accordionContent += "</div>"; // END this assessment accordion content
        });                 
        assessmentFiltersHTML += accordionContent;       

         // END the parent <div>                                     
        assessmentFiltersHTML += "</div>"; 

        return assessmentFiltersHTML;
    }      