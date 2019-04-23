    /**
     * 
     * @module service_application
     * 
     * @description Apply the application logic about origin functions.
     * 
     */
    
    const ERROR = require("../errors");
    const originDAO = require("../data_access/origin_DAO");

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    /**
     * @description Get all route origins.
     * 
     * @returns {Promise} A promise that return a list of strings.
     */
    function getAllOrigins() { return originDAO.getAllOrigins(); };

    /**
     * @description Get a particular origin match with a name parameter.
     * 
     * @param {String} name The origin name.
     * 
     * @returns {Promise} A promise that returns the origin.
     * 
     * @throws {Object} Error
     */
    function getOriginByName(name) {

        return originDAO.getOriginByName(name)
        .then(origin => {

            if(origin == null) throw ERROR.originDoesntExists;
            else return origin;
        });
    };

    /**
     * @description Gets the origin data.
     * 
     * @param {String} id The origin id.
     * 
     * @returns {Promise} A promise that returns the origin.
     * 
     * @throws {Object} Error
     */
    function getOriginById(id) {

        return originDAO.getOriginById(id)
        .then((origin) => {

            if(origin == null) throw ERROR.originDoesntExists;
            else return origin;
        });
    };

    /**
     * @description Deletes an origin.
     * 
     * @param {String} id The origin id.
     * 
     * @returns {Promise} A promise with confirmation about deletion.
     * 
     * @throws {Object} Error
     */
    function deleteOriginById(id) {

        return originDAO.getOriginById(id)
        .then((origin) => {

            if(origin == null) throw ERROR.originDoesntExists;
            else return originDAO.deleteOriginById(id);
        });
    };

    /**
     * @description Modifies a origin.
     * 
     * @param {String} id The old origin id. 
     * @param {Object} newData The new origin.
     * 
     * @returns {Promise} A promise with confirmation about modification.
     * 
     * @throws {Object} Error
     */
    function modifyOriginById(id, newData) {

        return checkRequirements(newData)
        .then(() => originDAO.getOriginById(id))
        .then((origin) => {

            if(origin == null) throw ERROR.originDoesntExists;
            else if(origin.name == newData.name) return false;
            else return originDAO.modifyOriginById(id, newData);
        })
        .then((result)=>{return result != false});
    };

    /**
     * @description Creates a new origin if not exists.
     * 
     * @param {Object} newOrigin The new origin.
     * 
     * @returns {Promise} A promise with new id origin.
     * 
     * @throws {Object} Error
     */
    function createOrigin(newOrigin) {

        return checkRequirements(newOrigin)
        .then(() => originDAO.getOriginByName(newOrigin.name))
        .then((origin) => {

            if(origin != null) throw ERROR.originAlreadyExists;
            else return originDAO.insertOrigin(newOrigin);
        });
    };


    /* *********************************************************************************************** */
    /* *********************************************************************************************** */
    /* Private function of module */

    /**
     * @description Checks origin requirements.
     * 
     * @param {Object} origin The origin to check.
     * 
     * @returns {Promise} A promise with confirmation about data are correct or not.
     */
    function checkRequirements(origin) {

        return new Promise((resolve,reject) => {

            if(origin == null || origin.name == null || origin.name.length == 0 || 
                origin.coordinates == null || origin.coordinates == "") reject(ERROR.badRequestForm);
            
            else resolve();
        });
    };

    /* *********************************************************************************************** */
    /* *********************************************************************************************** */

    module.exports = {

        getAllOrigins:getAllOrigins,
        getOriginById:getOriginById,
        deleteOriginById:deleteOriginById,
        modifyOriginById:modifyOriginById,
        createOrigin:createOrigin,
        getOriginByName:getOriginByName
    }
