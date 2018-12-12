const functions = require('firebase-functions');
const personSA = require("./business/personSA");
const originSA = require("./business/originSA");
const ERROR = require("./errors");




//----------------EXPORTED FUNCTIONS---------------------

//-----------------Person Functions---------------
/*
  DATA EXAMPLES:
    - SIGNIN: signin({user:{email:"jose@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
    - SIGNUP: signup({user:{surname:"ramirez",number:123,email:"joos@gmil.com",password:"123",type:"driver",name:"jose"}}, {headers: {Authorization: 'Bearer $token'}});
    - GETORIGINBYID: getOriginById({origin:{id:"nTREdQ19BRPRACy5JBiN"}}, {headers: {Authorization: 'Bearer $token'}});
*/

/**
 * @description Check the correct login of a user in the application.
 * @returns {Promise} a promise with user data in the correct case and error in the wrong case.
 */
exports.signin = functions.https.onCall((data, context) =>{
  return checkData(data)
  .then(()=>checkUser(data.user))
  .then(()=>personSA.signIn(data.user.email, data.user.password))
  .then(result=>result,error=>error);
});

/**
 * @description Signs up a new user if not exists
 * @returns {Promise} a promise that returns "signedUp:true" if its correct(it allows client to check if connection goes well) or an error
 */
exports.signup = functions.https.onCall((data, context)=>{
  return checkData(data)
  .then(()=>checkUser(data.user))
  .then(()=>personSA.signUp(data.user))
  .then(()=>{return {signedUp:true}},error=>error);
})




//----------------origin Functions---------------
/**
 * @description Get all route origins
 * @returns {Promise} a promise that return a list of route names
 */
exports.getAllOrigins = functions.https.onCall((data,context)=>{
  return originSA.getAllOrigins()
  .then(result=>{return {origins:result}},error=>error);
})


exports.getOriginById = functions.https.onCall((data,context)=>{
  return checkData(data)
  .then(()=>checkOrigin(data.origin))
  .then(()=>originSA.getOriginById(data.origin.id))
  .then(result=>result,error=>error);
})

exports.modifyOrigin = functions.https.onCall((data, context)=>{
  return checkData(data)
  .then(()=>checkOrigin(data.origin))
  .then(()=>personSA.checkUser(data.user,"admin"))  //only admin can modify origins so I check it.
  .then(()=>originSA.modifyOriginById(data.origin.id,data.origin))
  .then(()=>{return {Modified:true}},error=>error);
});


//---------PRIVATE METHODS----------
/**
 * @description Avoid internal null errors, it should be called at the first line of all exported functions.
 * @param {*} data from callable functions.
 */
function checkData(data){
  return new Promise((resolve,reject)=>{
    if(data == null) reject(ERROR.necessaryDataIsNull);
    else resolve();
  });
}//checkData

function checkUser(user){
  return new Promise((resolve,reject)=>{
    if(user == null || user.email == null || user.password == null) reject(ERROR.necessaryDataIsNull);
    else resolve();
  });
}

function checkOrigin(origin){
  return new Promise((resolve,reject)=>{
    if(origin == null) reject(ERROR.necessaryDataIsNull);
    else resolve();
  });
}
