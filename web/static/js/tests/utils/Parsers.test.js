
$(document).ready(function() {
  
  module("Utilities: Parsers");
  
  test("TimeStrToMinutes", function() {
    equals(ParserUtils.timeStrToMinutes("1h"), 60);
    equals(ParserUtils.timeStrToMinutes("1"), 60);
    equals(ParserUtils.timeStrToMinutes("1.5h"), 90);
    equals(ParserUtils.timeStrToMinutes("30min"), 30);
    equals(ParserUtils.timeStrToMinutes("1h 30min"), 90);
    equals(ParserUtils.timeStrToMinutes("1.5"), 90);
  });
});