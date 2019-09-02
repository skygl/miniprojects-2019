const loginMarkTemplate = `
<span aria-label="현재 로그인" style="background: rgb(66, 183, 42); border-radius: 50%; display: inline-block; height: 6px; margin-left: 4px; width: 6px;"></span>
`

const logoutMarkTemplate = `
<span aria-label="현재 로그아웃 " style="background: rgb(255, 0, 0); border-radius: 50%; display: inline-block; height: 6px; margin-left: 4px; width: 6px;"></span>
`

const allUserTemplate = (user) =>
`
<div class="btn-group dropleft data-id" data-id="${user.id}">
    <button type="button" class="list-group-item list-group-item-action" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <img class="img-circle width-25px" src=
        ${
            (() => {
                return (user.profile === undefined) || (user.profile === null) ?
                  DEFAULT_PROFILE_IMAGE_URL : user.profile.path
            })()
        }
        >

        ${user.name}
        ${
            (() => {
                if (user.login) {
                    return loginMarkTemplate
                } else {
                    return logoutMarkTemplate
                }
            })()
        }
    </button>
    <div class="dropdown-menu dropdown-menu-left">
    	<button class="FriendAsk" aria-label="${user.name}님을 친구로 추가" type="button">
    		<i class="friendAddBtn"></i>
    		친구 추가
    	</button>
    	<button class="start-messenger" aria-label="${user.name}님과 메신저" type="button">
            <i class="friendAddBtn"></i>
            메신저
    	</button>
    </div>
</div>
`

const friendTemplate = (friend) =>
`
<div class="btn-group dropleft data-id" data-id="${friend.friendId}">
    <button type="button" class="list-group-item list-group-item-action" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <img class="img-circle width-25px" src=
        ${
            (() => {
                return (friend.profile === undefined) || (friend.profile === null) ?
                  DEFAULT_PROFILE_IMAGE_URL : friend.profile.path
            })()
        }
        >
        ${friend.relatedUserName}
        ${
            (() => {
                if (friend.login) {
                    return loginMarkTemplate
                } else {
                    return logoutMarkTemplate
                }
            })()
        }
    </button>
    <div class="dropdown-menu dropdown-menu-left">
    	<button class="FriendRemove" aria-label="${friend.relatedUserName}님을 친구 삭제" type="button">
    		<i class="ti-trash"></i>
    		친구 삭제
    	</button>
    </div>
</div>
`
