const firebaseModule = (function () {
    async function init() {
        // Your web app's Firebase configuration
        if ('serviceWorker' in navigator) {
            window.addEventListener('load', function() {
                navigator.serviceWorker.register('/firebase-messaging-sw.js')
                    .then(registration => {
                        var firebaseConfig = {
                            apiKey: "AIzaSyBWbLdjemTbpsGckTlyDqaATpbI_0usdHw",
                            authDomain: "woowa-turkey.firebaseapp.com",
                            databaseURL: "https://woowa-turkey.firebaseio.com",
                            projectId: "woowa-turkey",
                            storageBucket: "",
                            messagingSenderId: "732784692174",
                            appId: "1:732784692174:web:555e23a165bac677"
                        };
                        // Initialize Firebase
                        firebase.initializeApp(firebaseConfig);


                        // Show Notificaiton Dialog
                        const messaging = firebase.messaging();
                        messaging.requestPermission()
                        .then(function() {
                            return messaging.getToken();
                        })
                        .then(async function(token) {
                            await fetch('/register', { method: 'post', body: token })
                            messaging.onMessage(payload => {
                                const title = payload.notification.title
                                const options = {
                                    body : payload.notification.body
                                }
                                navigator.serviceWorker.ready.then(registration => {
                                    registration.showNotification(title, options);
                                })
                            })
                        })
                        .catch(function(err) {
                            console.log("Error Occured");
                        })
                    })
            })
        }
    }      

    return {
        init: function () {
            init()
        }
    }
})()

firebaseModule.init()