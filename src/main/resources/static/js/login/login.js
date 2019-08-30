function login() {
    const enter = 13;
    const click = 1;

    if(!(event.keyCode === enter || event.which === click)) return

    const loginRequest = serializeObject($("#login_form"))

    api.POST("/login", loginRequest)
        .then(res => {
            if (res.ok) {
                return res.json();
            } else {
                throw res
            }
        })
        .then(loginUser => {
            localStorage.loginUserId = loginUser.id
            localStorage.loginUserName = loginUser.name
            localStorage.loginUserEmail = loginUser.email
            if (loginUser.profile) {
                localStorage.loginUserProfile = loginUser.profile.path
            } else {
                localStorage.removeItem('loginUserProfile')
            }
            alert('로그인되었습니다.')
            window.location.href="/"
        })
        .catch(error => {
            error.json().then(errorMessage =>
                alert(errorMessage.message))
        })
}

document.getElementById('pass').addEventListener('keyup', login)
document.getElementById('email').addEventListener('keyup', login)