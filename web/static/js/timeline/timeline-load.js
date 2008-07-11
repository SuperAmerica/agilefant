/* Initialize the Timeline object */
window.Timeline = new Object();
window.Timeline.DateTime = window.SimileAjax.DateTime;
window.Timeline.clientLocale = "en";
window.Timeline.serverLocale = "en";




$(document).ready(function() {
    /* Set the month names */
    Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
    
    Timeline.DefaultEventSource.Event = function(
        id, name, type, start, end) {
        
        this._id = id;
        
        this._instant = false;
    
        this._start = start;
        this._end = (end != null) ? end : start;
    
        this._latestStart = start;
        this._earliestEnd = end;
    
        this._text = "";
        this._description = "";
        this._image = null;
        this._link = null;
        this._title = name;
    
        this._icon = null;
        this._color = null;
        this._textColor = '#666666';
        this._classname = 'timeline-' + type;
        this._bandClass = 'timeline-band-' + type;
    
        this._wikiURL = null;
        this._wikiSection = null;
    };
    
    Timeline.DefaultEventSource.prototype.loadJSON = function(data) {
        var added = false;
        var parseDateTimeFunction = this._events.getUnit().getParser("iso8601");
        if (data && data.contents){
            for (var i=0; i < data.contents.length; i++){
                var event = data.contents[i];
            
                var evt = new Timeline.DefaultEventSource.Event(
                    event.id,
                    event.name,
                    event.type,
                    parseDateTimeFunction(event.startDate),
                    parseDateTimeFunction(event.endDate)
                );
            
                evt._obj = event;
                evt.getProperty = function(name) {
                    return this._obj[name];
                };
    
                this._events.add(evt);
                added = true;
            }
        }
   
        if (added) {
            this._fire("onAddMany", []);
        }
    };
    
    
    Timeline.DefaultEventSource.Event.prototype = {
	    getID:          function() { return this._id; },
	    
	    isInstant:      function() { return this._instant; },
	    isImprecise:    function() { return this._start != this._latestStart || this._end != this._earliestEnd; },
	    
	    getStart:       function() { return this._start; },
	    getEnd:         function() { return this._end; },
	    getLatestStart: function() { return this._latestStart; },
	    getEarliestEnd: function() { return this._earliestEnd; },
	    
	    getText:        function() { return this._title; },
	    getDescription: function() { return this._title; },
	    getImage:       function() { return this._image; },
	    getLink:        function() { return this._link; },
	    
	    getIcon:        function() { return this._icon; },
	    getColor:       function() { return this._color; }, 
	    getTextColor:   function() { return this._textColor; },
	    getClassName:    function() {return this._classname;  },
	    
	    getProperty:    function(name) { return null; },
	    
	    getWikiURL:     function() { return this._wikiURL; },
	    getWikiSection: function() { return this._wikiSection; },
	    setWikiInfo: function(wikiURL, wikiSection) {
	        this._wikiURL = wikiURL;
	        this._wikiSection = wikiSection;
	    },
	    
	    fillDescription: function(elmt) {
	        elmt.innerHTML = this._description;
	    },
	    fillWikiInfo: function(elmt) {
	        if (this._wikiURL != null && this._wikiSection != null) {
	            var wikiID = this.getProperty("wikiID");
	            if (wikiID == null || wikiID.length == 0) {
	                wikiID = this.getText();
	            }
	            wikiID = wikiID.replace(/\s/g, "_");
	            
	            var url = this._wikiURL + this._wikiSection.replace(/\s/g, "_") + "/" + wikiID;
	            var a = document.createElement("a");
	            a.href = url;
	            a.target = "new";
	            a.innerHTML = Timeline.strings[Timeline.clientLocale].wikiLinkLabel;
	            
	            elmt.appendChild(document.createTextNode("["));
	            elmt.appendChild(a);
	            elmt.appendChild(document.createTextNode("]"));
	        } else {
	            elmt.style.display = "none";
	        }
	    },
	    fillTime: function(elmt, labeller) {
	        if (this._instant) {
	            if (this.isImprecise()) {
	                elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
	                elmt.appendChild(elmt.ownerDocument.createElement("br"));
	                elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
	            } else {
	                elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
	            }
	        } else {
	            if (this.isImprecise()) {
	                elmt.appendChild(elmt.ownerDocument.createTextNode(
	                    labeller.labelPrecise(this._start) + " ~ " + labeller.labelPrecise(this._latestStart)));
	                elmt.appendChild(elmt.ownerDocument.createElement("br"));
	                elmt.appendChild(elmt.ownerDocument.createTextNode(
	                    labeller.labelPrecise(this._earliestEnd) + " ~ " + labeller.labelPrecise(this._end)));
	            } else {
	                elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
	                elmt.appendChild(elmt.ownerDocument.createElement("br"));
	                elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
	            }
	        }
	    },
	    fillInfoBubble: function(elmt, theme, labeller) {
	        var doc = elmt.ownerDocument;
	        
	        var title = this.getText();
	        var link = this.getLink();
	        var image = this.getImage();
	        
	        if (image != null) {
	            var img = doc.createElement("img");
	            img.src = image;
	            
	            theme.event.bubble.imageStyler(img);
	            elmt.appendChild(img);
	        }
	        
	        var divTitle = doc.createElement("div");
	        var textTitle = doc.createTextNode(title);
	        if (link != null) {
	            var a = doc.createElement("a");
	            a.href = link;
	            a.appendChild(textTitle);
	            divTitle.appendChild(a);
	        } else {
	            divTitle.appendChild(textTitle);
	        }
	        theme.event.bubble.titleStyler(divTitle);
	        elmt.appendChild(divTitle);
	        
	        var divBody = doc.createElement("div");
	        this.fillDescription(divBody);
            theme.event.bubble.bodyStyler(divBody);
            elmt.appendChild(divBody);
        
            var divTime = doc.createElement("div");
            this.fillTime(divTime, labeller);
            theme.event.bubble.timeStyler(divTime);
            elmt.appendChild(divTime);
        
            var divWiki = doc.createElement("div");
            this.fillWikiInfo(divWiki);
            theme.event.bubble.wikiStyler(divWiki);
            elmt.appendChild(divWiki);
        }
    };
    
    
    Timeline.loadJSON = function(url, cb) {
        var icb = function(data, status) { 
        cb(data); 
        }
        
        var json = jQuery.getJSON(url,{},icb);
    
    }
    /* Create the datasource */
    var eventSource = new Timeline.DefaultEventSource();
    
    /* Set the band properties */
    var bandInfos = [
    Timeline.createBandInfo({
        showEventText:  true,
        eventSource:    eventSource, 
        width:          "100%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 100
    })/*,
    Timeline.createBandInfo({
        showEventText:  false,
        eventSource:    eventSource2,
        width:          "50%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 100
    })*/
  ];
  /*
  bandInfos[1].syncWith = 0;
  bandInfos[1].highlight = false;*/
  
  tl = Timeline.create(document.getElementById("productTimeline"), bandInfos);
  /* Get the JSON data */
  var timelineActionURL = "timelineData.action?productId=" + productId;
  Timeline.loadJSON(timelineActionURL, function(json) { eventSource.loadJSON(json); });
});


