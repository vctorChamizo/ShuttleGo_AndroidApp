const functions = require('firebase-functions');
const personSA = require("./business/personSA");

/*
  Coment the function
*/
exports.signin = functions.https.onRequest((request, response) =>{

  const data = request.body.data;
  let user = data.user;

  personSA.signIn(user.email, user.password)
  .then((res) => { response.status(200).send(res); })
  .catch((err) => { response.status(500).send(err); })
});//signin
