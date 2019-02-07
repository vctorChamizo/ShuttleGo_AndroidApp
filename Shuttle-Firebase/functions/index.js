/**
 * @module Index
 */


const functions = require('firebase-functions');
const personSA = require("./business/personSA");
const originSA = require("./business/originSA");
const ERROR = require("./errors");
const routeSA = require("./business/routeSA");



/* DATA EXAMPLES
    
    ** ACCOUNT **
      - SIGNIN: signin({user:{email:"jose@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
      - SIGNUP: signup({user:{surname:"ramirez",number:123,email:"joos@gmil.com",password:"123",type:"driver",name:"jose"}}, {headers: {Authorization: 'Bearer $token'}});
    
    ** ORIGIN **
      - GETORIGINBYID: getOriginById({origin:{id:"nTREdQ19BRPRACy5JBiN"}}, {headers: {Authorization: 'Bearer $token'}});
      - DELETEORIGIN: deleteOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{id:"nTREdQ19BRPRACy5JBiN"}}, {headers: {Authorization: 'Bearer $token'}});
      - CREATEORIGIN: createOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{name:"Barajas T5"}}, {headers: {Authorization: 'Bearer $token'}});
      - MODIFYORIGIN: modifyOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{id:"...",name:"Barajas T6"}}, {headers: {Authorization: 'Bearer $token'}});
    
    ** Route **
      - CREATEROUTE: createRoute({user:{email:"driv@gmail.com",password:"123",id:"wxn6auBOwCJDFCDs0bTx"},route:{max:2,origin:"i9BQCi6ovzC1pdBGoRYm"}}, {headers: {Authorization: 'Bearer $token'}});
      - SEARCHROUTE: searchRoute({route:{destination:28008,origin:"loquesea"}}, {headers: {Authorization: 'Bearer $token'}});})
      - ADDTOROUTE: addToRoute({address:"passenger address",route:{id:"9hiuhK8P9L1HjBvfzuWP"},user:{email:"pass@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
*/



/*---------------- Exported Functions ---------------*/


/*---------------- ACCOUNT Functions ---------------*/
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
 * @description Signs up a new user if not exists.
 * @returns {Promise} a promise that returns "signedUp:true" if its correct(it allows client to check if connection goes well) or an error
 */
exports.signup = functions.https.onCall((data, context)=>{
  return checkData(data)
  .then(()=>checkData(data.user))
  .then(()=>personSA.signUp(data.user))
  .then(()=>{return {signedUp:true}},error=>error);
})


/*---------------- ORIGIN Functions ---------------*/
/**
 * @description Get a list of id and name origins.
 * @returns {Promise} A promise that return a list of route names.
 */
exports.getAllOrigins = functions.https.onCall((data,context)=>{
  return originSA.getAllOrigins()
  .then(result=>{return {origins:result}},error=>error);
})

/**
 * @description Get all data from an Origin.
 * @returns {Promise} A promise that returns a data from an Origin.
 */
exports.getOrigin = functions.https.onCall((data,context)=>{
  return checkData(data)
  .then(()=>checkOrigin(data.origin))
  .then(()=>originSA.getOriginById(data.origin.id))
  .then(result=>result,error=>error);
})

/**
 * @description modify an origin.
 * @returns {Promise}
 */
exports.modifyOrigin = functions.https.onCall((data, context)=>{
  return checkData(data)
  .then(()=>checkOrigin(data.origin))
  .then(()=>checkUser(data.user,"admin"))  //only admin can modify origins so I check it.
  .then(()=>originSA.modifyOriginById(data.origin.id,data.origin))
  .then(()=>{return {modified:true}},error=>error);
});

/**
 * @description Delete an origin.
 * @returns {Promise}
 */
exports.deleteOrigin = functions.https.onCall((data,context)=>{
  return checkData(data)
  .then(()=>checkUser(data.user,"admin")) //only admin can delete a origin.
  .then(()=>checkOrigin(data.origin))
  .then(()=>originSA.deleteOriginById(data.origin.id))
  .then(()=>{return {deleted:true}},error=>error);
})

/**
 * @description Creates a new origin.
 * @returns {Promise}
 */
exports.createOrigin = functions.https.onCall((data,context)=>{
  return checkData(data)
  .then(()=>checkUser(data.user,"admin"))
  .then(()=>checkOrigin(data.origin))
  .then(()=>originSA.createOrigin(data.origin))
  .then(()=>{return {created:true}},error=>error);
})

//---------------ROUTE FUNCTIONS-----------------------------
exports.createRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkUser(data.user,"driver"))
  .then(()=>checkData(data.user.id))
  .then(()=>checkData(data.route))
  .then(()=>{data.route.driver=data.user.id; return routeSA.createRoute(data.route);}) //the driver must be the user who created the route.
  .then(()=>{return {created:true}},error=>error);
})

exports.searchRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkData(data.route.origin))
  .then(()=>checkData(data.route.destination))
  .then(()=>routeSA.searchRoutes(data.route.origin,data.route.destination))
  .then((routes)=>routes,(error)=>error);
})

exports.addToRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkData(data.address))
  .then(()=>checkUser(data.user))
  .then(()=>routeSA.addToRoute(data.user,data.route))
  .then(()=>{return {added:true}},(error => error));
});

/*---------------- PRIVATE Functions ---------------*/
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

/**
 * @description 
 * @param {*} origin 
 */
function checkOrigin(origin){
  return new Promise((resolve,reject)=>{
    if(origin == null) reject(ERROR.necessaryDataIsNull);
    else resolve();
  });
}//checkOrigin




/**
 * @description Checks if an user exists and the type if it is indicated.
 * @param {Objct} user 
 * @param {string = null} userType 
 */
function checkUser(user,userType = null){
  return personSA.checkUser(user,userType);
}//checkUser
