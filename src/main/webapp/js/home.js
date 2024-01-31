(function() {
    //Vars
    var userData, uploadSong, createPlaylist;

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
                sessionStorage.getItem('songs'),
                [document.getElementById("userName")],
                document.getElementById("playlists")

                // document.getElementById("logout-button")
            );

            // Upload song component
            uploadSong = new UploadSong(
                document.querySelector('.upload-song-form')
            );

            // Create new playlist component
            createPlaylist = new CreatePlaylist(
                document.querySelector('.create-playlist-form')
            );

            // Fill the song selection multiselect with songs
            fillSongSelection();
        }
        /**
         * Method of the PageOrchestrator to refresh the view
         */
        this.refresh = function () {
            // Refreshes view
            userData = new UserData(
                sessionStorage.getItem('id'),
                sessionStorage.getItem('userName'),
                sessionStorage.getItem('playlists'),
                sessionStorage.getItem('songs'),
                [document.getElementById("userName")],
                document.getElementById("playlists")
            );
            userData.show();
        };
    }

    function UserData(
        _userId,
        _userName,
        _playlists,
        _songs,
        nameElements,
        playlistsElement
    ) {
        this.userId = _userId;
        this.userName = _userName;
        this.playlists = JSON.parse(_playlists);
        this.songs = JSON.parse(_songs);
        this.playlistsElement = playlistsElement;

        this.show = function () {
            nameElements.forEach(element => {
                element.textContent = this.userName;
            });
            this.showPlaylists();
        }

        this.showPlaylists = function () {
            this.playlistsElement.innerHTML = '';

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
        uploadSongForm.addEventListener('submit', function(event) {
            // Prevent the default form submission
            event.preventDefault();

            // Call the makeCall function
            makeFormCall('POST', '/PlaylistManager_war/UploadSong', uploadSongForm, function(req) {

                if (req.readyState === XMLHttpRequest.DONE) {
                    // Handle the response here
                    let message = req.responseText;
                    if (req.status === 200) {
                        // Login was successful
                        console.log("Song uploaded");
                        let responseData = JSON.parse(req.responseText);
                        console.log(responseData);
                        sessionStorage.setItem('userSongs', JSON.stringify(responseData));
                        document.querySelector('.upload-song').style.display = 'none';

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

    function fillSongSelection() {
        // Get the songs from sessionStorage
        let songs = sessionStorage.getItem('songs');
        // Get the multiselect element
        let songSelect = document.querySelector('#songSelection');
        songs = JSON.parse(songs);
        if (songs.length>0) {
            // Clear the current options
            songSelect.innerHTML = '';

            // Create an option element for each song and append it to the multiselect
            songs.forEach(song => {
                let option = document.createElement('option');
                option.value = song.id;
                option.textContent = song.title;
                songSelect.appendChild(option);
            });
        } else {
            let option = document.createElement('option');
            option.textContent = "No songs available";
            songSelect.appendChild(option);
            console.log('No songs found in sessionStorage');
        }
    }
function CreatePlaylist(createPlaylistForm) {
    createPlaylistForm.addEventListener('submit', function(event) {
        // Prevent the default form submission
        event.preventDefault();

        // Get the playlist name and selected songs
        let playlistTitle = document.getElementById('playlistTitle').value;
        let songSelection = Array.from(document.getElementById('songSelection').selectedOptions).map(option => option.value);

        // Create a FormData object and append the playlist name and selected songs
        // let formData = new FormData();
        createPlaylistForm.append('playlistTitle', playlistTitle);
        createPlaylistForm.append('songSelection', JSON.stringify(songSelection));

        // Call the makeCall function
        makeFormCall('POST', '/PlaylistManager_war/CreateNewPlaylist', createPlaylistForm, function(req) {

            if (req.readyState === XMLHttpRequest.DONE) {
                // Handle the response here
                let message = req.responseText;
                if (req.status === 200) {
                    // Playlist creation was successful
                    console.log("Playlist created");
                    let responseData = JSON.parse(req.responseText);
                    console.log(responseData);
                    sessionStorage.setItem('playlists', JSON.stringify(responseData));
                    pageOrchestrator.refresh();
                }
            }
        });
    });
}


})();
