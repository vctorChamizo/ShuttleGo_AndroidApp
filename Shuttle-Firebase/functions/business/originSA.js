const ERROR = require("../errors");
const originDAO = require("../dataAccess/originDAO");

/**
 * @description Get all route origins
 * @returns {Promise} a promise that return a list of strings
 */
function getAllOrigins(){
    return originDAO.getAllOrigins();
}

function getOriginById(id){
    return originDAO.getOriginById(id)
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return origin;
    })
}

function deleteOriginById(id){
    return originDAO.getOriginById(id)
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return originDAO.deleteOriginById(id);
    });
}

function modifyOriginById(id,newData){
    return checkRequirements(newData)
    .then(()=>originDAO.getOriginById(id))
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return originDAO.modifyOriginById(id,newData);
    });
}


function createOrigin(newOrigin){
    return checkRequirements(newOrigin)
    .then(()=>originDAO.getOriginByName(newOrigin.name))
    .then((origin)=>{
        if(origin != null) throw ERROR.originAlreadyExists;
        else return originDAO.createOrigin(newOrigin);
    })
}


//-----------Private Methods-----------------
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
    createOrigin:createOrigin
}