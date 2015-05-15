#!/usr/bin/env nodejs

var fs = require('fs');
var util = require('util');
var exec = require('child_process').exec;
var stream = require('stream');

var args_rx = /-(\w+)=(.*)/;
var args = {};

var die = function (errmsg) {
  throw errmsg;
};
var spit = function(status_code, status_msg, stream) {
  var out = process.stdout;
  out.write(util.format('%d %s\n\n', status_code, status_msg));
  stream.pipe(out);
};
var streamify = function (str) {
  var s = new stream.Readable();
  s._read = function () {};
  s.push(str);
  s.push(null);
  return s;
};

process.argv.forEach(function (val, index, array) {
  var mat = val.match(args_rx);
  if (mat) {
    var argument = mat[1];
    var value = mat[2];
    args[argument] = value;
  }
});

var tmpdir = args['tmpdir']
    || die("you must give -tmpdir=<temporary directory> as an argument");
var fsrc = tmpdir + '/source.c';
var fobj = tmpdir + '/out.js';

// create the temporary directory
fs.mkdir(tmpdir, function (err) {
  if (err) throw err;
});

// write stdin to tmpdir/submitted.c
process.stdin.pipe(fs.createWriteStream(fsrc));

// compile the program and give output
process.stdin.on('end', function() {
exec(util.format('emcc -O2 -o %s %s', fobj, fsrc),
     function(err, stdout, stderr) {
       if (!err) {
         spit(0, 'compilation succeeded.',
              fs.createReadStream(fobj));
       } else {
         spit(err.code, 'compilation failed.',
              streamify(stderr));
       }
});
});
