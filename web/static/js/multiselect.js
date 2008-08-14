function test($) {
	$.fn.extend({	
		userselect: function(users, formfix) {
			var me = this;
			$.each(users, function(key, user) {
			    if (formfix) {
				    $(me).append(
					   $('<li></li>').html(
						  '<input type="checkbox" name="userIds[' + user.id + ']" class="user_' + user.id + '" />' + user.name
					   )
				    );
				}
				else {
				    $(me).append(
                       $('<li></li>').html(
                          '<input type="checkbox" name="userIds" class="user_' + user.id + '" value="' + user.id + '"/>' + user.name
                       )
                    );
				}
			});
			return this;
		},
		groupselect: function(groups, extra) {
			var me = this;
			$.each(groups, function(key, group) {
				$(me).append(
					$('<li>' + group.name + '</li>').click(function() {
						var root = (extra!=undefined ? $(extra) : $('body'));
						$.each(group.users, function(index, user_id) {
							$('input.user_' + user_id, root).each(function() {
								if( !$(this).attr('checked') )
									$(this).attr('checked', true).trigger('change')
							})
						})
					})
				);
			});
			return this;
		},
		selectusers: function(users) {
			var me = this;
			$(':checkbox[@name^="userIds"]', this).attr('checked', false);
			$.each(users, function(index, user_id) {
				$('input.user_' + user_id, me).attr('checked', true);
			});
			return this;
		},
		multiuserselect: function(settings) {
			var me = $(this);
			if( settings.users != undefined ) {
				if( settings.users[0].prototype = Array ) {
					$.each(settings.users, function(index, users) {
						$(".users_" + index, me).empty().userselect(users, settings.formFix); 
					});
				} else {
					$(".users", me).empty().userselect(settings.users, settings.formFix);
				}
			}
			
			// Root for the list of check boxes which are selected by clicking the links
			var root = (settings.root != undefined ? $(settings.root) : $('body'));
			
			if( settings.groups != undefined ) {
				$(".groups", me).empty().groupselect(settings.groups,root);
			}
			$("li:nth-child(even)", this).addClass("even");
			$("li:nth-child(odd)", this).addClass("odd");
			return this;
		},
		toggle_disabled: function(state) {
			$(':checkbox[@name^="userIds"]', this).attr('disabled', state);
		}
	});
};
test(jQuery)