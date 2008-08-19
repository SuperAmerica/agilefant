(function($) {
   var TableEdit = function(opt)
   {
      var options = {
        container: null,
        submit: '#editableSubmitButton',
        deleteaction: false,
        useId: false,
        rowClass: 'editableTableRow',
        cellClass: 'editableTableCell',
        submitParam: 'objectId',
        deleteCb: null,
        fields: {}
      };
      jQuery.extend(options,opt);
      this.container = $(options.container); //table element
      this.tbody = this.container.find('tbody');
      this.submit = $(options.submit);     //submit button
      /* field options in form
       *  {field1_name: {cell: number, type: 'text/select/hidden/date', data: preset_data}, ...}
       * preset_data can be either a function, object (selects) or string
       * function will get table cell value and options object as argument
       */
      this.options  = options;
      this.uniqueId = options.uniqueId;
      this.deleteaction = options.deleteaction;
      this.fields = options.fields;
      this.counter = -1;
   };
   /**
    * Render editable table row.
    * If old row is null a new row will be added at the end of the table,
    * if old row is an existing table row, an editable row will be rendered 
    * with that data.
    *
    * If display data isn't the same as form data, e.g. select elements, 
    * form data can be places in the cell by adding a hidden span element
    * as the first element in the cell. For example <span style="display: none;">1234</span>
    *
    * If unique id is required in the for fields, e.g. value="fieldName[id]" uniqueId options 
    * parameter should be set and a hidden span element with that selector should be added to the row.
    * E.g. <span class="myUnique" style="display: none; uniqueId: 1234;" />
    *
    * NOTICE: place unique span to non-editable column or after value-span, if one is used.
    */
	TableEdit.prototype = {
      renderRow: function(oldRow) {
        var oldCells;
        var id = null;
        var cells = [];
        var row = $('<tr />').addClass(this.options.rowClass);
        var me = this;
  
        if(oldRow) { //break down display row to cells
          oldCells = oldRow.find('td');
          id = oldRow.find(this.uniqueId).text();
        } else {
          oldCells = false;
        }
        if(me.options.useId) { 
        	me.counter--;
	    }
	    jQuery.each(this.fields,function(i,v) {
          if(id && me.options.useId) {
              i = i + "["+id+"]";
          } else if(me.options.useId) { //generate negative id for new row field
              i = i + "["+me.counter+"]";
          }
          var oldCell;
          var oldCellVal;
          if(oldCells) {
            oldCell = $(oldCells.get(v.cell));
            oldCellVal = oldCell.text();
          } else {
            oldCellVal = null;
          }
          var tmp = (cells[v.cell] = $('<td />')); //editable row
          var val;
          if(v.data == null && oldCells != false) {
            var oc;
            if((oc = oldCell.find('span:hidden:eq(0)')).length == 1) { //inline form value container found
              val = jQuery.trim(oc.text());
            } else {
              val = jQuery.trim(oldCell.text());
            }
          } else {
            val = (typeof(v.data) == 'function') ? val = v.data(oldCellVal,me.options) : v.data;
          }
          if(v.type == "text" || v.type == "hidden") {
            if(val == null) { val = ""; }
            var inp = $('<input type="'+v.type+'" name="'+i+'" value="'+val+'" />').appendTo(tmp);
          	if(v.size) { inp.attr("size",v.size) }
          } else if(v.type == "reset") {
          	$('<input type="reset" value="Cancel" />').appendTo(tmp);
          } else if(v.type == "select") {
            var sel = $('<select />').attr('name',i).appendTo(tmp);
            if(typeof(val) == 'object') {
              jQuery.each(val,function(dk,dv) {
                 $('<option />').attr('value',dk).appendTo(sel).text(dv);
              });
            }
            if(oldCell != null && (oc = oldCell.find('span:hidden:eq(0)')).length == 1) {
            	sel.val(jQuery.trim(oc.text()));
            }
          } else if(v.type == "date") {
          	if(val == null) { val = ""; }
            $('<input type="text" />').attr('size',14).css('float','left').addClass('datePickerField')
                          .appendTo(tmp).val(val).datePicker({displayClose: true, createButton: true, clickInput: false});
          }
        });
       
        for(var i = 0; i < cells.length; i++) {
          if(typeof(cells[i]) == 'undefined') {
            $('<td />').addClass(this.options.cellClass).appendTo(row); //render all colums, even those without form data
          } else {
            cells[i].addClass(this.options.cellClass).appendTo(row);
          }
        }
        return row;
      },
      /**
       * Replace an existing row with editable one.
       */
      edit: function(element) {
        var row = $(element).parents('tr:eq(0)');
        var newRow = this.renderRow(row);
        var oldData = row.replaceWith(newRow);
        row = newRow;
        var me = this;
        row.find(':reset').unbind('click').click(function() {  		
          row.replaceWith(oldData);
          if(me.container.find(":reset").length == 0) {
            me.submit.hide();
            me.register(oldData);
          }
        });
        this.submit.show();
		},
      /**
       * Delete an existing row and call user callback if one is provided.
       */ 
      del: function(element,id) {
	      var row = $(element).parents('tr:eq(0)');
          var id = row.find(this.uniqueId).text();
          var me = this;
          if(this.deleteaction) {
          	var param = {};
          	param[this.options.submitParam] = id;
          	jQuery.post(this.deleteaction,param,function() {
            	row.remove();
            	if(me.options.deleteCb) {
              		me.options.deleteCb(id,me.options);
            	}
          	});
          } else if(this.options.deleteCb) {
          	if(me.options.deleteCb(id,me.options)) {
          		row.remove();
          	}
          }
		}, 
      /**
       * Add a new editable row to the end of the table.
       */
      add: function() {
       var html = this.renderRow(null);
       var visible = this.container.is(":visible");
       if(!visible)  { this.container.show(); }
       this.tbody.append(html);
       var me = this;
       html.find(":reset").click(function() {
        html.remove();
        if(!visible) { me.container.hide(); } 
        if(me.container.find(":reset").length == 0) {
          me.submit.hide();
        }
        return false;
       });
       me.submit.show();
       return false;
     },
     register: function(target) {
     	var me = this;
		target.find(this.options.edit).unbind('click',me.register).click(function() {
			me.edit(this);  
			return false;
		});
		target.find(this.options.del).unbind('click',me.register).click(function() {
			me.del(this);
			return false;
		});
	 }
     
	};
	jQuery.fn.extend({
		inlineTableEdit: function(opt) {
         var options = {
          add: '.table_edit_add',
          edit: '.table_edit_edit',
          del: '.table_edit_delete',
          container: this,
          uniqueId: 'span.uniqueId'
         };
         jQuery.extend(options,opt);
			this.each(function() {
          	var obj = jQuery.data(this,"inplaceTableEdit");
          	if(obj == null) {
	          	var handler = new TableEdit(options);
	            jQuery.data(this,"inplaceTableEdit",handler);
	            var me = $(this);
	            me.find(options.uniqueId).hide();
	            $(options.add).click(function() {
	              handler.add(this);
	              return false;
	   		   });
				handler.register($(this));
			 }
         });
		  return this;
      }
	});
})(jQuery);
