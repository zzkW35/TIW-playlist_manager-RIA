(function() {
    //Vars
    let userData, uploadSong, createPlaylist;

    let pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        // initialize the components
        pageOrchestrator.start();
        // display initial content
        pageOrchestrator.refresh();

    });


    function PageOrchestrator() {

        this.start = function () {
            // Init components
            document.getElementById('playlist-page').style.display = 'none';
            document.getElementById('update-playlist').style.display = 'none';
            document.getElementById('player').style.display = 'none';
            document.getElementById('reorder-button').style.display = 'none';
            document.getElementById('save-order-button').style.display = 'none';


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
            fillSongSelection('songs', '#songSelection');
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

                // Add event listener to the playlist card
                playlistCard.addEventListener('click', function() {
                    // Hide all other components
                    document.querySelector('.home-title').style.display = 'none';
                    document.querySelector('.playlists').style.display = 'none';
                    document.querySelector('.upload-song').style.display = 'none';
                    document.querySelector('.create-playlist').style.display = 'none';

                    // Show the playlist info
                    let playlistInfo = document.getElementById('playlist-page');
                    playlistInfo.style.display = 'block';
                    console.log(playlist.id);
                    getPlaylistPage(playlist.id);
                });

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
                        // document.querySelector('.upload-song').style.display = 'none';
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

    function fillSongSelection(songsToGet, multiselectElement) {
        // Get the songs from sessionStorage
        let songs = sessionStorage.getItem(songsToGet);
        // Get the multiselect element
        let songSelect = document.querySelector(multiselectElement);
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
            let songSelection = Array.from(document.getElementById('songSelection').selectedOptions)
                .map(option => option.value);

            let encodedSongSelection = encodeURIComponent(JSON.stringify(songSelection));
            let url = '/PlaylistManager_war/CreateNewPlaylist?playlistTitle=' + playlistTitle +
                "&songSelection=" + encodedSongSelection;

            makeFormCall('POST', url, null, function(req) {
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

    function getPlaylistPage(playlistId) {
        let url = '/PlaylistManager_war/GetPlaylist?playlistId=' + playlistId;
        makeFormCall('GET', url,  null, function(req) {
            let updatePlaylist;
            if (req.readyState === XMLHttpRequest.DONE) {
                // Handle the response here
                let message = req.responseText;
                if (req.status === 200) {
                    // Login was successful
                    console.log("Playlist created");
                    let responseData = JSON.parse(req.responseText);
                    console.log(responseData);
                    let songsRes = responseData.songs;
                    console.log("unsorted songs:" + JSON.stringify(songsRes))
                    songsRes.sort((a, b) => a.position - b.position);
                    console.log("sorted songs:" + JSON.stringify(songsRes))

                    sessionStorage.setItem('songs', JSON.stringify(songsRes));
                    sessionStorage.setItem('songsNotInPlaylist', JSON.stringify(responseData.songsNotInPlaylist));
                    console.log("Songs not in playlist: " + responseData.songsNotInPlaylist);
                    sessionStorage.setItem('playlistId', responseData.playlistId);
                    document.getElementById('playlist-name').textContent = responseData.playlistTitle;

                    let songTable = document.querySelector('.songTable');
                    let prevButton = document.querySelector('#prevButton');
                    let nextButton = document.querySelector('#nextButton');

                    let startIndex = 0;
                    let endIndex = 5;

                    function updateTable() {
                        songTable.innerHTML = '';
                        let songRow = document.createElement('tr');

                        for (let i = startIndex; i < endIndex && i < songsRes.length; i++) {
                            let song = songsRes[i];
                            let songCell = document.createElement('td');
                            songRow.appendChild(songCell);

                            let songCoverDiv = document.createElement('div');
                            let songCoverImage = document.createElement('img');
                            songCoverImage.src = song.coverPath;
                            songCoverDiv.appendChild(songCoverImage);
                            songCell.appendChild(songCoverDiv);

                            let songTitleDiv = document.createElement('div');
                            songTitleDiv.textContent = song.title;
                            songTitleDiv.className = 'song-title'; // Add a class to the song title div
                            songCell.appendChild(songTitleDiv);

                            // Add event listener to the song title div
                            songTitleDiv.addEventListener('click', function() {
                                // Hide all other components
                                document.querySelector('.home-title').style.display = 'none';
                                document.querySelector('.playlists').style.display = 'none';
                                document.querySelector('.upload-song').style.display = 'none';
                                document.querySelector('.create-playlist').style.display = 'none';
                                document.querySelector('.playlist-page').style.display = 'none';
                                document.querySelector('.update-playlist').style.display = 'none';
                                document.getElementById('reorder-button').style.display = 'none';


                                // Show the song info
                                let songInfo = document.getElementById('player');
                                songInfo.style.display = 'block';
                                console.log(song)

                                // Populate the song info div with the song details
                                document.getElementById('song-title').textContent = song.title;
                                document.getElementById('song-artist').textContent = song.artist;
                                document.getElementById('song-album').textContent = song.album;
                                document.getElementById('song-genre').textContent = song.genre;
                                document.getElementById('song-year').textContent = song.albumYear;
                                document.getElementById('player-img').src = song.coverPath;
                                let audioElement = document.getElementById('player-audio');
                                audioElement.src = song.filePath;
                                audioElement.load();
                            });
                        }
                        songTable.appendChild(songRow);

                        // Hide or show the "Previous" button
                        if (startIndex > 0) {
                            prevButton.style.display = '';
                        } else {
                            prevButton.style.display = 'none';
                        }

                        // Hide or show the "Next" button
                        if (endIndex < songsRes.length) {
                            nextButton.style.display = '';
                        } else {
                            nextButton.style.display = 'none';
                        }
                    }

                    prevButton.addEventListener('click', function () {
                        if (startIndex > 0) {
                            startIndex -= 5;
                            endIndex -= 5;
                            updateTable();
                        }
                    });

                    nextButton.addEventListener('click', function () {
                        if (endIndex < songsRes.length) {
                            startIndex += 5;
                            endIndex += 5;
                            updateTable();
                        }
                    });

                    updateTable();
                    document.getElementById('update-playlist').style.display = 'block';
                    // Create new update playlist component
                    updatePlaylist = new UpdatePlaylist(
                        document.querySelector('.update-playlist-form')
                    );

                    fillSongSelection('songsNotInPlaylist', '#update-songSelection');
                    document.getElementById('reorder-button').style.display = 'block';

                    document.getElementById('reorder-button').addEventListener('click', function() {
                        // Hide everything on the screen
                        document.querySelector('.home-title').style.display = 'none';
                        document.querySelector('.playlists').style.display = 'none';
                        document.querySelector('.upload-song').style.display = 'none';
                        document.querySelector('.create-playlist').style.display = 'none';
                        document.querySelector('.playlist-page').style.display = 'none';
                        document.querySelector('.update-playlist').style.display = 'none';
                        document.getElementById('reorder-button').style.display = 'none';

                        // Display the song list
                        document.getElementById('song-list').style.display = 'block';
                        document.getElementById('save-order-button').style.display = 'block';

                        // Populate the song list
                        let songs = JSON.parse(sessionStorage.getItem('songs'));
                        let songList = document.getElementById('song-list');
                        songs.forEach(song => {
                            let listItem = document.createElement('li');
                            listItem.textContent = song.title;
                            listItem.id = song.id; // Use the song id as the list item id
                            listItem.draggable = true;
                            songList.appendChild(listItem);
                        });

                        let listItems = document.querySelectorAll('#song-list li');
                        listItems.forEach(item => {
                            item.addEventListener('dragstart', function(e) {
                                e.dataTransfer.setData('text/plain', e.target.id);
                            });
                        });

                        document.getElementById('song-list').addEventListener('dragover', function(e) {
                            e.preventDefault(); // Allow dropping
                        });

                        document.getElementById('song-list').addEventListener('drop', function(e) {
                            e.preventDefault(); // Prevent the default action (open as link)
                            let id = e.dataTransfer.getData('text/plain');
                            e.target.parentNode.insertBefore(document.getElementById(id), e.target.nextSibling);
                        });

                        document.getElementById('save-order-button').addEventListener('click', function() {
                            let listItems = document.querySelectorAll('#song-list li');
                            let newOrder = Array.from(listItems).map(item => item.id);
                            // let newOrder = Array.from(listItems).map((item, index) => {
                            //     return {[item.id]: index};
                            // });
                            console.log(JSON.stringify(newOrder));
                            sessionStorage.setItem('songs', JSON.stringify(newOrder));
                            let encodedNewOrder = encodeURIComponent(JSON.stringify(newOrder));
                            let playlistId = sessionStorage.getItem('playlistId');
                            let url = '/PlaylistManager_war/SortSongs?playlistId=' + playlistId +
                                "&newOrder=" + encodedNewOrder;
                            makeFormCall('POST', url, null, function(req) {
                                if (req.readyState === XMLHttpRequest.DONE) {
                                    // Handle the response here
                                    let message = req.responseText;
                                    if (req.status === 200) {
                                        // Playlist creation was successful
                                        console.log("Songs reordered successfully");
                                        // let responseData = JSON.parse(req.responseText);
                                        // console.log(responseData);
                                        // sessionStorage.setItem('songs', JSON.stringify(responseData));
                                        // getPlaylistPage(playlistId);
                                    }
                                }
                            });
                        });

                    });

                }
            }
        });
    }

    function UpdatePlaylist(updatePlaylistForm){
        // updatePlaylistForm.innerHTML = '';
        updatePlaylistForm.addEventListener('submit', function(event) {
            // Prevent the default form submission
            event.preventDefault();

            // Get the playlist name and selected songs
            let playlistId = sessionStorage.getItem('playlistId');
            let songSelection = Array.from(document.getElementById('update-songSelection').selectedOptions)
                .map(option => option.value);

            let encodedSongSelection = encodeURIComponent(JSON.stringify(songSelection));
            let url = '/PlaylistManager_war/AddSongToPlaylist?playlistId=' + playlistId +
                "&songSelection=" + encodedSongSelection;

            makeFormCall('POST', url, null, function(req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    // Handle the response here
                    let message = req.responseText;
                    if (req.status === 200) {
                        // Playlist creation was successful
                        console.log("Song added successfully");
                        // let responseData = JSON.parse(req.responseText);
                        // console.log(responseData);
                        // sessionStorage.setItem('playlists', JSON.stringify(responseData));
                        // getPlaylistPage(playlistId);
                    }
                }
            });
        });
    }




})();
