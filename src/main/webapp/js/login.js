(function() {
    // Get the login form
    let loginForm = document.querySelector('.login-form');

    // Attach event listener to the form's submit event
    loginForm.addEventListener('submit', function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Call the makeCall function
        makeCall('POST', '/PlaylistManager_war/GoToHomePage', loginForm, function(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                // Handle the response here
                let message = req.responseText;
                if (req.status === 200) {
                    // Login was successful
                    let responseData = JSON.parse(req.responseText);
                    sessionStorage.setItem('id', responseData.userId);
                    sessionStorage.setItem('name', responseData.userName);
                    sessionStorage.setItem('playlists', JSON.stringify(responseData.playlists));
                    sessionStorage.setItem('songs', JSON.stringify(responseData.songs));
                    // window.location.href = '/PlaylistManager_war/home.html';
                } else {
                    // Login failed
                    let errorMessage = document.querySelector('.error-message');
                    errorMessage.textContent = message;
                    errorMessage.classList.remove('hidden');
                }

            }
        });
    });
})();