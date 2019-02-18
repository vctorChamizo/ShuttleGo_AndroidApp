/**
 * @module business/originSA
 */
const ERROR = require("../errors");
const originDAO = require("../dataAccess/originDAO");

/**
 * @description Get all route origins
 * @returns {Promise} A promise that return a list of strings
 */
function getAllOrigins(){
    return originDAO.getAllOrigins();
}


function getOriginByName(name){
    return originDAO.getOriginByName(name)
    .then(origin=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return origin;
    }
    )
}

/**
 * Gets the origin data.
 * @param {string} id The origin id.
 * @returns {Promise} A promise that returns the origin.
 */
function getOriginById(id){
    return originDAO.getOriginById(id)
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return origin;
    })
}

/**
 * @description Deletes an origin.
 * @param {string} id The origin id.
 */
function deleteOriginById(id){
    return originDAO.getOriginById(id)
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return originDAO.deleteOriginById(id);
    });
}

/**
 * @description Modifies a origin.
 * @param {string} id The old origin id. 
 * @param {Object} newData The new origin.
 */
function modifyOriginById(id,newData){
    return checkRequirements(newData)
    .then(()=>originDAO.getOriginById(id))
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return originDAO.modifyOriginById(id,newData);
    });
}

/**
 * @description Creates a new origin if not exists.
 * @param {Object} newOrigin The new origin.
 */
function createOrigin(newOrigin){
    return checkRequirements(newOrigin)
    .then(()=>originDAO.getOriginByName(newOrigin.name))
    .then((origin)=>{
        if(origin != null) throw ERROR.originAlreadyExists;
        else return originDAO.createOrigin(newOrigin);
    })
}


//-----------Private Methods-----------------

/**
 * Checks origin requeriments,this function should be used when you want to insert or modify new data into the database.
 * @param {Object} origin The origin to check.
 */
function checkRequirements(origin){
    return new Promise((resolve,reject)=>{
        if(origin == null || origin.name == null || origin.name.length == 0) reject(ERROR.badRequestForm);
        else resolve();
    })
}

module.exports = {
    getAllOrigins:getAllOrigins,
    getOriginById:getOriginById,
    deleteOriginById:deleteOriginById,
    modifyOriginById:modifyOriginById,
    createOrigin:createOrigin,
    getOriginByName:getOriginByName
}