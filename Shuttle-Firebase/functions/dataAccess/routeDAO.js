const ERROR = require("../errors");
const db = require("./database.js");

function insertRoute(newData){
    if(newData.id != "undefined") delete newData.id;
    return db.collection("routes").add(newData)
    .then(()=>null,error=>{throw ERROR.server});
}

function getRouteById(id){
    let route;
    
    return db.collection("routes").doc(id).get()
    .then((snapshot)=>{
        if(!snapshot.exists)
            return null;
        else{
            route = snapshot.data();
            route.id = snapshot.id;
            return getPassengers(id);
        };
    })
    .then((passengers)=>{
        route.passengers = passengers;
        return route;
    },error=>{throw ERROR.server});
}

function getPassengers(route){
    return db.collection("check-in").where("route","==",route).get()
    .then((snapshot)=>{
        return snapshot.docs.map(element=>element.data().passenger);
    },error => {throw ERROR.server});
}
function deleteRouteById(id){
    return db.collection("routes").doc(id).delete()
    .then(()=>null,error=>{throw ERROR.server});
}

function getRoutesByPostCode(postCode){
    return db.collection("routes").where("postCode","==",postCode).get()
    .then((snapshot) => {
        if(snapshot.docs.length > 0) return snapshot.docs.map(element=>{return element.data()});
        else return [];},
    (err)=>{throw ERROR.server })
}

function addToRoute(user,route){
    return db.collection("check-in")
    .add({
        passenger:user,
        route:route.id,
        order:route.passengers.length
    })
    .then(()=>null,(err)=>{throw ERROR.server });
}

module.exports = {
    deleteRouteById:deleteRouteById,
    insertRoute:insertRoute,
    getRouteById:getRouteById,
    getRoutesByPostCode:getRoutesByPostCode,
    addToRoute:addToRoute,
    getPassengers:getPassengers
}