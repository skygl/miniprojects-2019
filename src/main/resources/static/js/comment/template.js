const commentEditDeleteDropdown = `
<a class="pointer absolute top-0 right-0 view" data-toggle="dropdown" aria-expanded="false">
    <span class="btn-icon text-dark">
        <i class="ti-more font-size-16"></i>
    </span>
</a>
<a class="toggle-comment-update pointer absolute top-0 right-0 edit"  aria-expanded="true">
    <i class="ti-close pdd-right-10 text-dark"></i>
</a>
<ul class="dropdown-menu view">
    <li>
        <a class="pointer toggle-comment-update">
            <i class="ti-pencil pdd-right-10 text-dark"></i>
            <span class="">댓글 수정</span>
        </a>
    </li>
    <li>
        <a class="pointer comment-delete">
            <i class="ti-trash pdd-right-10 text-dark"></i>
            <span class="">댓글 삭제</span>
        </a>
    </li>
</ul>
`

const commentTemplate = (comment) => `
    <li class="comment-item parent-comment" data-id="${comment.id}">
        <img class="thumb-img img-circle" src=
        ${
            (() => {
                return (comment.userResponse.profile === undefined) || (comment.userResponse.profile === null) ?
                    DEFAULT_PROFILE_IMAGE_URL : comment.userResponse.profile.path
            })()
        }
         alt="">
        <div class="info">
            <div class="bg-lightgray border-radius-18 padding-10 max-width-100" >
                <a href="" class="title text-bold inline-block text-link-color">${comment.userResponse.name}</a>
                <span>${textFormat(comment.contents)}</span>
            </div>
            
           
            <div class="edit edit-form">
             <a class="toggle-comment-update pointer absolute top-0 right-0 edit"  aria-expanded="false">
                <i class="ti-close pdd-right-10 text-dark"></i>
            </a>
            <textarea class="bg-lightgray border-radius-18 padding-10 max-width-100 edit">${comment.contents}</textarea>
            </div>
            
            <div class="font-size-12 pdd-left-10 pdd-top-5">
                <span class="pointer text-link-color comment-good ${comment.goodResponse.gooded ? 'comment-good-active': ''}">좋아요</span>
                <span>·</span>
                <span class="pointer text-link-color toggle-child">답글 달기</span>
                <span>·</span>
                <span class="pointer">${dateFormat(comment.updatedAt, isUpdated(comment.createdAt, comment.updatedAt))}</span>
                <span>
                    <i class="fa fa-thumbs-o-up text-info font-size-16 mrg-left-5"></i>
                    <span class="comment-total-good font-size-14 lh-2-1">${comment.goodResponse.totalGood}</span> 
                </span>
            </div>
            ${
                (() => {
                    if (comment.userResponse.id == localStorage.loginUserId) {
                        return commentEditDeleteDropdown
                    } else {
                        return ""
                    }
                })()
            }

        </div>
    </li>
`;

const childCommentEditDeleteDropdown = (comment) => `
<a class="pointer absolute top-0 right-0 view width-30" data-toggle="dropdown" aria-expanded="false">
    <span class="btn-icon text-dark">
        <i class="ti-more font-size-16"></i>
    </span>
</a>

<div class="edit edit-form">
    <a class="toggle-comment-update pointer absolute top-0 right-100 edit"  aria-expanded="false">
        <i class="ti-close pdd-right-10 text-dark"></i>
    </a>
    <textarea class="bg-lightgray border-radius-18 padding-10 max-width-100 edit">${comment.contents}</textarea>
</div>

<ul class="dropdown-menu view">
    <li>
        <a class="pointer toggle-comment-update">
            <i class="ti-pencil pdd-right-10 text-dark"></i>
            <span class="">댓글 수정</span>
        </a>
    </li>
    <li>
        <a class="pointer comment-delete">
            <i class="ti-trash pdd-right-10 text-dark"></i>
            <span class="">댓글 삭제</span>
        </a>
    </li>
</ul>
`


const childCommentTemplate = (comment) => `
    <li class="comment-item child-comment" data-id="${comment.id}">
        <input class = "parentId" type="hidden" value="${comment.parentId}"/>
        <img class="thumb-img img-circle" src=
         ${
             (() => {
                 return (comment.userResponse.profile === undefined) || (comment.userResponse.profile === null) ?
                     DEFAULT_PROFILE_IMAGE_URL : comment.userResponse.profile.path
             })()
         }
         alt="">
        <div class="info">
            <div class="bg-lightgray border-radius-18 padding-10 width-75">
                <a href="" class="title text-bold inline-block text-link-color">${comment.userResponse.name}</a>
                <span>${textFormat(comment.contents)}</span>
            </div>
            <div class="font-size-12 pdd-left-10 pdd-top-5">
                <span class="pointer text-link-color comment-good  comment-${comment.goodResponse.gooded} ">좋아요</span>
                <span>·</span>
                <span class="pointer text-link-color toggle-child">답글 달기</span>
                <span>·</span>
                <span class="pointer">${dateFormat(comment.updatedAt, isUpdated(comment.createdAt, comment.updatedAt))}</span>
                <span>
                    <i class="fa fa-thumbs-o-up text-info font-size-16 mrg-left-5"></i>
                    <span class="comment-total-good font-size-14 lh-2-1">${comment.goodResponse.totalGood}</span> 
                </span>
            </div>
            ${
                (() => {
                    if (comment.userResponse.id == localStorage.loginUserId) {
                        return childCommentEditDeleteDropdown(comment)
                    } else {
                        return ""
                    }
                })()
            }
        </div>
    </li>
`;

const countOfChildren = (count) => `
 <span class="pointer text-link-color get-child" data-id="${count}">${count}개의 답글 보기</span>

`


const commentFormTemplate = `
<div class="add-comment">
    <textarea rows="1" class="form-control" placeholder="댓글을 입력하세요.." autofocus></textarea>
</div>
`;

const moreCommentsTemplate = (pageNumber)=>`
 <span class="pointer text-link-color more-comments" data-id="${pageNumber+1}">댓글 더 보기</span>
`;

