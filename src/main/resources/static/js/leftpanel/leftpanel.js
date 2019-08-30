const setupLeftPanelName = () => {
    document.getElementById('left-panel-name').innerHTML = localStorage.loginUserName
}

const setupLeftPanelProfile = () => {
    const currentUserProfile = document.getElementById('left-panel-profile')
    const imageUrl = (localStorage.loginUserProfile) ? localStorage.loginUserProfile : DEFAULT_PROFILE_IMAGE_URL

    currentUserProfile.setAttribute('src', imageUrl)
}

setupLeftPanelName()
setupLeftPanelProfile()