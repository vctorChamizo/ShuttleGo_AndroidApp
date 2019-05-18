    /**
     * 
     * @module service_application
     * 
     * @description Apply the application logic about route functions.
     * 
     */

    const ERROR = require("../errors");
    const routeDao = require("../data_access/route_DAO");
    const originDao = require("../data_access/origin_DAO");
    const personDao = require("../data_access/person_DAO");

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Create a new route.
     * 
     * @param {Object} route The new route.
     * 
     * @returns {Promise} A promise with a new id route.
     * 
     * @throws {Object} Error.
     */
    function createRoute(route) {

        return checkRequirements(route)
        .then(() => originDao.getOriginById(route.origin))
        .then((origin) => {

            if(origin == null) throw ERROR.originDoesntExists;
            else return personDao.getUserById(route.driver);
        })
        .then((driver) => {

            route.max = Number(route.max);
            route.passengersNumber = 0;

            if(driver == null) throw ERROR.userDoesntExists;
            else if(driver.type != "driver") throw ERROR.noPermissions;
            else return routeDao.insertRoute(route);
        });
    };

    /**
     * @description Get the route data.
     * 
     * @param {String} id The route id.
     * @param {Object} uer The user that belong the route.
     * 
     * @returns {Promise} A promise that returns the route.
     * 
     * @throws {Object} Error.
     */
    function getRouteById(id, user) {

        let routeFin;

        return routeDao.getRouteById(id)
        .then((route) => {

            if(route == null) throw ERROR.routeDoesntExists;
            else {

                routeFin = route;
                
                return personDao.getUserById(route.driver);
            }
        })
        .then((driver) => {

            routeFin.driverName = driver.name;
            routeFin.driverSurname = driver.surname;
            routeFin.driverNumber = driver.number;
            routeFin.driverEmail = driver.email;

            return originDao.getOriginById(routeFin.origin);  
        })
        .then((origin) => {

            routeFin.origin = origin.name;

            if(user == null) return routeFin;
            else {

                return personDao.getUser(user.email)
                .then((userFull) => {

                    if(userFull == null) throw ERROR.userDoesntExists;

                    return routeDao.getDestination(routeFin.id,userFull.id);
                })
                .then((destination) => {

                    if(destination == null) throw ERROR.userNotAdded;

                    routeFin.destinationName = destination;

                    return routeFin;
                });
            }
        });
    };

    /**
     * @description Look for the route match with origin and destination.
     * 
     * @param {String} origin The origin of route.
     * @param {String} destination The destination of route.
     * 
     * @returns {Promise} A promise with a list of coincides routes.
     * 
     * @throws {Object} Error.
     */
    function searchRoutes(origin, destination) {

        destination = Number(destination);

        return routeDao.getRoutesByOriginAndDestination(origin, destination);
    }

    /**
     * @description Add a user to a exists route.
     * 
     * @param {Object} user The data user.
     * @param {Object} route The data route.
     * @param {Object} address The address to end route for user.
     * @param {Object} coordinates The coordinates os address.
     * 
     * @returns {Promise} A promise with confirmation.
     * 
     * @throws {Object} Error.
     */
    function addToRoute(user, route, address, coordinates) {

        let passenger;

        return personDao.getUser(user.email)
        .then((result) => {

            if (!result) throw ERROR.userDoesntExists;
            else {

                passenger = result;

                return routeDao.getRouteById(route.id);
            }
        })
        .then((route) => {

            if (!route) throw ERROR.routeDoesntExists;
            else  if (route.passengers.length >= route.max) throw ERROR.routeSoldOut;
            else if (route.passengers.indexOf(passenger.id) != -1) throw ERROR.userAlreadyAdded;
            else return routeDao.addToRoute(passenger.id, route, address, coordinates);
        });
    };

    /**
     * @description Delete a user from the route.
     * 
     * @param {Object} passenger The data user.
     * @param {Object} route The data user.
     * 
     * @returns {Promise} A promise with confirmation.
     * 
     * @throws {Object} Error.
     */
    function removePassengerFromRoute(passenger, route) {

        let fullRoute;

        return routeDao.getRouteById(route.id)
        .then((route) => {

            if(route == null) throw ERROR.routeDoesntExists;
            else {

                fullRoute = route;

                return personDao.getUser(passenger.email);
            }
        })
        .then((user) => {

            if(user == null) throw ERROR.userDoesntExists;
            else if(!fullRoute.passengers.some(pass => pass == user.id)) throw ERROR.userNotAdded; 
            return routeDao.removePassengerFromRoute(user.id,fullRoute.id);
        });
    };

    /**
     * @description Delete a route from database.
     * 
     * @param {Object} driver The data user.
     * @param {Object} route The data route.
     * 
     * @returns {Promise} A promise with confirmation.
     * 
     * @throws {Object} Error.
     */
    function removeRoute(driver, route) {

        let driverId;

        return personDao.getUser(driver.email)
        .then((driver) => {

            if(driver == null) throw ERROR.userDoesntExists;
            else {

                driverId = driver.id;

                return routeDao.getRouteById(route.id);
            }
        })
        .then((routeFull) => {

            if (routeFull == null) throw ERROR.routeDoesntExists;
            else if(routeFull.driver != driverId) throw ERROR.noPermissions;
            else if (routeFull.passengersNumber > 0) throw ERROR.routeNotEmpty;
            else return routeDao.deleteRouteById(routeFull.id);
        });
    };

    /**
     * @description Get the routes that belong a user.
     * 
     * @param {Object} user The data user.
     * 
     * @returns {Promise} A promise with a list os routes.
     * 
     * @throws {Object} Error.
     */
    function getRoutesByUser(user) {

        return personDao.getUser(user.email)
        .then((userFull) => {

            if(userFull == null) throw ERROR.userDoesntExists;
            else if(userFull.type == "driver") return routeDao.getRoutesByDriver(userFull.id);
            else if(userFull.type == "passenger") return routeDao.getRoutesByPassenger(userFull.id);
            else throw ERROR.noPermissions;
        });
    };

    /**
     * @description Get the coordinates belong routes.
     * 
     * @param {Object} route The data route.
     * @param {Object} driver the data user.
     * 
     * @returns {Promise} A promise with list of route points.
     * 
     * @throws {Object} Error.
     */
    function getRoutePoints(route, driver) {

        let driverFull;
        let routeFull;
        let waypoints;

        return personDao.getUser(driver.email)
        .then((data) => {

            if(data == null) throw ERROR.userDoesntExists;

            driverFull = data;

            return routeDao.getRouteById(route.id);
        })
        .then((data) => {

            if(data == null) throw ERROR.routeDoesntExists;

            routeFull = data;

            if(driverFull.id == routeFull.id) throw ERROR.noPermissions;
            else return routeDao.getRoutePoints(routeFull.id);
        })
        .then((data2) => {

            waypoints = data2;

            return originDao.getOriginById(routeFull.origin);
        })
        .then((origin) => { return {waypoints: waypoints, origin: origin} });
    };

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */
    /* Private function of module */

    /**
     * @description Check the data os a route.
     * 
     * @param {Object} route The data route.
     * 
     * @returns {Promise} A promise with confirmation.
     * 
     * @throws {Object} Error.
     */
    function checkRequirements(route) {

        return new Promise((resolve,reject) => {

            if(route == null || route.driver == null || route.origin == null || route.destination == null ||
            route.max == null || Number(route.max) == NaN || Number(route.max)<0) reject(ERROR.badRequestForm);
            else resolve();
        });
    };

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    module.exports = {

        createRoute:createRoute,
        getRouteById:getRouteById,
        searchRoutes:searchRoutes,
        addToRoute:addToRoute,
        removePassengerFromRoute:removePassengerFromRoute,
        removeRoute:removeRoute,
        getRoutesByUser:getRoutesByUser,
        getRoutePoints:getRoutePoints
    }
    