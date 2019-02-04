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
            if(driver == null) throw ERROR.userDoesntExists;
            else if(driver.type != "driver") throw ERROR.noPermissions;
            else return routeDao.insertRoute(route);
        } )

}

function getRouteById(id){
    return routeDao.getRouteById(id)
    .then((route)=>{
        if(route == null) throw ERROR.routeDoesntExists;
        else return route;
    })
}

function getRoutesByPostCode(postCode){
    return routeDao.getRoutesByPostCode(postCode);
}

function addToRoute(user,route){
    return getRouteById(route)
    .then((route)=>{
        if (!route) throw ERROR.routeDoesntExists;
        else  if (route.passengers.length >= route.max) throw ERROR.routeSoldOut;
        else return routeDao.addToRoute(user,route);
    })
}


function checkRequirements(route){
    return new Promise((resolve,reject)=>{
        if(route == null || route.driver == null || route.origin == null ||
           route.max == null || Number(route.max) == NaN || Number(route.max)<0) reject(ERROR.badRequestForm);
        else resolve();
    });
}
module.exports = {
    createRoute:createRoute,
    getRouteById:getRouteById,
    getRoutesByPostCode:getRoutesByPostCode,
    addToRoute:addToRoute
}