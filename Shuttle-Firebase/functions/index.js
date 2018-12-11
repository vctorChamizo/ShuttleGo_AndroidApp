const functions = require('firebase-functions');
const personSA = require("./business/personSA");
const originSA = require("./business/originSA");
const ERROR = require("./errors");

/*
  DATA EXAMPLES:
    - SIGNIN: signin({user:{email:"jose@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
    - SIGNUP: signup({user:{surname:"ramirez",number:123,email:"joos@gmil.com",password:"123",type:"driver",name:"jose"}}, {headers: {Authorization: 'Bearer $token'}});
*/

/**
 * @description Check the correct login of a user in the application.
 * @returns {Promise} User data in the correct case and error in the wrong case.
 */
exports.signin = functions.https.onCall((data, context) =>{
  checkData(data);
  let user = data.user;
  return personSA.signIn(user.email, user.password)
  .then(result=>result,error=>error);
});

/**
 * @description Signs up a new user if not exists
 * @returns {Promise} a promise that returns the new user with the new id or an error
 */
exports.signup = functions.https.onCall((data, context)=>{
  checkData(data);
  let newUser = data.user;
  return personSA.signUp(newUser)
  .then(result =>{return {signedUp:true}},error=>error);
})

/**
 * @description Get all route origins
 * @returns {Promise} a promise that return a list of strings
 */
exports.getAllOrigins = functions.https.onCall((data,context)=>{
  return originSA.getAllOrigins()
  .then(result=>result,error=>error);
})

/**
 * @description
 * @param {*} data 
 */
function checkData(data){
  if(data == null || data.user == null || data.user.email == null || data.user.password == null)
    data={user:{email:"",password:""}};
}//checkData
