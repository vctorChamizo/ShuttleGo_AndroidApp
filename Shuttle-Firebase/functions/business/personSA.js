
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
 * @description register a new user in the database if not exists and meets the requirements
 * @param {Object} newUser the new user data
 * @returns {Promise}
 * @throws {Object} error
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
 * @description
 * @param {} user 
 * @param {*} userType
 * @throws
 */
function checkUser(user,userType){
    personSA.signIn(user.email,user.password).then((result)=>{
        if(result != null && (result.type == null || result.type != userType))
            throw ERROR.noPermissions;
    });
}//checkUser

/**
 * @description
 * @param {*} newUser 
 * @returns
 */
function checkRequirements(newUser){
    return checkType(newUser.type) &&
    newUser.name != null && newUser.name.length > 0 &&
    newUser.surname != null && newUser.surname.length > 0 &&
    newUser.password != null && newUser.password.length >= 3 &&
    newUser.number!= null && Number(newUser.number)!=NaN;
}//checkRequirements

/**
 * @description admin can't signUp
 * @param {*} type 
 * @returns
 */
function checkType(type){ 
    return type == "passenger" || type == "driver"; 
};//checkType

module.exports = {
    signIn:signIn,
    signUp:signUp
}
