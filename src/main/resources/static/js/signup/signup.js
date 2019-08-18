const signup = async e => {
	const url = "/api/users"
	const header = {
		"Content-Type": "application/json; charset=UTF-8"
	}

	const form_data = JSON.stringify($("#reg").serializeObject())

	const res = await fetch(url, {
		method: "POST",
		body: form_data,
		headers: header
	})

	const result = await res
	const result_data = await result.json()

	if(result.status === 201) {
		alert('가입이 완료되었습니다. 로그인 해주세요.')
		clear()
	} else if(result.status === 400) {
		alert(result_data.errorMessage)
	}

}

const clear = async e => {
	document.getElementById("form-1-1").value = ""
	document.getElementById("form-1-2").value = ""
	document.getElementById("form-1-3").value = ""
}