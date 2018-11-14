
const db = require("./database");

function getUser(email){
    return db.ref("persons").equalTo(email,"email").once("value");
}

module.exports = {
    getUser: getUser
}