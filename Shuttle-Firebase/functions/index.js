const functions = require('firebase-functions');
const personSA = require("./business/personSA");

exports.signin = functions.https.onRequest((request, response) =>{

  let user = request.body.user;

  personSA.signIn(user.email, user.password)
  
  .then((sucessful)=>
    response.status(200).send(sucessful)        //200 = OK
  )
  .catch((err)=>{
    console.log(new Error("database Error: "+err));
    response.status(500).send();                //500 = Internal Server Error
    }
  )

});
