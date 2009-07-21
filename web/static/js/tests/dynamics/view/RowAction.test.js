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
	
	test("..", function() {
    this.parentView.expects().getElement().andReturn(this.parent);
    
    var items = [{text: "Item 1", callback: this.controller.func1},
                 {text: "Item 2", callback: this.controller.func2}
                 ];
    var testable = new DynamicTableRowActions(items, this.controller, null, this.parentView);
    var menuButton = this.parent.children("div").children("div.actionColumn");
   
    equals($('.actionCell').length, 0, "Menu not open");
    //expect when opening the menu
    this.parentView.expects().getElement().andReturn(this.parent);
    menuButton.click();
    equals($('.actionCell').length, 1, "Menu open");

    //click first menu item
    this.controller.expects().func1(null, this.parentView);
    $('ul.actionCell').children("li:eq(0)").click();
    //and the second
    this.controller.expects().func2(null, this.parentView);
    $('ul.actionCell').children("li:eq(1)").click();
    //check that the menu is still open
    ok($('.actionCell').is(":visible"), "Menu visible");
    //close the menu
    $(window).click();
    equals($('.actionCell').length, 0, "Menu not open");
    
	});
});