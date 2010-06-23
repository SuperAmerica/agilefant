var DynamicTableRowButton = function DynamicTableRowButton(options, controller, model, parentView) {
    this.label = options.label;
    this.callback = options.callback;
    this.controller = controller;
    this.model = model;
    this.parentView = parentView;
};

DynamicTableRowButton.prototype = new CommonFragmentSubView();

/**
 * @private
 */
DynamicTableRowButton.prototype.getHTML = function() {
  return '<div id="'+this.getId()+'" style="width: 68px;"><div class="actionColumn"><div class="edit" style="width: 44px">' + this.label + '</div></div></div>';
  /*
  var me = this;
    this.container = $('<div />').width("68px").appendTo(
        this.parentView.getElement());
    
    this.button = $('<div class="actionColumn"><div class="edit" style="width: 44px">' + this.label + '</div></div>');
    this.button.appendTo(this.container);

    this.clickListener = function(event) {
        me.callback();
    };
    
    this.button.click(function(event) { me.callback.call(me.controller); });
    this.element = this.container;
*/
};
