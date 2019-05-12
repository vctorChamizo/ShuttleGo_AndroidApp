    /**
     * 
     * @module data_access
     * 
     * @description Database manager and route data exchange.
     * 
     */

    const ERROR = require("../errors");
    const db = require("./database.js");
    
    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Create a nre route in database.
     * 
     * @param {Object} newData The new route data.
     * 
     * @returns {Promise} A promise with confirmation the new created route.
     */
    function insertRoute(newData) {

        if(newData.id != "undefined") delete newData.id;

        return db.collection("routes").add(newData)
        .then((result) => result.id, error => { throw ERROR.server });
    }

    /**
     * @description Get a route.
     * 
     * @param {String} id The route id.
     * 
     * @returns {Promise} A promise with route data.
     */
    function getRouteById(id) {

        let route;
        
        return db.collection("routes").doc(id).get()
        .then((snapshot) => {

            if(!snapshot.exists) return null;
            else {

                route = snapshot.data();
                route.id = snapshot.id;

                return getPassengers(id);
            }
        })
        .then((passengers) => {

            if(!passengers) return null;

            route.passengers = passengers;

            return route;

        }, error => { throw ERROR.server });
    }

    /**
     * @description Get a list of passengers of route.
     * 
     * @param {String} route The route id
     * 
     * @returns {Promise} A promise with a list of passengers.
     */
    function getPassengers(route) {

        return db.collection("check-in").where("route","==",route).get()
        .then((snapshot) => { return snapshot.docs.map(element=>element.data().passenger) }, error => { throw ERROR.server });
    }

    /**
     * @description Get a route to match with the parameters.
     * 
     * @param {String} origin The name of origin.
     * @param {String} destination The name of destination.
     * 
     * @returns {Promise} A promise with the route.
     */
    function getRoutesByOriginAndDestination(origin, destination) {

        return db.collection("routes").where("destination", "==", destination).where("origin", "==", origin).get()
        .then((snapshot) => {

            if(snapshot.docs.length > 0) {

                return snapshot.docs.map(element => {

                    let fin = element.data();
                    fin.id = element.id;

                    return fin;
                });
            } 
            else return [];
        }, error => { throw ERROR.server })
    }

    /**
     * @description Add a new route to database.
     * 
     * @param {Object} user The user data.
     * @param {Route} route The route data.
     * @param {String} address The name of address.
     * @param {Object} coordinates The coordinates of address.
     * 
     * @returns {Promise} A promise with confirmation the new added route.
     */
    function addToRoute(user, route, address, coordinates){

        let oldPassengersNumber;

        return db.collection("routes").doc(route.id).get()
        .then((snapshot) => { oldPassengersNumber = snapshot.data().passengersNumber })
        .then(

            db.collection("check-in").add({

                passenger:user,
                route:route.id,
                order:route.passengers.length,
                address:address,
                coordinates:coordinates
            })
        )
        .then(() => db.collection("routes").doc(route.id).update({passengersNumber: oldPassengersNumber + 1}))
        .then(() => null, error =>{ throw ERROR.server });
    }

    /**
     * @description Delete a user to the list of route.
     * 
     * @param {String} passengerId The id of user.
     * @param {String} routeId The id of route.
     * 
     * @returns {Promise} A promise with confirmation the deleted passenger of route.
     */
    function removePassengerFromRoute(passengerId, routeId) {

        return db.collection("check-in").where("passenger", "==", passengerId).where("route", "==", routeId).get()
        .then(snapshot => db.collection("check-in").doc(snapshot.docs[0].id).delete())
        .then(() => db.collection("routes").doc(routeId).get())
        .then((snapshot) => db.collection("routes").doc(routeId).update({passengersNumber: snapshot.data().passengersNumber-1}))
        .then(() => null, error => { throw ERROR.server });
    }

    /**
     * @description Remove a route of databse
     * 
     * @param {String} routeId The id of route.
     * 
     * @returns {Promise} A promise withe confirmation removed route.
     */
    function deleteRouteById(routeId) {

        return db.collection("routes").doc(routeId).delete()
        .then(() => db.collection("check-in").where("route", "==", routeId).get())
        .then((snapshot) => {

            let promises = [];

            snapshot.docs.forEach(doc => { promises.push(db.collection("check-in").doc(doc.id).delete()) });
            
            return Promise.all(promises);
        })
        .then(()=> null, error => { throw ERROR.server });
    }

    /**
     * @description Get a list of route´s driver.
     * 
     * @param {String} DriverId the id of driver.
     * 
     * @returns {Promise} A list with route´s driver. 
     */
    function getRoutesByDriver(DriverId) {

        return db.collection("routes").where("driver", "==", DriverId).get()
        .then((snapshot) => snapshot.docs.map(element => {

            let route = element.data();

            route.id = element.id;

            return route;
        }))
        .then((routes) => {

            let promises = [];
            let routesFull = [];

            routes.forEach(route => {

                promises.push(db.collection("origins").doc(route.origin).get()
                .then((snapshot) => {

                    let routeFull = route;
                    routeFull.originName = snapshot.data().name;
                    routesFull.push(routeFull);

                    return routesFull;
                }));
            })

            return Promise.all(promises);

        }, error => { throw ERROR.server });
    }

    /**
     * @description Get a list of passenger´s routes.
     * 
     * @param {String} passengerId The id of passenger.
     * 
     * @returns {Promise} A promise with the list of passenger´s routes.
     */
    function getRoutesByPassenger(passengerId) {

        return db.collection("check-in").where("passenger", "==", passengerId).get()
        .then((snapshot) => {

            let destinationNames = snapshot.docs.map(element => element.data().address);
            let routeIds = snapshot.docs.map(element => element.data().route);
            let promises = [];

            routeIds.forEach(id => promises.push(getRouteById(id)));
            return Promise.all(promises)
            .then((routes) => {

                return routes.map((route, i) => {

                    let newRute = route;
                    newRute.destinationName = destinationNames[i];

                    return newRute;
                })
            })
        })
        .then((routes => {

            let promises = [];
            let routesFull = [];

            routes.forEach(route => {

                promises.push(db.collection("origins").doc(route.origin).get()
                .then((snapshot) => {

                    let routeFull = route;
                    routeFull.originName = snapshot.data().name;
                    routesFull.push(routeFull);

                    delete routeFull.passengers;

                    return routesFull;
                }));
            });

            return Promise.all(promises);
        }))
        .then((routes) => routes, error => { throw ERROR.server });
    }

    /**
     * @description Get a list of route's coordinates.
     * 
     * @param {String} routeId The id of route.
     * 
     * @returns {Promise} A promise with the list of route's coordinates.
     */
    function getRoutePoints(routeId) {

        return db.collection("check-in").where("route","==",routeId).get()
        .then((snapshot) => { return snapshot.docs.map(element => element.data()); })
    }

    /**
     * @description Get a destination of a route.
     * 
     * @param {String} routeId The id of route.
     * @param {String} passengerId The id of passenger.
     * 
     * @returns {Promise} A promise with the destination match with route and passenger.
     */
    function getDestination(routeId, passengerId) {

        return db.collection("check-in").where("route", "==", routeId).where("passenger", "==", passengerId).get()
        .then((snapshot) => { return snapshot.docs.length == 0 ? null: snapshot.docs[0].data().address });
    }

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

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
        getRoutePoints:getRoutePoints,
        getDestination:getDestination
    }