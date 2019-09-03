const friendTagService = (function() {
	const createFriendsList = () => {
        api.GET("/api/friends")
            .then(res => {
            	if (res.status == 200) {
            		return res.json()
            	}
            	throw res.json()
            })
            .then(friends => {
                for (let i = 0; i < friends.length; i++) {
                    createFriendDOM(friends[i])
                }
            })
            .catch(errorMessage => {
            	alert(errorMessage.message)
            })
	}

	const deleteFriendsList = () => {
		const friendsTagList = document.getElementsByClassName('friends-tag-list')[0]
		while (friendsTagList.hasChildNodes()) { 
			friendsTagList.removeChild(friendsTagList.firstChild);
		}
	}

	const createFriendDOM = friend => {
        document.getElementsByClassName('friends-tag-list')[0].insertAdjacentHTML('beforeend', friendTagTemplate(friend))
	}

	const tag = () => {
		const friends_tag_list_items = document.getElementsByClassName('friends-tag-list')[0].getElementsByTagName('li')
		let tagged_id = []
		for(let i = 0; i < friends_tag_list_items.length; i++) {
			if (friends_tag_list_items[i].getElementsByTagName('input')[0].checked == true) {
				tagged_id.push(friends_tag_list_items[i].getAttribute('data-id'))
			}
		}
		return tagged_id
	}

	const tagName = () => {
		const friends_tag_list_items = document.getElementsByClassName('friends-tag-list')[0].getElementsByTagName('li')
		let tagged_name = []
		for(let i = 0; i < friends_tag_list_items.length; i++) {
			if (friends_tag_list_items[i].getElementsByTagName('input')[0].checked == true) {
				tagged_name.push(friends_tag_list_items[i].getElementsByTagName('span')[0].innerHTML)
			}
		}
		return tagged_name
	}

	const cancelTag = () => {
		const friends_tag_list_items = document.getElementsByClassName('friends-tag-list')[0].getElementsByTagName('li')
		let tagged_id = []
		for(let i = 0; i < friends_tag_list_items.length; i++) {
			if (friends_tag_list_items[i].getElementsByTagName('input')[0].checked == true) {
				friends_tag_list_items[i].getElementsByTagName('input')[0].checked = false
			}
		}
		document.getElementById('tagged_user_names').innerHTML = ""
	}

	const showTaggedUserNames = () => {
		const tagged_name = tagName()
		console.log(tagged_name)
		document.getElementById('tagged_user_names').innerHTML = tagged_name.length == 0 ? "" : tagged_name.join(",") + "님과 함께"
	}

	const init = () => {
		deleteFriendsList()
		createFriendsList()
	}

	return {
		init : () => init(),
		tag : () => tag(),
		cancelTag : () => cancelTag(),
		showTaggedUserNames : () => showTaggedUserNames()
	}
})()

$('#friend-tag-modal').on('show.bs.modal', function(e) {
	friendTagService.init()
})
