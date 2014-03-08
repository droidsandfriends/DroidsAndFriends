function $(id) {
  return document.getElementById(id);
}

function $dom(tagName, var_args) {
  var el = document.createElement(arguments[0]);
  for (var i = 1, numArgs = arguments.length; i < numArgs; i++) {
    var child = arguments[i];
    el.appendChild((typeof child == 'string') ? document.createTextNode(child) : child);
  }
  return el;
}

function $addHandler(element, eventName, fn) {
  if (!element.addEventListener) {
    // IE
    element.attachEvent('on' + eventName, fn);
  } else {
    // Non-IE
    element.addEventListener(eventName, fn);
  }
}
