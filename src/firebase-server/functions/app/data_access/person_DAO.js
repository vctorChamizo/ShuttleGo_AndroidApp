    /**
     * 
     * @module data_access
     * 
     * @description Database manager and person data exchange.
     * 
     */

    const ERROR = require("../errors");
    const db = require("./database.js");
    
    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Gets the user from the database with that email.
     * 
     * @param {String} email Account's email.
     * 
     * @returns {Promise} Promise with the user data in the correct case or null if not, or an error.
     */
    function getUser(email) {

        return db.collection("persons").where("email", "==", email).get()
        .then((snapshot) => {

            if(snapshot.docs.length > 0) {

                let result = snapshot.docs[0].data();

                result.id = snapshot.docs[0].id;

                return result;
            }
            else return null;

        }, error => { throw ERROR.server });
    }

    /**
     * @description Gets the user from the database with that email.
     * 
     * @param {String} id User´s id.
     * 
     * @returns {Promise} A promise with user.
     */
    function getUserById(id) { 

        return db.collection("persons").doc(id).get()
        .then((snapshot) => {

            if(!snapshot.exists) return null;
            else {

                let user = snapshot.data();

                user.id = snapshot.id;

                return user;
            }

        }, error => { throw ERROR.server });
    }

    /**
     * @description Insert a new user in the database.
     * 
     * @param {Object} newUser The data user.
     * 
     * @returns {Promise} Promise that return null or an error.
     */
    function insertUser(newUser) { 

        return db.collection("persons").add(newUser)
        .then(() => { return null}, error => { throw ERROR.server });
    }
    
    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    module.exports = {

        getUser: getUser,
        insertUser: insertUser,
        getUserById:getUserById
    }
