
/*
 * Add the validator for AFTime.
 * param[0] should be false if empty values are accepted, true otherwise.
 * param[1] allow negatives 
 */
jQuery.validator.addMethod("aftime",function(value, element, param) {
    if (param == null) {
        param = [ true ];
    }
    if (param[0] == false && jQuery.trim(value) == "") {
        return true;
    }
    if (param[1] == null) {
        param[1] = false;
    }
    var hourOnly = new RegExp("^[ ]*[0-9]+h?[ ]*$"); //10h
    var minuteOnly = new RegExp("^[ ]*[0-9]+min[ ]*$"); //10min
    var hourAndMinute = new RegExp("^[ ]*[0-9]+h[ ]+[0-9]+min[ ]*$"); //1h 10min
    var shortFormat = new RegExp("^[0-9]+[.,][0-9]+$"); //1.5 or 1,5
    if (param[1]) {
	    hourOnly = new RegExp("^[ ]*-?[0-9]+h?[ ]*$"); //10h
	    minuteOnly = new RegExp("^[ ]*-?[0-9]+min[ ]*$"); //10min
	    hourAndMinute = new RegExp("^[ ]*-?[0-9]+h[ ]+[0-9]+min[ ]*$"); //1h 10min
        shortFormat = new RegExp("^-?[0-9]+[.,][0-9]+$"); //1.5 or 1,5
    }
    return (hourOnly.test(value) || minuteOnly.test(value) || hourAndMinute.test(value) || shortFormat.test(value));
}, "Invalid format");

/* Add the validator for time format */
jQuery.validator.addMethod("time",function(value, element, param) {
    var standardDateFormat = new RegExp("^[ ]*[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])[ ]+([0-1][0-9]|2[0-3]):[0-5][0-9][ ]*$");
    var secondDateFormat = new RegExp("^[ ]*[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])[ ]*$");
    return (standardDateFormat.test(value) || secondDateFormat.test(value)); 
}, "Invalid date format");

/* param[0] should be the id of the other field */
jQuery.validator.addMethod("before", function(value, element, param) {
    var elem = $(element);
    var date1 = Date.fromString(value);
    var date2 = Date.fromString(elem.parents('form:eq(0)').find('[name=' + param[0] + ']').val());
    if (date1 == false || date2 == false) {
        return true;
    }
    return (date1.getTime() <= date2.getTime());
}, "Date 1 must be before date 2");

/**
 * param should be the selector of the other field.
 */
jQuery.validator.addMethod("equalField",function(value, element, param) {
	var elem1 = $(element);
    var elem2 = elem1.parents('form:eq(0)').find(param);
    if (elem2 == null || elem1.val() == elem2.val()) {
    	return true;
    }
    return false;
}, "Field values don't match");

/**
 * Unique field validator.
 * param[0] should be the field name
 * param[1] should be the name of the cached json object
 * param[2] should be json element of additional parameters
 */
jQuery.validator.addMethod("unique", function(value, element, param) {
    var options = {
        exclude: false,
        excludeField: ""
    }
    jQuery.extend(options, param[2]);
    var elem = $(element);
    var valid = true;
    var list;
    if (typeof(param[1]) == 'function') {
        list = param[1](this);
    }
    else {
        list = jsonDataCache.get(param[1]);
    }
    var excludeThis = null;
    if (param[0] == null || param[0] == "") {
        return true;
    }
    if (options.exclude) {
        excludeThis = $(this.currentForm).find(options.excludeField).val();
    }
    $.each(list, function() {
        if ((this.id != excludeThis) &&
            this[param[0]] == elem.val()) {
            valid = false;
        }
    });
    return valid;
});


