/**
 *@module dataAccess/personDAO
 */
const ERROR = require("../errors");
const db = require("./database.js");

/**
 * @description Gets the user from the database with that email.
 * @param {String} email Account's email.
 * @returns {Promise} Promise with the user data in the correct case or null if not, or an error.
 */
function getUser(email){
    return db.collection("persons").where("email", "==", email).get()
    .then((snapshot) => {
        if(snapshot.docs.length > 0) return snapshot.docs[0].data();
        else return null;},
    (err)=>{throw ERROR.server })
}//getUser

/**
 * @description Insert a new user in the database.
 * @param {Object} newUser  new user data
 * @returns {Promise} Promise that return null or an error.
 */
function insertUser(newUser){
    return db.collection("persons").add(newUser)
    .then(()=>{return null},
    (err)=>{throw ERROR.server});
}//insertUser





module.exports = {
    getUser: getUser,
    insertUser: insertUser
}
