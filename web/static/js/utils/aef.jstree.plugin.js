/*
 * Agilefant jsTree checkboxes
 */
(function ($) {
  $.jstree.plugin("aefCheckbox", {
    defaults: {
      select_callback: $.noop
    },
    __init : function () {
      if(!this.data.ui) { throw("aef jsTree checkboxes: jsTree UI plugin not included"); }

      this.get_container()
        .bind("open_node.jstree create_node.jstree", $.proxy(function (e, data) { 
          this._prepare_checkboxes(data.rslt.obj);
        }, this))
        .bind("loaded.jstree", $.proxy(function (e) {
          this._prepare_checkboxes();
        }, this))
        .bind("node_refresh.jstree", $.proxy(function(e) {
          this._prepare_checkboxes(e.target);
        }, this))
        .delegate("ins.checkbox", "click.jstree", $.proxy(function (e) {
            this.change_state(e.target);
            e.preventDefault();
          }, this));
    },
    _fn : {
      _prepare_checkboxes : function (obj) {
        obj = !obj || obj == -1 ? this.get_container() : this._get_node(obj);
        var c = obj.is("li") && obj.hasClass("jstree-checked") ? "jstree-checked" : "jstree-unchecked";
        obj.find("a").not('.openCreateDialogLink').not(":has(.checkbox)").prepend("<ins class='checkbox'>&#160;</ins>").parent().addClass(c);
      },
      change_state : function (obj, state) {
        obj = this._get_node(obj);
        state = (state === false || state === true) ? state : obj.hasClass("jstree-checked");
        if(state) { obj.removeClass("jstree-checked").addClass("jstree-unchecked"); }
        else { obj.removeClass("jstree-unchecked").addClass("jstree-checked"); }

        this.get_settings().aefCheckbox.select_callback(this.get_checked().length);
        this.__callback(obj);
      },
      check_node : function (obj) {
        this.change_state(obj, false);
      },
      uncheck_node : function (obj) {
        this.change_state(obj, true);
      },

      is_checked : function(obj) {
        obj = this._get_node(obj);
        return obj.length ? obj.is(".jstree-checked") : false;
      },
      get_checked : function (obj) {
        obj = !obj || obj === -1 ? this.get_container() : this._get_node(obj);
        return obj.find("ul > .jstree-checked");
      },
      get_unchecked : function (obj) { 
        obj = !obj || obj === -1 ? this.get_container() : this._get_node(obj);
        return obj.find("ul > .jstree-unchecked");
      },

      show_checkboxes : function () { this.get_container().children("ul").removeClass("jstree-no-checkboxes"); },
      hide_checkboxes : function () { this.get_container().children("ul").addClass("jstree-no-checkboxes"); }
    }
  });
})(jQuery);
//*/