var agilefantValidationRules = {
    empty: { },
    theme: {
        rules: {
	        "businessTheme.name": {
	           required: true,
	           minlength: 1,
	           unique: [ "name", function(me) {
	               var prodId = $(me.currentForm).find('[name=productId]').val();
	               return jsonDataCache.get("themesByProduct", {data: {productId: prodId}}, prodId);
	           }, { exclude: true, excludeField: 'input[name=businessThemeId]'}]
	        },
	        "productId": {
	           required: true
            }
	    },
	    messages: {
	        "businessTheme.name": {
	            required: "Please enter a name",
	            minlength: "Please enter a name",
	            unique: "Theme name already used in this product"
	        },
	        "productId": {
	           required: "Please select a product"
	        }
	    }
	},
	project: {
        rules: {
            "project.name": {
                required: true,
                minlength: 1
            },
            "productId": {
                required: true
            },
            "startDate": {
                required: true,
                time: true,
                before: [ "endDate" ]
            },
            "endDate": {
                required: true,
                time: true
            },
            "project.defaultOverhead": {
                aftime: [ false ]
            },
            "project.backlogSize": {
                digits: true
            }
        },
        messages: {
            "project.name": {
                required: "Please enter a name",
                minlength: "Please enter a name"
            },
            "productId": {
                required: "Please select a product"
            },
            "startDate": {
                required: "Please specify a start date",
                time: "Invalid date format",
                before: "Start date must be before end date"
            },
            "endDate": {
                required: "Please specify an end date",
                time: "Invalid date format"
            },
            "project.defaultOverhead": {
                aftime: "Invalid format"
            },
            "project.backlogSize": {
                digits: "Please enter only numbers"
            }
        }
	},
	product: {
	   rules: {
	       "product.name": {
	           required: true,
	           unique: [ "name", "allProducts", { exclude: true, excludeField: 'input[name=productId]' } ]
	       }
	   },
	   messages: {
	       "product.name": {
	           required: "Please enter a name",
	           unique: "Product name already in use"
	       }
	   }
	},
	iteration: {
	   rules: {
	       "iteration.name": {
	           required: true
	       },
	       "projectId": {
	           required: true
	       },
	       "startDate": {
                required: true,
                time: true,
                before: [ "endDate" ]
            },
            "endDate": {
                required: true,
                time: true
            },
            "iteration.backlogSize": {
                digits: true
            }
	   },
	   messages: {
	       "iteration.name": {
               required: "Please enter a name"
           },
           "projectId": {
               required: "Please select a project"
           },
           "startDate": {
               required: "Please specify a start date",
               time: "Invalid date format",
               before: "Start date must be before end date"
           },
           "endDate": {
               required: "Please specify an end date",
               time: "Invalid date format"
           },
           "iteration.backlogSize": {
               digits: "Please enter only numbers"
           }
	   }
	},
	iterationGoal: {
	   rules: {
	       "iterationGoal.name": {
	           required: true
	       },
	       "iterationId": {
	           required: true
	       }
	   },
	   messages: {
	       "iterationGoal.name": {
               required: "Please enter a name"
           },
           "iterationId": {
               required: "Please select an iteration"
           }
	   }
	},
	story: {
	   rules: {
	       "story.name": {
	           required: true
	       },
	       "backlogId": {
	           required: true
	       },
	       "story.originalEstimate": {
	           aftime: [ false ]
	       }
	   },
	   messages: {
	       "story.name": {
               required: "Please enter a name"
           },
           "backlogId": {
               required: "Please select a backlog"
           },
           "story.originalEstimate": {
               aftime: "Invalid format"
           }
	   }
	},
    hourEntry: {
       rules: {
           "hourEntry.timeSpent": {
               required: true,
               aftime: [ true ]
           },
           "date": {
               required: true,
               time: true
           },
           "userIds": {
               required: true, 
               minlength: 1
           }
       },
       messages: {
           "hourEntry.timeSpent": {
               required: "Please enter the time spent",
               aftime: "Invalid format"
           },
           "date": {
               required: "Please enter a date",
               time: "Invalid format"
           },
           "userIds": {
                required: "Select at least 1 user",
                minlength: "Select at least 1 user"
           }
       }
    },
    newUser: {
       rules: {
           "user.fullName": {
               required: true
           },
           "user.loginName": {
               required: true,
               unique: [ "loginName", "allUsers", { exclude: true, excludeField: 'input[name=userId]' } ]
           },
           "user.initials": {
               required: true
           },
           "user.email": {
               required: true,
               email: true
           },
           "user.weekHours": {
               aftime: [ true ]
           },
           "password1": {
               required: true
           },
           "password2": {
               equalTo: '#password1'
           }
       },
       messages: {
           "user.fullName": {
               required: "Please enter a name"
           },
           "user.loginName": {
               unique: "Login name already in use",
               required: "Please enter a login name"
           },
           "user.initials": {
               required: "Please enter the initials"
           },
           "user.email": {
               required: "Please enter an email address",
               email: "Invalid email address"
           },
           "user.weekHours": {
               aftime: "Invalid format"
           },
           "password1": {
               required: "Please enter a password"
           },
           "password2": {
               equalTo: "Passwords don't match"
           }
       }
    },
    user: {
       rules: {
           "user.fullName": {
               required: true
           },
           "user.loginName": {
               required: true,
               unique: [ "loginName", "allUsers", { exclude: true, excludeField: 'input[name=userId]' } ]
           },
           "user.initials": {
               required: true
           },
           "user.email": {
               required: true,
               email: true
           },
           "user.weekHours": {
               aftime: [ true ]
           },
           "password1": {
               required: false
           },
           "password2": {
               equalField: 'input[name=password1]'
           }
       },
       messages: {
           "user.fullName": {
               required: "Please enter a name"
           },
           "user.loginName": {
               unique: "Login name already in use",
               required: "Please enter a login name"
           },
           "user.initials": {
               required: "Please enter the initials"
           },
           "user.email": {
               required: "Please enter an email address",
               email: "Invalid email address"
           },
           "user.weekHours": {
               aftime: "Invalid format"
           },
           "password2": {
               equalTo: "Passwords don't match"
           }
       }
    },
    newTeam: {
        rules: {
            "team.name": {
                required: true,
                unique: [ "name", "allTeams" ]
            }
        },
        messages: {
            "team.name": {
                required: "Please enter a name",
                unique: "Team name already in use"
            }
        }
    },
    team: {
        rules: {
            "team.name": {
                required: true,
                unique: [ "name", "allTeams", { exclude: true, excludeField: 'input[name=teamId]' } ]
            }
        },
        messages: {
            "team.name": {
                required: "Please enter a name",
                unique: "Team name already in use"
            }
        }
    },
    newProjectType: {
        rules: {
            "projectType.name": {
                required: true,
                unique: [ "name", "allProjectTypes" ]
            }
        },
        messages: {
            "projectType.name": {
                required: "Please enter a name",
                unique: "Project type name already in use"
            }
        }
    },
    projectType: {
        rules: {
            "projectType.name": {
                required: true,
                unique: [ "name", "allProjectTypes", { exclude: true, excludeField: 'input[name=projectTypeId]' } ]
            }
        },
        messages: {
            "projectType.name": {
                required: "Please enter a name",
                unique: "Project type name already in use"
            }
        }
    },
    bliProgress: {
        rules: {
            "effortLeft": {
                aftime: [ false ]
            },
            "spentEffort": {
                aftime: [ false ]
            }
        },
        messages: {
            "effortLeft": {
                aftime: "Invalid format"
            },
            "spentEffort": {
                aftime: "Invalid format"
            }
        }
    }
};
agilefantValidationRules.businessTheme = agilefantValidationRules.theme;

