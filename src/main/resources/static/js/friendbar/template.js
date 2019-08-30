const allUserTemplate = (user) =>
`
<div class="btn-group dropleft" data-id="${user.id}">
    <button type="button" class="list-group-item list-group-item-action" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <img class="img-circle width-25px" src="/images/default/eastjun_profile.jpg">
        ${user.name}
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
<div class="btn-group dropleft" data-id="${friend.friendId}">
    <button type="button" class="list-group-item list-group-item-action" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <img class="img-circle width-25px" src="/images/default/eastjun_profile.jpg">
        ${friend.relatedUserName}
    </button>
    <div class="dropdown-menu dropdown-menu-left">
    	<button class="FriendRemove" aria-label="${friend.relatedUserName}님을 친구 삭제" type="button">
    		<i class="ti-trash"></i>
    		친구 삭제
    	</button>
    </div>
</div>
`
