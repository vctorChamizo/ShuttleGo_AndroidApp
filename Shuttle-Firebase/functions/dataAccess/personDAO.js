
const db = require("./database");

function getUser(email){
    return db.ref("persons").equalTo(email,"email").once("value")
    .then((data)=>{ return data.val()});
}

module.exports = {
    getUser: getUser
}

getUser("carlos@gmail.com");