/*
 * Add the error placement rules to each ruleset.
 */
jQuery.each(agilefantValidationRules, function() {
    this.errorPlacement = function(error, element) {
        var errorLabel;
        if (element.hasClass('datePickerField')) {
            element.next().after(error);
        }
        else if ( (errorLabel = element.parents("form:eq(0)").find('label.errorMessage[for="' + element.attr('name') + '"]')).length > 0) {
            error.appendTo( errorLabel );
            errorLabel.show();
        }
        else if ( element.parent().find('span.errorMessage').length > 0) {
            error.appendTo( element.parent().find('span.errorMessage') );
        }
        else {
            element.after( error );
        }
    };
});


var validationRulesByHTMLClass = {
    'validateNewStory': agilefantValidationRules.story,
    'validateStory': agilefantValidationRules.story,
    'validateNewHourEntry': agilefantValidationRules.hourEntry,
    'validateNewIteration': agilefantValidationRules.iteration,
    'validateIteration': agilefantValidationRules.iteration,
    'validateNewIterationGoal': agilefantValidationRules.iterationGoal,
    'validateIterationGoal': agilefantValidationRules.iterationGoal,
    'validateNewProduct': agilefantValidationRules.product,
    'validateNewProject': agilefantValidationRules.project,
    'validateProject': agilefantValidationRules.project,
    'validateNewProjectType': agilefantValidationRules.newProjectType,
    'validateProjectType': agilefantValidationRules.projectType,
    'validateNewTeam': agilefantValidationRules.newTeam,
    'validateTeam': agilefantValidationRules.team,
    'validateNewTheme': agilefantValidationRules.theme,
    'validateTheme': agilefantValidationRules.theme,
    'validateNewUser': agilefantValidationRules.newUser,
    'validateUser': agilefantValidationRules.user,
    'validateBLIProgressTab': agilefantValidationRules.bliProgress,
    'validateExistingProduct': agilefantValidationRules.product,
    'validateEmpty': agilefantValidationRules.empty
};