const login = e => {
	const url = "/login"
	const header = {
		"Content-Type": "application/json; charset=UTF-8"
	}

	const form_data = JSON.stringify($("#login_form").serializeObject())

	fetch(url, {
		method: "POST",
		body: form_data,
		headers: header
	}).then(res => {
		if (res.status === 200) {
			alert('로그인되었습니다.')
			window.location.href="/"
		}
		else if (res.status === 400) {
			res.json().then(err => alert(err.errorMessage))
		}
	})

}