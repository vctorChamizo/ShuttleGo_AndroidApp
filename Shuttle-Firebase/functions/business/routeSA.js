const ERROR = require("../errors");
const routeDao = require("../dataAccess/routeDAO");
const originDao = require("../dataAccess/originDAO");
const personDao = require("../dataAccess/personDAO");

function createRoute(route){
   return checkRequirements(route)
        .then(()=>originDao.getOriginById(route.origin))
        .then((origin)=>{
            if(origin == null) throw ERROR.originDoesntExists;
            else return personDao.getUserById(route.driver);
        })
        .then((driver)=>{
            route.max=Number(route.max);
            route.passengersNumber = 0;
            if(driver == null) throw ERROR.userDoesntExists;
            else if(driver.type != "driver") throw ERROR.noPermissions;
            else return routeDao.insertRoute(route);
        } )

}

function getRouteById(id){
    let routeFin;

    return routeDao.getRouteById(id)
    .then((route)=>{
        if(route == null) throw ERROR.routeDoesntExists;
        else{
            routeFin = route;
            return personDao.getUserById(route.driver);
        }
    })
    .then((driver)=>{
        routeFin.driverName = driver.name;
        routeFin.driverSurname = driver.surname;
        routeFin.driverNumber = driver.number;
        routeFin.driverEmail = driver.email;
        return originDao.getOriginById(routeFin.origin);  
    })
    .then((origin)=>{
        routeFin.origin = origin.name;
        return routeFin;
    })
}

function searchRoutes(origin,destination){
    destination = Number(destination);
    return routeDao.getRoutesByOriginAndDestination(origin,destination);
}

function addToRoute(user,route,address,coordinates){
    let passenger;
    return personDao.getUser(user.email)
    .then((result)=>{
        if (!result) throw ERROR.userDoesntExists;
        else{
            passenger = result;
            return routeDao.getRouteById(route.id);
        }
    })
    .then((route)=>{
        if (!route) throw ERROR.routeDoesntExists;
        else  if (route.passengers.length >= route.max) throw ERROR.routeSoldOut;
        else if (route.passengers.indexOf(passenger.id) != -1) throw ERROR.userAlreadyAdded;
        else return routeDao.addToRoute(passenger.id,route,address,coordinates);
    })
}

function removePassengerFromRoute(passenger,route){
    let fullRoute;
    return routeDao.getRouteById(route.id)
    .then((route)=>{
        if(route == null) throw ERROR.routeDoesntExists;
        else{
            fullRoute = route;
            return personDao.getUser(passenger.email);
        }
    })
    .then((user)=>{
        if(user == null) throw ERROR.userDoesntExists;
        else if(!fullRoute.passengers.some(pass=>pass==user.id)) throw ERROR.userNotAdded; 
        return routeDao.removePassengerFromRoute(user.id,fullRoute.id);
    })
}

function removeRoute(driver,route){
    let driverId;
    return personDao.getUser(driver.email)
    .then((driver)=>{
        if(driver == null) throw ERROR.userDoesntExists;
        else {
            driverId = driver.id;
            return routeDao.getRouteById(route.id);
        }
    })
    .then((route)=>{
        if(route.driver != driverId) throw ERROR.noPermissions;
        else if (route.passengersNumber>0) throw ERROR.routeNotEmpty;
        else return routeDao.removeRoute(route.id);
    })
}

//-----------------------------------Private functions-------------------------------------------
function checkRequirements(route){
    return new Promise((resolve,reject)=>{
        if(route == null || route.driver == null || route.origin == null || route.destination == null ||
           route.max == null || Number(route.max) == NaN || Number(route.max)<0) reject(ERROR.badRequestForm);
        else resolve();
    });
}

module.exports = {
    createRoute:createRoute,
    getRouteById:getRouteById,
    searchRoutes:searchRoutes,
    addToRoute:addToRoute,
    removePassengerFromRoute:removePassengerFromRoute,
    removeRoute:removeRoute
}