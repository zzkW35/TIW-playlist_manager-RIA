(function() {
    //Vars
    var userData, uploadSong;

    var pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        // initialize the components
        pageOrchestrator.start();
        // display initial content
        pageOrchestrator.refresh();

    });


    function PageOrchestrator() {

        this.start = function () {
            // Init components
            // UserData component
            userData = new UserData(
                sessionStorage.getItem('id'),
                sessionStorage.getItem('userName'),
                sessionStorage.getItem('playlists'),
                [document.getElementById("userName")],
                document.getElementById("playlists")

                // document.getElementById("logout-button")
            );

            // Upload song component
            uploadSong = new UploadSong(
                document.getElementById("upload-song-form")
            );
        }
        /**
         * Method of the PageOrchestrator to refresh the view
         */
        this.refresh = function (excludeContacts) {
            // Refreshes view
            userData.show();
            // accountList.show();
            //
            // if (!excludeContacts) {
            //
            //     addressBook.load();
            // }
        };
    }

    function UserData(
        _userId,
        _userName,
        _playlists,
        nameElements,
        playlistsElement
    ) {
        this.userId = _userId;
        this.userName = _userName;
        this.playlists = JSON.parse(_playlists);
        this.playlistsElement = playlistsElement;

        this.show = function () {
            nameElements.forEach(element => {
                element.textContent = this.userName;
            });
            this.showPlaylists();
        }

        this.showPlaylists = function () {
            this.playlists.forEach(playlist => {
                let playlistCard = document.createElement('div');
                playlistCard.className = 'playlist-card';

                let playlistTitle = document.createElement('div');
                playlistTitle.className = 'playlist-title';
                playlistTitle.textContent = playlist.title;
                playlistCard.appendChild(playlistTitle);

                let playlistDate = document.createElement('div');
                playlistDate.className = 'playlist-date';
                playlistDate.textContent = playlist.creationDate;
                playlistCard.appendChild(playlistDate);

                this.playlistsElement.appendChild(playlistCard);
            });
        }
    }

    function UploadSong(
        uploadSongForm
    ) {
        this.uploadSongForm = uploadSongForm;

        this.uploadSongForm.addEventListener('submit', function(event) {
            // Prevent the default form submission
            event.preventDefault();

            // Call the makeCall function
            window.makeCall('POST', '/PlaylistManager_war/UploadSong', uploadSongForm, function(req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    // Handle the response here
                    let message = req.responseText;
                    if (req.status === 200) {
                        // Login was successful
                        console.log("Song uploaded");
                    } //else {
                    //     // Login failed
                    //     let errorMessage = document.querySelector('.error-message');
                    //     errorMessage.textContent = message;
                    //     errorMessage.classList.remove('hidden');
                    // }

                }
            });
        });
    }

})();
