$(document).ready(function() { 
	module("Dynamics: Dynamic view", {
		setup: function() {
			this.mockControl = new MockControl();
			this.mockModel = this.mockControl.createMock(CommonModel);
			this.mockController = this.mockControl.createMock(CommonController);
		}, teardown: function() {
			this.mockControl.verify();
		}
	});
	test("init", function() {
		var testable = new DynamicView();
		this.mockModel.expects().addEditListener(TypeOf.isA(Function));
		this.mockModel.expects().addDeleteListener(TypeOf.isA(Function));
		testable.init(this.mockController, this.mockModel);
		equals(this.mockController, testable.getController());
		equals(this.mockModel, testable.getModel());
	});
});