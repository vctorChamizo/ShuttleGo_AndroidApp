var admin = require("firebase-admin");

var serviceAccount = require("./shuttlebus-c7c54-firebase-adminsdk-bmff9-c8e91d2f08.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://shuttlebus-c7c54.firebaseio.com"
});

const db = admin.firestore();
db.settings.timestampsInSnapshots=true;

module.exports = db;