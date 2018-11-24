
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
};//signIn




/**
 * @description register a new user in the database if not exists and meets the requirements
 * @param {Object} newUser 
 * @returns {Promise}
 * @throws {Object} error
 */
function signUp(newUser){
    
   // if (!checkRequirements(newUser))
   //     throw ERROR.badRequestForm;
    
   // else
        return personDAO
        .getUser(newUser.email)
        .then(result => {
            if (result != null) throw ERROR.userAlreadyExists;
            else  return personDAO.insertUser(newUser);     
        });
};

function checkRequirements(newUser){
    return checkType(newUser.type) &&
    newUser.name != null && newUser.length > 0 &&
    newUser.surname != null && newUser.surname.length > 0 &&
    newUser.password != null && newUser.password.length >= 3 &&
    newUser.number!= null && typeof newUser.number == 'number';
}

function checkType(type){
    return type == "passenger" || type == "driver"; //admin can't signUp
};//signup




module.exports = {
    signIn:signIn,
    signUp:signUp
}
