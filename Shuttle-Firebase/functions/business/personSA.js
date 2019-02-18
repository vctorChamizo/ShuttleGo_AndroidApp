/**
 * @module business/personSA
 */
const ERROR = require("../errors")
const personDAO = require("../dataAccess/personDAO");

/**
 * @description Check that the user exists and that the password entered is correct.
 * @param {String} email Account's email.
 * @param {String} password Account's password.
 * @return {Promise} A promise with the user data if email and password are correct.
 * @throws {Object} Error
 */
function signIn(email, password) {
    return personDAO
    .getUser(email)
    .then(user => {
        if (user == null) throw ERROR.userDoesntExists;
        else if (user.password == password) return user;
        else throw ERROR.incorrectSignin;
    });
}//signIn

/**
 * @description Register a new user in the database if not exists and meets the requirements.
 * @param {Object} newUser The new user data.
 * @returns {Promise} A promise that returns null or error.
 */
function signUp(newUser){
    return new Promise((resolve,reject)=>{
    
    if (!checkRequirements(newUser)) reject(ERROR.badRequestForm);
    else resolve();
    }).then(()=>{
        return personDAO.getUser(newUser.email);
    }).then(result => {
            if (result != null) throw ERROR.userAlreadyExists;
            else{
                newUser.number=Number(newUser.number);
                 return personDAO.insertUser(newUser); 
            }    
    });
}//signUp

/**
 * @description Check the type and if exists the user. (It should be called when getting or editing risk information )
 * @param {Object} user User data.
 * @param {string = null} userType The type of the user that can do that action.
 * @returns {Promise} Promise that returns null or error.
 */
function checkUser(user,userType = null){

    return new Promise((resolve,reject)=>{
        if(user==null)reject(ERROR.necessaryDataIsNull);
        else resolve();
    })
    .then(()=>signIn(user.email,user.password))
    .then((result)=>{
        if(result == null ||(userType != null &&( result.type == null || result.type != userType)))
            throw ERROR.noPermissions;
        else return result;
    });
}//checkUser







//--------------PRIVATE METHODS----------------
/**
 * @description Check sign up requirements.
 * @param {Object} newUser New user data. 
 * @returns {Boolean}
 */
function checkRequirements(newUser){
    return checkType(newUser.type) &&
    newUser.name != null && newUser.name.length > 0 &&
    newUser.surname != null && newUser.surname.length > 0 &&
    newUser.password != null && newUser.password.length >= 3 &&
    newUser.number!= null && Number(newUser.number)!=NaN;
}//checkRequirements

/**
 * @description Check that you are signing up with the correct type
 * @param {String} type 
 * @returns
 */
function checkType(type){ 
    return type == "passenger" || type == "driver"; 
};//checkType




module.exports = {
    signIn:signIn,
    signUp:signUp,
    checkUser:checkUser
}
