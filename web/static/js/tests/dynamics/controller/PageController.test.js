$(document).ready(function() {
  
  module("Dynamics: Page controller", {
    setup: function() {
      this.mockControl = new MockControl();
      ModelFactory.instance = this.mockControl.createMock(ModelFactory);

      var originalInit = PageController.prototype._init;
      
      PageController.prototype._init = function() {};
      this.PageControlWithoutInit = function() {};
      this.PageControlWithoutInit.prototype = new PageController();
      PageController.prototype._init = originalInit;
      
      window.pageController = new this.PageControlWithoutInit();
    },
    teardown: function() {
      this.mockControl.verify();
      ModelFactory.instance = null;
    }
  });
  
  test("Initialization", function() {
    window.pageController = null;    
    
    var original = ModelFactory.updateObject;
    var originalInit = PageController.prototype._init;
    
    var expected = { id: 5 };
    var user = new UserModel();
    
    var updateObjectCalled = false;
    ModelFactory.updateObject = function(data) {
      equals(expected, data, "Expected data correct");
      updateObjectCalled = true;
      return user;
    }
    
    PageController.prototype._init = function() {};
    
    PageController.initialize(expected);
    
    ok(updateObjectCalled, "User object instantiated");
    ok((window.pageController instanceof PageController), "Page controller set");
    equals(user, PageController.getInstance().currentUser, "Current user set");
    
    ModelFactory.updateObject = original;
    PageController.prototype._init = originalInit;
  });
  
  
  test("Menu refresh", function() {
    window.menuController = {};
    var reloadCallCount = 0;
    window.menuController.reload = function() {
      reloadCallCount++;
    };
    
    PageController.getInstance().refreshMenu();
    
    same(reloadCallCount, 1, "Menu refresh called once");
    
    window.menuController = null;
  });
  
  test("Refresh root", function() {
    var orig = ModelFactory.reloadRoot;
    var rootReloaded = false;
    ModelFactory.reloadRoot = function() {
      rootReloaded = true;
    };
    
    window.pageController.refreshContent();
    
    ok(rootReloaded, "Root object reloaded");
    ModelFactory.reloadRoot = orig;
  });
  
  test("Toggle menu", function() {
    var wrapper = $('<div/>').attr('id','outerWrapper').appendTo(document.body);
    
    var cookieCallCount = 0;
    window.pageController._updateMenuCookie = function() {
      cookieCallCount++;
    };
    
    PageController.getInstance().toggleMenu();
    ok(wrapper.hasClass('menu-collapsed'), "Menu collapsed");
    PageController.getInstance().toggleMenu();
    ok(!wrapper.hasClass('menu-collapsed'), "Menu not collapsed");
    
    same(cookieCallCount, 2, "Cookie updated twice");
    
    wrapper.remove();
  });
});

