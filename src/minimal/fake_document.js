React = require("react");
domino = require("domino");
fs = require("fs");

var html = fs.readFileSync(__dirname + '/index.html', 'utf8');
global.window = domino.createWindow(html);
global.document = document = window.document;
