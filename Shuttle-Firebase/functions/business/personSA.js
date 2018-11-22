
const personDAO = require("../dataAccess/personDAO");


/**
 * Check that the user exists and that the password entered is correct.
 * @param {String} email Account's email.
 * @param {String} password Account's password.
 * @return {Object} User data in the correct case and null in the wrong case.
 */
function signIn(email, password) {

    return personDAO
    .getUser(email)
    .then(user => {
        if (user && user.password == password) return user;
        else return null;
    });
};//signIn


module.exports = {
    signIn:signIn
}
