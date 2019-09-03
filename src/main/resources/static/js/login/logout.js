function logout() {
    const logoutConfirm = confirm('로그아웃 하시겠습니까?')

    if (logoutConfirm) {
        api.POST("/logout")
            .then(res => {
                if (res.ok) {
                    localStorage.removeItem('loginUserId')
                    localStorage.removeItem('loginUserName')
                    localStorage.removeItem('loginUserEmail')
                    localStorage.removeItem('loginUserProfile')
                    alert('로그아웃 되었습니다.')
                } else {
                    throw res
                }
            })
            .catch(error => {
                error.json().then(errorMessage =>
                    alert(errorMessage.message))
            })

        window.location.href = "/users/form"
    }
}