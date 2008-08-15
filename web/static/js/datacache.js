
var dataCacheClass = function() { this.allUsers = null; }
dataCacheClass.prototype.getAllUsers = function() {
    if (this.allUsers == null) {
        var json;
        $.ajax({
            url: "getUserJSON.action",
            async: false,
            dataType: 'json',
            type: 'POST',
            success: function(data, status) {
                json = data;
            }
        });
        this.allUsers = json;
    }
    return this.allUsers;
}

var jsonDataCache = new dataCacheClass();