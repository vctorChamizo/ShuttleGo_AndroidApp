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
    .then(()=>null,(err)=>{throw ERROR.server });
}

function removePassengerFromRoute(passengerId,routeId){
    return db.collection("check-in").where("passenger","==",passengerId).where("route","==",routeId).get()
    .then(snapshot=>db.collection("check-in").doc(snapshot.docs[0].id).delete())
    .then(()=>null,(error)=>{throw ERROR.server});
}

function deleteRouteById(routeId){
    return db.collection("routes").doc(routeId).delete()
    .then(()=>db.collection("check-in").where("route","==",routeId).get())
    .then((snapshot)=>{
        let promises = [];
        snapshot.docs.forEach(doc=>{console.log(doc);promises.push(db.collection("check-in").doc(doc.id).delete())});
        return Promise.all(promises);
    })
    .then(()=>null,(error)=>{throw ERROR.server});
}

function getRoutesByDriver(DriverId){
    return db.collection("routes").where("driver","==",DriverId).get()
    .then((snapshot)=>snapshot.docs.map(element=>{
        let route = element.data();
        route.id = element.id;
        return route;
    }))
    .then((routes)=>routes,error =>{throw ERROR.server});
}

function getRoutesByPassenger(passengerId){
    return db.collection("check-in").where("passenger","==",passengerId).get()
    .then((snapshot)=>{
        let routeIds = snapshot.docs.map(element=>element.data().route);
        let promises = [];
        routeIds.forEach(id=> promises.push(getRouteById(id)));
        return Promise.all(promises);
    })
    .then((routes)=>routes,error =>{throw ERROR.server});
}

function getRoutePoints(routeId){
    return db.collection("check-in").where("route","==",routeId).get()
    .then((snapshot)=>{
        return snapshot.docs.map(element=>element.data());
    })
}

module.exports = {
    deleteRouteById:deleteRouteById,
    insertRoute:insertRoute,
    getRouteById:getRouteById,
    getRoutesByOriginAndDestination:getRoutesByOriginAndDestination,
    addToRoute:addToRoute,
    getPassengers:getPassengers,
    removePassengerFromRoute:removePassengerFromRoute,
    getRoutesByDriver:getRoutesByDriver,
    getRoutesByPassenger:getRoutesByPassenger,
    getRoutePoints:getRoutePoints
}