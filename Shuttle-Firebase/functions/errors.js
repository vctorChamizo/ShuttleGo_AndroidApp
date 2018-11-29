
/**
 * @description Errors which will be sent to the clients
 */
const errors = {
    server:{error:"server"},                            //Internal error
    userAlreadyExists:{error:"userAlreadyExists"},      //The user you are trying to register already exists in the db.
    incorrectSignin:{error:"incorrectSignin"},          //The credentials are wrong.
    userDoesntExists:{error:"userDoesntExists"},        //The user you are trying to use doesn't exists.
    badRequestForm:{error:"badRequestForm"},            //The form requirements are not met.
    noPermissions:{error:"noPermissions"}               //The account you are using is not allowed to do this.
}

module.exports = errors;