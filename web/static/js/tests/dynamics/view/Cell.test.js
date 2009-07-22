$(document).ready(function() { 
	module("Dynamics: DynamicTableCell", {
		setup: function() {
			this.mockControl = new MockControl();
			this.mockRow = this.mockControl.createMock(DynamicTableRow);
			this.cellConfig = this.mockControl.createMock(DynamicTableColumnConfiguration);
	}, teardown: function() {
			this.mockControl.verify();
		}
	});
	
	test("initialize width and class set", function() {
		this.cellConfig.expects().getWidth().andReturn("10%");
		this.cellConfig.expects().getWidth().andReturn("10%");
		this.cellConfig.expects().getMinWidth().andReturn("150");
		this.cellConfig.expects().getMinWidth().andReturn("150");
		this.cellConfig.expects().isFullWidth().andReturn(null);
		this.cellConfig.expects().getCssClass().andReturn("testClass");
		this.cellConfig.expects().getCssClass().andReturn("testClass");
		this.cellConfig.expects().getSubViewFactory().andReturn(null);
		this.cellConfig.expects().isVisible().andReturn(true);
		var testable = new DynamicTableCell(this.mockRow, this.cellConfig);

		same(testable.getElement().css("width"), "10%", "Width correct");
		same(testable.getElement().attr("min-width"), "150", "Min-width correct");
		same(testable.getElement().css("clear"), "none", "Clear correct");
		ok(testable.getElement().hasClass("testClass"), "Css class correct");
	});
	test("initialize width and class not set", function() {
		this.cellConfig.expects().getWidth().andReturn(null);
		this.cellConfig.expects().getMinWidth().andReturn(null);
		this.cellConfig.expects().isFullWidth().andReturn(true);
		this.cellConfig.expects().getCssClass().andReturn("");
		this.cellConfig.expects().getSubViewFactory().andReturn(null);
    this.cellConfig.expects().isVisible().andReturn(true);


				
		var testable = new DynamicTableCell(this.mockRow, this.cellConfig);

		same(testable.getElement().css("width"), "auto", "Width correct");
		same(testable.getElement().attr("min-width"), undefined, "Min-width correct");
		same(testable.getElement().css("clear"), "left", "Clear correct");
		ok(!testable.getElement().hasClass("testClass"), "Css class correct");
	});
});