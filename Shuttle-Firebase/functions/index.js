const functions = require('firebase-functions');
const personSA = require("./business/personSA");



//formato para pruebas NOOO BORRARR!
//signin({user:{email:"jose@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});


/**
 * @description Check the correct login of a user in the application.
 * @returns {Promise} User data in the correct case and error in the wrong case.
 */
exports.signin = functions.https.onCall((data, context) =>{
  let user = data.user;
  return personSA.signIn(user.email, user.password)
  .then(result=>result,error=>error);
});

/**
 * @description Signs up a new user if not exists
 * @returns {Promise} a promise that returns the new user with the new id or an error
 */
exports.signup = functions.https.onCall((data,context)=>{
  let newUser = data.user;
  return personSA.signUp(newUser)
  .then(result =>result.error,error=>error);
})
