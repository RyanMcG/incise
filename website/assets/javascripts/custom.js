jQuery(function ($) {
  var socket = new WebSocket("ws://localhost:5000/ws");
  function replacePartOfPage(event) {
    console.log(event);
    var selector = event.data.selector;
    var content = event.data.content;
    $(selector).html(content);
  }
  socket.onmessage = replacePartOfPage;
  console.log("yo");
});
