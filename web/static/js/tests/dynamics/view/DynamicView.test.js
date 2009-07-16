$(document).ready(function() { 
	module("Dynamics: Dynamic view", {
		setup: function() {
			this.mockControl = new MockControl();
			this.mockModel = this.mockControl.createMock(CommonModel);
			this.mockController = this.mockControl.createMock(CommonController);
			DynamicView.instanceCounter = 1;
		}, teardown: function() {
			this.mockControl.verify();
		}
	});
	test("init with DOM parent", function() {
		var testable = new DynamicView();
		var parent = $('<div/>');
		this.mockModel.expects().addListener(TypeOf.isA(Function));
		this.mockModel.expects().addListener(TypeOf.isA(Function));
		testable.init(this.mockController, this.mockModel, parent);
		equals( testable.getController(), this.mockController, "Controller set");
		equals( testable.getModel(), this.mockModel, "Model set");
		equals(testable.getParentElement(),parent, "Parent element set");
		ok(!testable.getParentView(), "Shouldn't have a parent view");
	});
	
	test("init with DynamicView parent", function() {
		var testable = new DynamicView();
		var domParent = $('<div/>');
		var parent = new DynamicView();
		parent.subViews = {};
		parent.parentElement = domParent;
		parent.getElement = function() { return domParent; };
		
		this.mockModel.expects().addListener(TypeOf.isA(Function));
		this.mockModel.expects().addListener(TypeOf.isA(Function));
		testable.init(this.mockController, this.mockModel, parent);
		same(testable.getParentView(), parent, "Parent view set");
		same(testable.getParentElement(), domParent)
	});
});