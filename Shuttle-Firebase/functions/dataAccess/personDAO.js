
const db = require("./database.js");


function getUser(email){

    return db.collection("persons").where("email", "==", email)
    .get()
    .then((snapshot) => {
        if(snapshot.docs.length > 0) return snapshot.docs[0].data();
        else return null;
    },
    (err) => {  return new Error("databaseError"); })
}//getUser


module.exports = {

    getUser: getUser
}
