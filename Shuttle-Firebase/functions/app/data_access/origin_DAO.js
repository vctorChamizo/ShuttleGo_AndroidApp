    /**
     * 
     * @module data_access
     * 
     * @description Database manager and origin data exchange.
     * 
     */

    const ERROR = require("../errors");
    const db = require("./database.js");

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Get all route origins from the database.
     * 
     * @returns {Promise} A promise that return a list of strings.
     */
    function getAllOrigins() {

        return db.collection("origins").get()
        .then((snapshot) => {
            
            let origins = [];

            snapshot.docs.forEach((doc) => { origins.push({name:doc.data().name, id:doc. id,coordinates:doc.data().coordinates });})
            return origins;

        }, error => { throw ERROR.server });
    }

    /**
     * @description Get the origin data.
     * 
     * @param {String} id The id of the origin.
     * 
     * @returns {Promise} A promise that returns a origin.
     */
    function getOriginById(id) {

        return db.collection("origins").doc(id).get()
        .then((snapshot) => {

            if(!snapshot.exists) return null;
            else {

                let origin = snapshot.data();

                origin.id = snapshot.id;

                return origin;
            }

        }, error => { throw ERROR.server });
    }

    /**
     * @description Get the origin data.
     * 
     * @param {String} name The name of the origin.
     * 
     * @returns {Promise} A promise that returns a origin.
     */
    function getOriginByName(name) { 

        return db.collection("origins").where("name", "==", name).get()
        .then((snapshot) => {

            if(snapshot.docs.length > 0) {

                let origin = snapshot.docs[0].data();

                origin.id = snapshot.docs[0].id;

                return origin;
            }
            else return null;
            
        }, error => {throw ERROR.server});
    }

    /**
     * @description Deletes a origin.
     * 
     * @param {String} id the origin id.
     */
    function deleteOriginById(id) { 

        return db.collection("origins").doc(id).delete()
        .then(() => null, error => { throw ERROR.server });
    }

    /**
     * @description Modifies the origin.
     * 
     * @param {String} id The old origin id.
     * @param {Object} newData The new origin data.
     */
    function modifyOriginById(id, newData) {

        if(newData.id != "undefined") delete newData.id;

        return db.collection("origins").doc(id).set(newData)
        .then(() => null, error => { throw ERROR.server });
    }

    /**
     * @description Insert a new origin in the database.
     * 
     * @param {Object} newData the new origin.
     */
    function insertOrigin(newData) { 

        if(newData.id != "undefined") delete newData.id;

        return db.collection("origins").add(newData)
        .then((result) => result.id, error => { throw ERROR.server });
    }
    
    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    module.exports = {

        getAllOrigins:getAllOrigins,
        getOriginById:getOriginById,
        deleteOriginById:deleteOriginById,
        modifyOriginById:modifyOriginById,
        insertOrigin:insertOrigin,
        getOriginByName:getOriginByName
    }