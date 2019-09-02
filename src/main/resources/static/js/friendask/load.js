$(document).ready(function () {
    friendAskModule.init()
})

const friendAskModule = (function () {
    async function loadFriendsAsks() {
        await api.GET("/api/friends/asks")
            .then(res => res.json())
            .then(data => {
                for (let i = 0; i < data.length; i++) {
                    createFriendAskDOM(data[i])
                }

                addFriendAskAcceptEventListener()
                addFriendAskRejectEventListener()
                preventDropdownHide()
            })
	}

    function createFriendAskDOM(friendAsk) {
        document.getElementById('friend-ask-list').insertAdjacentHTML('beforeend', friendAskTemplate(friendAsk))
    }

	function addFriendAskAcceptEventListener() {
		const btns = document.getElementsByClassName('friend-ask-accept-btn')
		for(let i = 0; i < btns.length; i++) {
			btns[i].addEventListener('click', async function(event) {
			    const data = {"friendAskId" : event.target.parentNode.getAttribute("data-id")}

                const res = await api.POST("/api/friends", data)

	            if(res.status == 201) {
	                alert('친구가 되었습니다~')
	                const parent = event.target.parentNode
	                parent.parentNode.removeChild(parent)
	            } else if(res.status == 400) {
                    const error = await res.json()
    	            alert(error.message)
	            }
			})
		}
	}

	function addFriendAskRejectEventListener() {
		const btns = document.getElementsByClassName('friend-ask-reject-btn')
		for(let i = 0; i < btns.length; i++) {
			btns[i].addEventListener('click', async function(event) {
			    const friendAskId = event.target.parentNode.getAttribute("data-id")
			    const deleteUrl = "/api/friends/asks/" + friendAskId

                const res = await api.DELETE(deleteUrl)

	            if(res.status == 204) {
	                alert('친구 요청이 거절되었습니다.')
	                const parent = event.target.parentNode
	                parent.parentNode.removeChild(parent)
	            } else if(res.status == 400) {
                    const error = await res.json()
    	            alert(error.message)
	            }
			})
		}
	}

    function preventDropdownHide() {
        $('div.btn-group.dropleft').on('click', function (event) {
            let events = $._data(document, 'events') || {}
            events = events.click || []
            for (let i = 0; i < events.length; i++) {
                if (events[i].selector) {
                    if ($(event.target).is(events[i].selector)) {
                        events[i].handler.call(event.target, event)
                    }

                    $(event.target).parents(events[i].selector).each(function () {
                        events[i].handler.call(this, event)
                    })
                }
            }
            event.stopPropagation()
        })
    }

    return {
        init: function () {
            loadFriendsAsks()
        }
    }
})()