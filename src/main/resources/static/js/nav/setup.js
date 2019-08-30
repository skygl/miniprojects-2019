const MYPAGE_URL = (userId) => `/users/${userId}`
const DEFAULT_PROFILE_IMAGE_URL = "/images/default/profile-default.png"

const setupNavBarUserName = () => {
    const currentUserName = document.getElementById("current-user-name")

    currentUserName.innerHTML = localStorage.loginUserName
    currentUserName.addEventListener('click', (event) => {
        location.href = MYPAGE_URL(localStorage.loginUserId)
    })
}

const setupNavBarUserProfile = () => {
    const currentUserProfile = document.getElementById("current-user-profile")
    const imageUrl = (localStorage.loginUserProfile) ? localStorage.loginUserProfile : DEFAULT_PROFILE_IMAGE_URL

    currentUserProfile.setAttribute('src', imageUrl)
}

setupNavBarUserName()
setupNavBarUserProfile()