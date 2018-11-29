const ERROR = require("../errors");
const db = require("./database.js");

/**
 * @description Get all route origins from the database
 * @returns {Promise} a promise that return a list of strings
 */
function getAllOrigins(){
    return db.collection("origins").get()
    .then((snapshot)=>{
        
        let origins = [];
        snapshot.docs.forEach((doc)=>{
            origins.push(doc.data().name);
        })
        return origins;
        
    },(error)=>{throw ERROR.server});
}

module.exports={
    getAllOrigins:getAllOrigins
}