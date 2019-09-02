const MY_PAGE_URL = (userId) => `/users/${userId}`

const postEditDeleteDropdown = `
<a class="pointer absolute top-0 right-0 view" data-toggle="dropdown" aria-expanded="false">
    <span class="btn-icon text-dark">
        <i class="ti-more font-size-16"></i>
    </span>
</a>
<a class="toggle-post-update pointer absolute top-0 right-0 edit"  aria-expanded="false">
    <i class="ti-close pdd-right-10 text-dark"></i>
</a>
<ul class="dropdown-menu view">
    <li>
        <a class="pointer toggle-post-update">
            <i class="ti-pencil pdd-right-10 text-dark"></i>
            <span class="">게시글 수정</span>
        </a>
    </li>
    <li>
        <a class="pointer post-delete">
            <i class="ti-trash pdd-right-10 text-dark"></i>
            <span class="">게시글 삭제</span>
        </a>
    </li>
</ul>
`

const postTemplate = (post, loginUserId) => `
<div class="card widget-feed padding-15" data-id="${post.id}">
    <div class="feed-header">
        <ul class="list-unstyled list-info">
            <li>
                <img class="thumb-img img-circle" src=
                 ${
                     (() => {
                         return (post.author.profile === undefined) || (post.author.profile === null) ?
                            DEFAULT_PROFILE_IMAGE_URL : post.author.profile.path
                     })()
                 }
                 alt="">
                <div class="info">
                    <a href="${MY_PAGE_URL(post.author.id)}" class="title no-pdd-vertical text-semibold inline-block">${post.author.name}</a>
                    ${
                        (() => {
                            if (post.receiver !== null) {
                                return receiverFormat(post.receiver)
                            }
                            return ""
                        })()
                    }                
                    <span class="sub-title">${dateFormat(post.updatedAt, isUpdated(post.createdAt, post.updatedAt))}</span>
                    ${
                        (() => {
                            if (post.author.id == loginUserId) {
                                return postEditDeleteDropdown
                            }
                            return ""
                        })()
                    }
                </div>
            </li>
        </ul>
    </div>
    <div class="feed-body no-pdd">
        <p class="view">
            ${textFormat(post.contents.contents)}
        </p>
        <div class="post-files">
            ${post.files.length === 0 ? '' : carouselTemplate(post)}
        </div>
        <div class="edit edit-form">
            <textarea class="resize-none form-control border bottom resize-none edit">${post.contents.contents}</textarea>
            <div class="files-preview"></div>
            <ul class="composor-tools pdd-top-15">
                <div>
                <li class="bg-lightgray border-radius-round mrg-right-5 file-attach">
                    <input accept="video/*, image/*" multiple name="filename[]" style="display:none" type="file"/>
                    <div class="pdd-vertical-5 pdd-horizon-10 pointer file-attach">
                        <div class="inline-block icons photo-video"></div>
                        <span class="icon-name font-size-13 text-bold"> 사진/동영상</span>
                    </div>
                </li>
                <li class="bg-lightgray border-radius-round mrg-right-5">
                    <a class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <div class="icons tag-friend"></div>
                        <span class="icon-name font-size-13 text-bold"> 친구 태그하기</span>
                    </a>
                </li>
                <li class="bg-lightgray border-radius-round mrg-right-5">
                    <a class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <div class="icons feeling-activity"></div>
                        <span class="icon-name font-size-13 text-bold"> 기분/활동</span>
                    </a>
                </li>
                </div>
                <li class="bg-lightgray border-radius-round mrg-right-5 post-update">
                    <a class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <i class="icons ti-save"></i>
                        <span class="icon-name font-size-13 text-bold">게시</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <ul class="feed-action pdd-btm-5 border bottom">
        <li>
            <i class="fa fa-thumbs-o-up text-info font-size-16 mrg-left-5"></i>
            <span class="show-post-good font-size-14 lh-2-1">${post.goodResponse.totalGood}</span>
        </li>
        <li class="float-right">
            <span  class="font-size-13">공유 0회</span>
        </li>
        <li class="float-right mrg-right-15">
            댓글<span class="font-size-13 totalComment"> ${post.totalComment}</span>개
        </li>
    </ul>
    <ul class="feed-action border bottom d-flex">
        <li class="text-center flex-grow-1">
            <button class="good btn btn-default no-border pdd-vertical-0 no-mrg width-100 ${post.goodResponse.gooded ? 'good-active':''}">
                <i class="fa fa-thumbs-o-up font-size-16"></i>
                <span class="font-size-13">좋아요</span>
            </button>
        </li>
        <li class="text-center flex-grow-1">
            <button class="btn btn-default no-border pdd-vertical-0 no-mrg width-100 comment">
                <i class="fa fa-comment-o font-size-16"></i>
                <span  class="font-size-13 comments-button">댓글</span>
            </button>
        </li>
        <li class="text-center flex-grow-1">
            <button class="btn btn-default no-border pdd-vertical-0 no-mrg width-100">
                <i class="fa fa-share font-size-16"></i>
                <span class="font-size-13">공유하기</span>
            </button>
        </li>
    </ul>
    <div class="feed-footer">
        <div class="comment">
            <ul class="list-unstyled list-info comment-list">

            </ul>
        </div>
        <div class="comment-form">
        
        </div>
    </div>
</div>
`

const isUpdated = (createdAt, updatedAt) => {
    const createdDate = new Date(createdAt)
    const updatedDate = new Date(updatedAt)
    return createdDate !== updatedDate
}

const dateFormatting = (date) => {
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
}

const dateFormat = (date, updated) => {
    date = new Date(date)
    const timeDiff = Math.floor((Date.now() - date + 1000) / (1000 * 60 * 60))

    return timeDiff < 24 ? `${timeDiff}시간 전` : dateFormatting(date) +
        (updated ? '(edited)' : '')
}

const textFormat = contentStr => {
    const div = document.createElement('div');
    div.innerText = contentStr
    return div.outerHTML
}

const receiverFormat = (receiver) => `
<i class="ti-angle-right"></i>
<a href="${MY_PAGE_URL(receiver.id)}" class="title no-pdd-vertical text-semibold inline-block">${receiver.name}</a> 
`