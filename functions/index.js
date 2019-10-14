// Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions')
// Import and initialize the Firebase Admin SDK.
const admin = require('firebase-admin');
admin.initializeApp();

//Send Notification to all Coordinators whenever a new Request is made

exports.sendNotifications = functions.firestore.document('All_Student_Requests/{id}').onCreate(
  async (snapshot) => {

    //Compose Notification
    const payload = {
      data:{
        title: `${snapshot.data().requestType} from ${snapshot.data().user.schoolId}`,
        location:`${snapshot.data().user.schoolId} is going to ${snapshot.data().location}`,
        body: snapshot.data().purpose,
        icon: snapshot.data().user.imageUri,
        requestId:snapshot.data().requestUniqueId,
      }
    };

    // Get all Coordinator tokens
    const allCoodinatorTokens = await admin.firestore().collection('Student_Coordinator').get();
    const tokens = [];
    allCoodinatorTokens.forEach((coordinatorDocs) => {
      console.log('Doc maybe real', coordinatorDocs.data().fcmToken);
      tokens.push(coordinatorDocs.data().fcmToken);
    });

    if (tokens.length > 0) {
      const response = await admin.messaging().sendToDevice(tokens, payload);
      console.log('Notifications has been sent');
    }
  });

  // Cleans up the tokens that are no longer valid.
function cleanupTokens(response, tokens) {
// For each notification we check if there was an error.
const tokensDelete = [];
response.results.forEach((result, index) => {
  const error = result.error;
  if (error) {
    console.error('Failure sending notification to', tokens[index], error);
    // Cleanup the tokens who are not registered anymore.
    if (error.code === 'messaging/invalid-registration-token' ||
        error.code === 'messaging/registration-token-not-registered') {
      const deleteTask = admin.firestore().collection('All_Student_Requests').doc(tokens[index]).delete();
      tokensDelete.push(deleteTask);
    }
  }
});
return Promise.all(tokensDelete);
}


exports.createSecurity = functions.firestore.document('Security/{id}').onCreate(
  async (snapshot) => {

  admin.auth().createUser({
    uid:snapshot.data().uniqueId,
    email: snapshot.data().email,
    emailVerified: false,
    password: snapshot.data().password
  })
  .then(function(userRecord) {
    // See the UserRecord reference doc for the contents of userRecord.
    console.log('Successfully created new user:', userRecord.uid);
    return userRecord.sendEmailVerification();
  })
    .then(function() {
      // See the UserRecord reference doc for the contents of userRecord.
      console.log('Successfully sent verification email to user');
      return null
    })
    .catch(function(error) {
      console.log('Error creating new user:', error);
      return null
    });
    return null
});


exports.createCoordinator = functions.firestore.document('Student_Coordinator/{id}').onCreate(
  async (snapshot) => {

  admin.auth().createUser({
    uid:snapshot.data().uniqueId,
    email: snapshot.data().email,
    emailVerified: false,
    password: snapshot.data().password
  })
  .then(function(userRecord) {
    // See the UserRecord reference doc for the contents of userRecord.
    console.log('Successfully created new user:', userRecord.uid);
    return userRecord.sendEmailVerification();
  })
    .then(function() {
      // See the UserRecord reference doc for the contents of userRecord.
      console.log('Successfully sent verification email to user');
      return null
    })
    .catch(function(error) {
      console.log('Error creating new user:', error);
      return null
    });
    return null
});
