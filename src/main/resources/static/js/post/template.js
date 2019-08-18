const postTemplate = (post) => `
<div class="card widget-feed padding-15" data-id="${post.id}">
    <div class="feed-header">
        <ul class="list-unstyled list-info">
            <li>
                <img class="thumb-img img-circle" src="images/default/eastjun_profile.jpg" alt="">
                <div class="info">
                    <a href="" class="title no-pdd-vertical text-semibold inline-block">${post.author.name}</a>
                    <span>님이 그룹에 링크를 공유했습니다.</span>
                    <span class="sub-title">${dateFormat(post.updatedAt, isUpdated(post.createdAt, post.updatedAt))}</span>
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
                </div>
            </li>
        </ul>
    </div>
    <div class="feed-body no-pdd">
        <p class="view">
            <span>${post.contents.contents}</span>
        </p>
        <div class="edit edit-form">
            <textarea class="resize-none form-control border bottom resize-none edit">${post.contents.contents}</textarea>
            <ul class="composor-tools pdd-top-15">
                <div>
                <li class="bg-lightgray border-radius-round mrg-right-5">
                    <a class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <div class="icons photo-video"></div>
                        <span class="icon-name font-size-13 text-bold"> 사진/동영상</span>
                    </a>
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
            <span class="font-size-14 lh-2-1">67</span>
        </li>
        <li class="float-right">
            <span  class="font-size-13">공유 78회</span>
        </li>
        <li class="float-right mrg-right-15">
            <span class="font-size-13">댓글 2개</span>
        </li>
    </ul>
    <ul class="feed-action border bottom d-flex">
        <li class="text-center flex-grow-1">
            <button class="btn btn-default no-border pdd-vertical-0 no-mrg width-100">
                <i class="fa fa-thumbs-o-up font-size-16"></i>
                <span class="font-size-13">좋아요</span>
            </button>
        </li>
        <li class="text-center flex-grow-1">
            <button class="btn btn-default no-border pdd-vertical-0 no-mrg width-100">
                <i class="fa fa-comment-o font-size-16"></i>
                <span  class="font-size-13">댓글</span>
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
            <ul class="list-unstyled list-info">
                <li class="comment-item">
                    <img class="thumb-img img-circle" src="images/default/eastjun_profile.jpg" alt="">
                    <div class="info">
                        <div class="bg-lightgray border-radius-18 padding-10 max-width-100">
                            <a href="" class="title text-bold inline-block text-link-color">eastjun</a>
                            <span>크 멋져요. MVC패턴을 직접 프로젝트에 적용해봤나요?</span>
                        </div>
                        <div class="font-size-12 pdd-left-10 pdd-top-5">
                            <span class="pointer text-link-color">좋아요</span>
                            <span>·</span>
                            <span class="pointer text-link-color">답글 달기</span>
                            <span>·</span>
                            <span class="pointer">2시간</span>
                        </div>
                    </div>
                </li>
            </ul>
            <div class="add-comment">
                <textarea rows="1" class="form-control" placeholder="댓글을 입력하세요.."></textarea>
            </div>
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
    const timeDiff = Math.floor((Date.now() - date) / (1000 * 60 * 24))

    return timeDiff < 24 ? `${timeDiff}시간 전` : dateFormatting(date) +
        (updated ? '(edited)' : '')
}

