
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


var agilefantValidationRules = {
	backlogItem: {
        rules: {
	    },
	    messages: { 
	    }
	},
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
            }
        },
        errorPlacement: function(error, element) {
            if (element.hasClass('datePickerField')) {
                element.next().after(error);
            }
            else if ( element.parent().find('span.errorMessage').length > 0) {
                error.appendTo( element.parent().find('span.errorMessage') );
            }
            else {
                element.after( error );
            }
        }
	}
};
agilefantValidationRules.businessTheme = agilefantValidationRules.theme;
