
/*
 * Add the validator for AFTime.
 * param[0] should be false if empty values are accepted, true otherwise. 
 */
jQuery.validator.addMethod("aftime",function(value, element, param) {
    if (param[0] == false && trim(value) == "") {
        return true;
    }
    var hourOnly = new RegExp("^[ ]*[0-9]+h?[ ]*$"); //10h
    var minuteOnly = new RegExp("^[ ]*[0-9]+min[ ]*$"); //10min
    var hourAndMinute = new RegExp("^[ ]*[0-9]+h[ ]+[0-9]+min[ ]*$"); //1h 10min
    var shortFormat = new RegExp("^[0-9]+[.,][0-9]+$"); //1.5 or 1,5
    return (hourOnly.test(value) || minuteOnly.test(value) || hourAndMinute.test(value) || shortFormat.test(value));
}, "Invalid format");

/* Add the validator for time format */
jQuery.validator.addMethod("time",function(value, element, param) {
    var standardDateFormat = new RegExp("^[ ]*[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])[ ]+([0-1][0-9]|2[0-3]):[0-5][0-9][ ]*$");
    return (standardDateFormat.test(value) ); 
}, "Invalid date format");

/* param[0] should be the id of the other field */
jQuery.validator.addMethod("before", function(value, element, param) {
    var elem = $(element);
    var date1 = Date.fromString(value);
    var date2 = Date.fromString(elem.parents('form:eq(0)').find('[@name=' + param[0] + ']').val());
    if (date1 == false || date2 == false) {
        return true;
    }
    return (date1.getTime() <= date2.getTime());
}, "Date 1 must be before date 2");

/**
 * Unique field validator.
 * param[0] should be the field name
 * param[1] should be the name of the cached json object
 */
jQuery.validator.addMethod("unique", function(value, element, param) {
    var elem = $(element);
    var valid = true;
    var e = jsonDataCache;
    var f = param[1];
    var g;
    var list = jsonDataCache.get(param[1]);
    if (param[0] == null || param[0] == "") {
        return true;
    }
    $.each(list, function() {
        if (this[param[0]] == elem.val()) {
            valid = false;
        }
    });
    return valid;
});



var agilefantValidationRules = {
    theme: {
        rules: {
	        "businessTheme.name": {
	           required: true,
	           minlength: 1
	        },
	        "productId": {
	           required: true
            }
	    },
	    messages: {
	        "businessTheme.name": {
	            required: "Please enter a name",
	            minlength: "Please enter a name"
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
                required: "Please specify a end date",
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
	           required: true
	       }
	   },
	   messages: {
	       "product.name": {
	           required: "Please enter a name"
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
               required: "Please specify a end date",
               time: "Invalid date format"
           },
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
	backlogItem: {
	   rules: {
	       "backlogItem.name": {
	           required: true
	       },
	       "backlogId": {
	           required: true
	       },
	       "backlogItem.originalEstimate": {
	           aftime: [ false ]
	       }
	   },
	   messages: {
	       "backlogItem.name": {
               required: "Please enter a name"
           },
           "backlogId": {
               required: "Please select a backlog"
           },
           "backlogItem.originalEstimate": {
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
    user: {
       rules: {
           "user.fullName": {
               required: true
           },
           "user.loginName": {
               required: true,
               unique: [ "loginName", "allUsers" ]
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
               required: "Please enter a name"
           },
           "user.email": {
               required: "Please enter an email",
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
    team: {
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
    projectType: {
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
    }
};
agilefantValidationRules.businessTheme = agilefantValidationRules.theme;
agilefantValidationRules.bli = agilefantValidationRules.backlogItem;

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
