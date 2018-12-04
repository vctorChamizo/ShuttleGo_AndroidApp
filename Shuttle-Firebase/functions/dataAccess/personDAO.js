const ERROR = require("../errors");
const db = require("./database.js");

/**
 * @description Check that the user is in the database
 * @param {String} email Account's email.
 * @returns {Object} User data in the correct case or null if not.
 */
function getUser(email){
    return db.collection("persons").where("email", "==", email).get()
    .then((snapshot) => {
        if(snapshot.docs.length > 0) return snapshot.docs[0].data();
        else return null;},
    (err)=>{throw ERROR.server })
}//getUser

/**
 * @description Insert a new user in the database
 * @param {Object} newUser 
 * @returns {Promise}
 * @throws {Object} Error
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
