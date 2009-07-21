$(document).ready(function() { 
  module("Dynamics: DynamicTable", {
    setup: function() {
      this.mockControl = new MockControl();
    }, teardown: function() {
      this.mockControl.verify();
    }});
  
});