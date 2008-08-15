(function() {
	var themeEditor = function(table, prod,submit) {
		this.container = $(table);
		this.submit = submit;
		this.tbody  = table.find("tbody");
		var me = this;
		var cb = function(data) {
			 var selectTheme = $('<select name="businessThemeIds"></select>');
			 jQuery.each(data,function() {
			 	if(this.active == true) {
			 		$('<option/>').val(this.id).text(this.name + " (" + this.description.substring(0,20) + ")").appendTo(selectTheme);
			 	}
			 });
			me.html = $('<tr></tr>');
			$('<td></td>').append(selectTheme).appendTo(me.html);
			$('<td></td>').append('<input type="text" name="plannedSpendings" value=""/> (e.g. 1h 30min or 10%)').appendTo(me.html);
			$('<td></td>').append('<input type="Reset" value="Cancel"/>').appendTo(me.html);
		};
		if(jsonDataCache.peek("themesByProduct") == null) {
			jQuery.ajax({url: "themesByProduct.action", dataType: "json", data: {productId: prod}, async: false, success: function(data,status) {
				 jsonDataCache.put("themesByProduct",data);
				 cb(data);
			}});
		} else {
			cb(jsonDataCache.peek("themesByProduct"));
		}
	};
	themeEditor.prototype = {
		edit: function(element,binding,theme,relative,fixed,percentage) {
			var row = $(element).parents('tr:eq(0)');
			var newRow = this.html.clone();
		   	var oldData = row.replaceWith(newRow);
		   	row = newRow;
		   	var me = this;
		   	row.find(':reset').click(function() {  		
		   		row.replaceWith(oldData);
		   		if(me.container.find(":reset").length == 0) {
			   		me.submit.hide();
		   		}
		   	});
		   	this.submit.show();
		   	var bindVal = (relative) ? percentage + '%' : fixed;
		   	row.find("select[name='businessThemeIds']").val(theme);
		   	row.find(":text[name='plannedSpendings']").val(bindVal);
		   	row.find("td:eq(0)").append($('<input type="hidden" name="bindingIds" value="'+binding+'"/>'));
		}, 
		delete: function(element,id) {
			var row = $(element).parents('tr:eq(0)');
			jQuery.post("removeThemeFromBacklog.action",{bindingId: id},function() {
				row.remove();
			});
		}, 
		add: function() {
			var html = this.html.clone();
			var visible = this.container.is(":visible");
			if(!visible) this.container.show();
			this.tbody.append(html);
			var me = this;
			html.find(":reset").click(function() {
				html.remove();
				if(!visible) me.container.hide(); 
				if(me.container.find(":reset").length == 0) {
					me.submit.hide();
				}
				return false;
			});
			me.submit.show();
	   		return false;
		}
	};
	jQuery.fn.extend({
		themeBinding: function(add,product,submit) {
			var obj = jQuery.data(this.get(0),"themeBinding");
			if(obj == null) {
				var handler = new themeEditor(this,product,$(submit));
				jQuery.data(this.get(0),"themeBinding",handler);
				$(add).click(function() {
					handler.add();
					return false;
   				});
   				return this;
			} else {
				return obj;
			}
		},
		attachTheme: function() {
		
		},
		todoEdit: function() {
		
		}
	});
})();
