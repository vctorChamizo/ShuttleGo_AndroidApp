const admin = require('firebase-admin');
admin.initializeApp(require('firebase-functions').config().firebase);
  

module.exports = admin.database();