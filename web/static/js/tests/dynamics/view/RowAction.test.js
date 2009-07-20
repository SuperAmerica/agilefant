$(document).ready(function() { 
	module("Dynamics: DynamicTableRowActions", {
		setup: function() {
			this.parent = $('<div />').appendTo(document.body);
			this.mockControl = new MockControl();
			
		}, teardown: function() {
		
			this.mockControl.verify();
		}
	});
});