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
    return originDAO.getOriginById(id)
    .then((origin)=>{
        if(origin == null) throw ERROR.originDoesntExists;
        else return originDAO.modifyOriginById(id,newData);
    });
}

module.exports = {
    getAllOrigins:getAllOrigins,
    getOriginById:getOriginById,
    deleteOriginById:deleteOriginById,
    modifyOriginById:modifyOriginById
}