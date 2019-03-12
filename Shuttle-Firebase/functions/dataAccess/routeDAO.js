const ERROR = require("../errors");
const db = require("./database.js");

function insertRoute(newData){
    if(newData.id != "undefined") delete newData.id;
    return db.collection("routes").add(newData)
    .then((result)=>result.id,error=>{throw ERROR.server});
}

function getRouteById(id){
    let route;
    
    return db.collection("routes").doc(id).get()
    .then((snapshot)=>{
        console.log(snapshot.exists)
        if(!snapshot.exists)
            return null;
        else{
            route = snapshot.data();
            route.id = snapshot.id;
            return getPassengers(id);
        };
    })
    .then((passengers)=>{
        if(!passengers)
            return null;
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

function getRoutesByOriginAndDestination(origin,destination){
    return db.collection("routes").where("destination","==",destination).where("origin","==",origin).get()
    .then((snapshot) => {
        if(snapshot.docs.length > 0) 
            return snapshot.docs.map(element=>{
                let fin = element.data();
                fin.id = element.id;
                return fin;
                }
            );
        else return [];},
    (err)=>{throw ERROR.server })
}

function addToRoute(user,route,address,coordinates){

    let oldPassengersNumber;

    return db.collection("routes").doc(route.id).get()
    .then((snapshot)=>{
        oldPassengersNumber = snapshot.data().passengersNumber
    })
    .then(
        db.collection("check-in").add({
            passenger:user,
            route:route.id,
            order:route.passengers.length,
            address:address,
            coordinates:coordinates
    }))
    .then(()=>db.collection("routes").doc(route.id).update({passengersNumber:oldPassengersNumber+1}))
    .then(()=>null,(err)=>{console.log(err);throw ERROR.server });
}

function removePassengerFromRoute(passenger,route){

}
module.exports = {
    deleteRouteById:deleteRouteById,
    insertRoute:insertRoute,
    getRouteById:getRouteById,
    getRoutesByOriginAndDestination:getRoutesByOriginAndDestination,
    addToRoute:addToRoute,
    getPassengers:getPassengers,
    removePassengerFromRoute:removePassengerFromRoute,
    removeRoute:removeRoute
}