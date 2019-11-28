    /**
     * 
     * @module service_application
     * 
     * @description Apply the application logic about person functions.
     * 
     */

    const ERROR = require("../errors")
    const personDAO = require("../data_access/person_DAO");

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Check that the user exists and that the password entered is correct.
     * 
     * @param {String} email Account's email.
     * @param {String} password Account's password.
     * 
     * @return {Promise} A promise with the user data if email and password are correct.
     * 
     * @throws {Object} Error.
     */
    function signIn(email, password) {

        return personDAO.getUser(email)
        .then(user => {

            if (user == null) throw ERROR.userDoesntExists;
            else if (user.password == password) return user;
            else throw ERROR.incorrectSignin;
        });
    };

    /**
     * @description Register a new user in the database if not exists and meets the requirements.
     * 
     * @param {Object} newUser The new user data.
     * 
     * @returns {Promise} A promise that returns null or error.
     * 
     * @throws {Object} Error.
     */
    function signUp(newUser){

        return new Promise((resolve, reject) => {
        
            if (!checkRequirements(newUser)) reject(ERROR.badRequestForm);
            else resolve();
        })
        .then(() => { return personDAO.getUser(newUser.email); })
        .then(result => {

            if (result != null) throw ERROR.userAlreadyExists;
            else {

                newUser.number = Number(newUser.number);

                return personDAO.insertUser(newUser); 
            }    
        });
    };

    /**
     * @description Check the type and if exists the user. (It should be called when getting or editing risk information )
     * 
     * @param {Object} user User data.
     * @param {string = null} userType The type of the user that can do that action.
     * 
     * @returns {Promise} Promise that returns null or error.
     * 
     * @throws {Object} Error.
     */
    function checkUser(user,userType = null) {

        return new Promise((resolve,reject) => {

            if(user == null)reject(ERROR.necessaryDataIsNull);
            else resolve();
        })
        .then(() => signIn(user.email, user.password))
        .then((result) => {

            if(result == null ||(userType != null && ( result.type == null || result.type != userType))) throw ERROR.noPermissions;
            else return result;
        });
    };

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */
    /* Private function of module */

    /**
     * @description Check sign up requirements.
     * 
     * @param {Object} newUser New user data. 
     * 
     * @returns {Boolean} A conformation about data are correct or not.
     */
    function checkRequirements(newUser){

        return checkType(newUser.type) &&
        newUser.name != null && newUser.name.length > 0 &&
        newUser.surname != null && newUser.surname.length > 0 &&
        newUser.password != null && newUser.password.length >= 3 &&
        newUser.number != null && Number(newUser.number) != NaN;
    };

    /**
     * @description Check that you are signing up with the correct type
     * 
     * @param {String} type The tye of user [passenger or driver].
     * 
     * @returns {Boolean} A conformation about data are correct or not.
     */
    function checkType(type){ return type == "passenger" || type == "driver"; };

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    module.exports = {
        
        signIn:signIn,
        signUp:signUp,
        checkUser:checkUser
    }
