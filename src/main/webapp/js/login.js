(function() {
    // Get the login form
    let loginForm = document.querySelector('.login-form')
    document.getElementById('error').style.display = 'none';

    // Attach event listener to the form's submit event
    loginForm.addEventListener('submit', function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Call the makeCall function
        makeFormCall('POST', '/PlaylistManager_war/Login', loginForm, function(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                // Handle the response here
                let message = req.responseText;
                if (req.status === 200) {
                    // Login was successful
                    let responseData = JSON.parse(req.responseText);
                    sessionStorage.setItem('userId', responseData.userId);
                    sessionStorage.setItem('userName', responseData.userName);
                    sessionStorage.setItem('playlists', JSON.stringify(responseData.playlists));
                    sessionStorage.setItem('songs', JSON.stringify(responseData.songs));
                    window.location.href = 'home.html';
                }
                else {
                    console.log("message: " + message);
                    document.getElementById('error').style.display = 'block';
                    document.getElementById('error-info').textContent = message;
                }

            }
        });
    });
})();