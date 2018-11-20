const functions = require('firebase-functions');
const personSA = require("./business/personSA");

exports.signin = functions.https.onRequest((request, response) =>{

  //all the request body is here
  const data = request.body.data;

  let user = data.user;

  personSA.signIn(user.email, user.password)
  
  .then((sucessful)=>
    response.status(200).send(sucessful)        //200 = OK
  )
  .catch((err)=>{
    console.log(err);
    response.status(500).send(""+err);          //500 = Internal Server Error
    }
  )

});
