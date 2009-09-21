$(document).ready(function() { 
    module("Dynamics: DynamicTableRowActions", {
        setup: function() {
        this.parent = $('<div />').appendTo(document.body);
        this.mockControl = new MockControl();
        this.parentView = this.mockControl.createMock(DynamicView);
        this.testController = function() {
        };
        this.testController.prototype.func1 = function(model, view) {
        };
        this.testController.prototype.func2 = function(model, view) {
        };
        this.controller = this.mockControl.createMock(this.testController);
    }, teardown: function() {
        this.parent.remove();
        this.mockControl.verify();
    }
    });

    test("interaction", function() {
        this.parentView.expects().getElement().andReturn(this.parent);

        var items = [
            {text: "Item 1", callback: this.controller.func1},
            {text: "Item 2", callback: this.controller.func2}
        ];
        
        var testable = new DynamicTableRowActions(items, this.controller, null, this.parentView);
        var menuButton = this.parent.children("div").children("div.actionColumn");

        same($('.actionCell').length, 0, "Menu not open");
        // expect when opening the menu
        this.parentView.expects().getElement().andReturn(this.parent);
        menuButton.click();
        same($('.actionCell').length, 1, "Menu open");

        // click first menu item
        this.controller.expects().func1(null, this.parentView);
        $('ul.actionCell').children("li:eq(0)").click();

        // check that the menu is hidden properly!
        same($('.actionCell').size(), 0, "Menu not open");
    });

    test("disabled items", function() {
        this.parentView.expects().getElement().andReturn(this.parent);

        var items = [
            {text: "Item 1", callback: this.controller.func2, enabled: function() { return false; }},
            {text: "Item 2", callback: this.controller.func2, enabled: false },
            {text: "Item 3", callback: this.controller.func1, enabled: function() { return true; }},
            {text: "Item 4", callback: this.controller.func2, enabled: true }
        ];
        
        var testable = new DynamicTableRowActions(items, this.controller, null, this.parentView);
        var menuButton = this.parent.children("div").children("div.actionColumn");

        same($('.actionCell').length, 0, "Menu not open");
        // expect when opening the menu
        this.parentView.expects().getElement().andReturn(this.parent);
        menuButton.click();
        
        this.controller.expects().func1(null, this.parentView);
        same($('.actionCell').length, 1, "Menu open");

        $('ul.actionCell').children("li:eq(0)").click();
        same($('.actionCell').length, 1, "Menu open");

        $('ul.actionCell').children("li:eq(1)").click();
        same($('.actionCell').length, 1, "Menu open");

        $('ul.actionCell').children("li:eq(2)").click();
        same($('.actionCell').length, 0, "Menu closed");
    });
    
    test("close on window click", function() {
        this.parentView.expects().getElement().andReturn(this.parent);

        var items = [
            {text: "Item 1", callback: this.controller.func1, enabled: function() { return false; }},
            {text: "Item 2", callback: this.controller.func2, enabled: false },
            {text: "Item 3", callback: this.controller.func1, enabled: function() { return true; }},
            {text: "Item 4", callback: this.controller.func2, enabled: true }
        ];
        
        var testable = new DynamicTableRowActions(items, this.controller, null, this.parentView);
        var menuButton = this.parent.children("div").children("div.actionColumn");

        same($('.actionCell').length, 0, "Menu not open");
        // expect when opening the menu
        this.parentView.expects().getElement().andReturn(this.parent);
        menuButton.click();
        same($('.actionCell').length, 1, "Menu open");

        // close the menu
        $(window).click();
        same($('.actionCell').length, 0, "Menu not open");
    });
});