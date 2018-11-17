
const db = require("./database.js");

function getUser(email){
    //return db.ref("persons").equalTo(email,"email").once("value")
    return db.ref("persons").once("value").then((data)=>{
        return data.val();

    },(err)=>{
        console.log(err);
    })
}

module.exports = {
    getUser: getUser
}