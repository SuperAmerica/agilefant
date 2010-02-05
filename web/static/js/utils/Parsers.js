var ParserUtils = {
    TimeStrPattern: /\s*((\d+)([.,]\d+)?h)?\s*((\d+)min)?\s*/,
    TimeStrNumericPattern: /^\s*(\d+)([.,]\d+)?(h)?\s*$/,

    timeStrToMinutes: function(timeStr) {
      var matches = ParserUtils.TimeStrNumericPattern.exec(timeStr);
      var hours = 0;
      var minutes = 0;
      var hourDecimals = 0.0;
      if (matches && matches.length > 0) {
        hours = parseInt(matches[1], 10);
        hourDecimals = parseFloat(matches[2]);
      } else {
        matches = ParserUtils.TimeStrPattern.exec(timeStr);
        hours = parseInt(matches[2], 10);
        hourDecimals = parseFloat(matches[3]);
        minutes = parseInt(matches[5], 10);
      }
      if(isNaN(minutes)) { minutes = 0; }
      if(isNaN(hours)) { hours = 0; }
      if(isNaN(hourDecimals)) { hourDecimals = 0; }
      return minutes + Math.round((hours +hourDecimals)*60);
    }
};