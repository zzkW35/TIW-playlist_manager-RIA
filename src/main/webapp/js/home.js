(function() {
    //Vars
    var userData, accountList, transferResult, transferList, addressBook;

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
                [document.getElementById("userName")],
                // document.getElementById("logout-button")
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
        _userid,
        _username,
        nameElements,
        // _logout_button
    ) {

        this.id = _userid;
        this.userName = _username;
        // this.logout_button = _logout_button;

        // this.logout_button.addEventListener("click", e => {
        //
        //     sessionStorage.clear();
        // });

        /**
         * Method of the class UserData that shows the UserData
         * binding them with their corresponding elements
         */
        this.show = function () {
            nameElements.forEach(element => {

                element.textContent = this.userName;
            });
        }
    }




})();
