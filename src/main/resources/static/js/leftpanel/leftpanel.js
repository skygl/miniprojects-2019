const loadLeftPanelName = () => {
    document.getElementById('left-panel-name').innerHTML=localStorage.loginUserName
}

loadLeftPanelName()