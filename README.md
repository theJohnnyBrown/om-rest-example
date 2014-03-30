# om-rest-example

An example of DRY client+server templating and routing, using om and secretary.

# Hacking

As this is primarily an expirimental project, alterations, forks, etc. are encouraged. The way I develop is to run `lein cljsbuild auto node browser-dev` in one terminal, and `supervisor -w main.js main.js` (after installing [node-supervisor](https://github.com/isaacs/node-supervisor) locally) in another. This way changes are automatically compiled, to main.js, and the node server automatically restarts.

# About

I want to make the dream of shared server+browser code happen in real life, not just experiments like this one (at least in clojurescript-land). This is what I've got working so far. I'm publishing it in this raw state in the hope of attracting feedback or encouragement toward that goal.

In my opinion the obvious targets for code sharing would be html templates, route definitions, and data validation. One of the major benefits would be to render html initially on the server, rather than the pattern of serving and empty div, then using javascript to fill it in.

This app is written mostly in clojurescript, which is executed both in node.js and in the browser. I've written the very beginnings of api routes on the node server, but these are intended only as a shim for the client to run against. Eventually I'd like to define the structure of the app's resources in cljx, and use that definition to create RESTful routes served by ring and liberator.

`:first` is used throughout as a standin for :id or something more sensible

## Other projects with similar goals

 + https://github.com/augustl/react-nashorn-example and https://github.com/brendanyounger/omkara both take the approach of starting a clojure/java server, which embeds a javascript engine to render templates.

 + https://github.com/seabre/matchcolor is another example of clojure on node.js
 
 + https://github.com/swannodette/om/wiki/Intermediate-Tutorial and http://swannodette.github.io/todomvc/labs/architecture-examples/om/index.html were the source of most of the UI code in this app.
 
# Fantasies

I'd like to run the server in jvm clojure, or at least the api server, using [liberator](https://github.com/clojure-liberator/liberator) to generate a REST api. We'd then define data validation and API routes using cljx to share them between client and server. To render templates, we could either embed a node/nashborn/rhino process in the jvm server, or have a running node server recieve requests to e.g. `/contact/fred/` and get data from the api by making requests to `/api/contacts/fred/` like any other client.

## License

Copyright © 2014 Jonathan Brown, Distributed under the Eclipse Public License.

## Thanks

I am indebted to @swannodette and everyone else who brought the clojure ecosystem to the point that things like these are possible.

Most of the code in this project is copied from the "other projects" listed above, and from om's tutorials.
