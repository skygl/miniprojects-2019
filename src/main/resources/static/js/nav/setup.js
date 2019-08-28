const MYPAGE_URL = (userId) => `/users/${userId}`

const setupNavBarUserName = () => {
    const currentUserName = document.getElementById("current-user-name")
    currentUserName.innerHTML = localStorage.loginUserName
    currentUserName.addEventListener('click', (event) => {
        location.href = MYPAGE_URL(localStorage.loginUserId)
    })
}

setupNavBarUserName()