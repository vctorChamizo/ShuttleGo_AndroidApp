const request = require('request');
const fs = require("fs");

const data = JSON.parse(fs.readFileSync("usuario.json",{encoding:"UTF-8"}));

request.post("https://us-central1-shuttlebus-c7c54.cloudfunctions.net/signin",(error,response,body)=>{

if(error) console.log(error);
else console.log(body);

}).form(data);