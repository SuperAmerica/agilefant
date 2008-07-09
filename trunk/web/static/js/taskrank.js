function test($) {
	$.fn.extend({
		movedown: function() {
			var me = this;
			var row = $(me).parents('tr').filter(':first');
			var next = row.next();
			if(next && next.get(0)) {
				next.after(row);
				row.updateRowCss();
			}
			return this;
		},
		moveup: function() {
			var me = this;
			var row = $(me).parents('tr').filter(':first');
			var prev = row.prev();
			
			if(prev && prev.get(0)) {
				prev.before(row);
				row.updateRowCss();
			}
			return this;
		},
		movebottom: function() {
			var me = this;
			var row = $(me).parents('tr').filter(':first');
			var bottom = row.siblings(':last');
			
			if(bottom && bottom.get(0) && bottom != row) {
				bottom.after(row);
				row.updateRowCss();
			}
			return this;
		},
		movetop: function() {
			var me = this;
			var row = $(me).parents('tr').filter(':first');
			var topnode = row.siblings(':first');
			
			if(topnode && topnode.get(0) && topnode != row) {
				topnode.before(row);
				row.updateRowCss();
			}
			return this;
		},
		
		updateRowCss: function() {
			var me = $(this);
			me.parent().children(':odd').attr('class', 'even');
			me.parent().children(':even').attr('class', 'odd');
			return this;
		} 
	});
};

test(jQuery);