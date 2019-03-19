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
      - GETORIGINBYNAME: getOriginByName({origin:{name:"nombre"}}, {headers: {Authorization: 'Bearer $token'}});
      - GETALLORIGINS: getAllOrigins({}, {headers: {Authorization: 'Bearer $token'}});
      - GETORIGINBYID: getOriginById({origin:{id:"nTREdQ19BRPRACy5JBiN"}}, {headers: {Authorization: 'Bearer $token'}});
      - DELETEORIGIN: deleteOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{id:"nTREdQ19BRPRACy5JBiN"}}, {headers: {Authorization: 'Bearer $token'}});
      - CREATEORIGIN: createOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{name:"Barajas T5",coordAlt:123,coordLong:234}}, {headers: {Authorization: 'Bearer $token'}});
      - MODIFYORIGIN: modifyOrigin({user:{email:"admin@gmail.com",password:"123"},origin:{id:"...",name:"Barajas T6"}}, {headers: {Authorization: 'Bearer $token'}});
    
    ** Route **
      - GETROUTESBYUSER: getRoutesByUser({user:{email:"driv@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
      - GETROUTEBYID: getRouteById({route:{id:"..."}}, {headers: {Authorization: 'Bearer $token'}});
      - CREATEROUTE: createRoute({user:{email:"driv@gmail.com",password:"123"},route:{max:2,origin:"i9BQCi6ovzC1pdBGoRYm(elOrigenDelId)",destination:"1234(codigoPostal)"}}, {headers: {Authorization: 'Bearer $token'}});
      - SEARCHROUTE: searchRoute({route:{destination:"28008",origin:"loquesea"}}, {headers: {Authorization: 'Bearer $token'}});})
      - ADDTOROUTE: addToRoute({address:"passenger address",coordinates:"",route:{id:"7ptW7eHRPqtoaHL4SWae"},user:{email:"pass@gmail.com",password:"123"}}, {headers: {Authorization: 'Bearer $token'}});
      FOR DRIVER USE THIS: - REMOVEROUTE: removeRoute({user:{email:"driv@gmail.com",password:"123"},route:{id:"N6ObwG7HYLpelukKi5qL"}}, {headers: {Authorization: 'Bearer $token'}});
      FOR PASSENGER USE THIS: -REMOVEPASSENGERFROMROUTE: removePassengerFromRoute({user:{email:"pass@gmail.com",password:"123"},route:{id:"N6ObwG7HYLpelukKi5qL"}}, {headers: {Authorization: 'Bearer $token'}});
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
 * @returns {Promise} A promise that return a list of origins.
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
  .then((modified)=>{return {modified:modified}},error=>error);
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
  .then(()=>checkData(data.origin))
  .then(()=>checkData(data.origin.coordAlt))
  .then(()=>checkData(data.origin.coordLong))
  .then(()=>{
    data.origin.coordinates = String(data.origin.coordAlt)+","+String(data.origin.coordLong);
    delete data.origin.coordAlt;
    delete data.origin.coordLong;
    return originSA.createOrigin(data.origin);
  })
  .then((id)=>{return {id:id}},error=>error);
})

exports.getOriginByName = functions.https.onCall((data,context)=>{
  return checkData(data)
  .then(()=>checkData(data.origin))
  .then(()=>checkData(data.origin.name))
  .then(()=>originSA.getOriginByName(data.origin.name))
  .then((origin)=>origin,error=>error);
})
/*---------------- ROUTE Functions ---------------*/
/**
 * 
 */
exports.createRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkUser(data.user,"driver"))
  .then((fullUser)=>{data.route.driver=fullUser.id; return routeSA.createRoute(data.route);}) //the driver must be the user who created the route.
  .then((id)=>{return {id:id}},error=>error);
})

/**
 * 
 */
exports.searchRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkData(data.route.origin))
  .then(()=>checkData(data.route.destination))
  .then(()=>routeSA.searchRoutes(data.route.origin,data.route.destination))
  .then((routes)=>{return {routes:routes}},(error)=>error);
})

/**
 * 
 */
exports.addToRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkData(data.address))
  .then(()=>checkUser(data.user))
  .then(()=>checkData(data.coordinates))
  .then(()=>routeSA.addToRoute(data.user,data.route,data.address,data.coordinates))
  .then(()=>{return {added:true}},(error => error));
});

exports.getRouteById = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkData(data.route.id))
  .then(()=>routeSA.getRouteById(data.route.id))
  .then((route)=>route,error=>error);
})


exports.removePassengerFromRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkUser(data.user,"passenger"))
  .then(()=>routeSA.removePassengerFromRoute(data.user,data.route))
  .then(()=>{return {removed:true}},error=>error);
})

exports.removeRoute = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkData(data.route))
  .then(()=>checkUser(data.user,"driver"))
  .then(()=>routeSA.removeRoute(data.user,data.route))
  .then(()=>{return {removed:true}},error=>error);
})


exports.getRoutesByUser = functions.https.onCall((data,conext)=>{
  return checkData(data)
  .then(()=>checkUser(data.user))
  .then(()=>routeSA.getRoutesByUser(data.user))
  .then((routes)=>{return {routes:routes}},error=>error);
})

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
