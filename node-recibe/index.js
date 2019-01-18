var express = require('express')
var bodyParser = require('body-parser')
var multer  = require('multer')


var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './uploads')
  },
  filename: function (req, file, cb) {
    cb( null, file.fieldname + '-' + Date.now() + '.pdf' )
  }
})

var upload = multer({ storage: storage })


var app = express()
var PORT = 9090

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.post('/node', function (req, res) {
  // create user in req.body
  console.log(req.body)
  

  res.json({data:"exitoso", data2:"exitoso 2"})
})

/**
 * Revice desde spring con rest template, en forma de form data el PDF 
 */
app.post('/node-receive-file', upload.single('file'), function (req, res) {
  // create user in req.body
  console.log("node-receive-file")
  console.log(req.file)
  console.log("\n\n", req.body)
  console.log("\n\n", req.headers)
  res.json({data:"exitoso", data2:"exitoso 2"})

})


app.get('/prueba', function (req, res) {
  // create user in req.body
  console.log(req.body)

  res.json({data:"exitoso"})
})


app.listen(PORT);
console.log('Magic happens on port ' + PORT);