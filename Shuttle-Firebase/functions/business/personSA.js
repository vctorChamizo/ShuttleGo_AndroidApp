
const personDAO = require("../dataAccess/personDAO");

function signIn(email,password){
    
    return personDAO.getUser(email).then(user=>{
        return user && user.password == password});
};

module.exports = {
    signIn:signIn
}