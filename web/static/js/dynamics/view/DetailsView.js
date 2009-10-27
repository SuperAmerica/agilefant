var DetailsView = function DetailsViewLink(controller, model, parentView) {
    this.controller = controller;
    this.model = model;
    this.parentView = parentView;
    this.dialogOpen = false;

    this.initialize();
};

DetailsView.prototype = new ViewPart();

/**
 * @private
 */
DetailsView.prototype.initialize = function() {
    var me = this;

    this.link = $("a", this.parentView.getElement()).attr("href", "#");

    this.toggleViewListener = function(event) {
        if (me.menuOpen) {
            me.close();
        } else {
            me.open();
        }
        event.stopPropagation();
        return false;
    };

    this.link.click(this.toggleViewListener);
    this.element = this.container;
    this.element = this.parentView.getElement();
};

/**
 * Display the menu
 */
DetailsView.prototype.open = function() {
    var me = this;
    $(window).click(this.toggleMenuListener);
    this.menuOpen = true;
    this.menu = $('<div style="min-width: 400px"/>').appendTo(document.body);
    var off = this.parentView.getElement().offset();
    var menuCss = {
            "position" : "absolute",
            "overflow" : "visible",
            "z-index" : "100",
            "white-space" : "nowrap",
            "top" : off.top + 18,
            "left" : off.left - 32,
            "min-width": 400
    };
    this.menu.css(menuCss);
};

/**
 * Close the menu
 */
DetailsView.prototype.close = function() {
    $(window).unbind('click', this.toggleViewListener);
    this.menu.remove();
    this.menuOpen = false;
};
