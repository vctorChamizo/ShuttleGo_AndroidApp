const ERROR = require("../errors");
const originDAO = require("../dataAccess/originDAO");

/**
 * @description Get all route origins
 * @returns {Promise} a promise that return a list of strings
 */
function getAllOrigins (){
    return originDAO.getAllOrigins();
}
module.exports = {
    getAllOrigins:getAllOrigins
}