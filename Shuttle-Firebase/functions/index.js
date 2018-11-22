const functions = require('firebase-functions');
const personSA = require("./business/personSA");

/**
 * Check the correct login of a user in the application.
 * @returns User data in the correct case and null in the wrong case.
 */
exports.signin = functions.https.onCall((data, context) =>{
  let user = data.user;
  return personSA.signIn(user.email, user.password);
});
