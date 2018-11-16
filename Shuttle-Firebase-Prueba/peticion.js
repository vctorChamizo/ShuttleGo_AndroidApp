const request = require('request');
const fs = require("fs");

const data = JSON.parse(fs.readFileSync("usuario.json",{encoding:"UTF-8"}));

request.post("http://localhost:8010/shuttlebus-c7c54/us-central1/signin",(request)=>console.log(request)).form(data);