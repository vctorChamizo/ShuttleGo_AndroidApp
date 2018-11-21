const functions = require('firebase-functions');
const personSA = require("./business/personSA");

/*
  Coment the function
*/
exports.signin = functions.https.onCall((data, context) =>{
  let user = data.user;
  return personSA.signIn(user.email, user.password);
});//signin
