const writeFormTemplate = (receiver) => `
<div class="social-feeds">
    <div class="card widget-compose">
        <p class="border bottom width-100 pdd-btm-5 text-bold">게시물 만들기</p><p id="tagged_user_names"></p>
        <textarea data-id="${receiver.id}" id="write-area" class="resize-none form-control border bottom resize-none" placeholder="${getSuggestMessage(receiver)}"} ></textarea>
        <div class="files-preview"></div>
        <ul class="composor-tools pdd-top-15">
            <div>
                <li class="bg-lightgray border-radius-round mrg-right-5 file-attach">
                    <input accept="video/*, image/*" multiple name="filename[]" style="display:none" type="file"/>
                    <div class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <div class="inline-block icons photo-video"></div>
                        <span class="icon-name font-size-13 text-bold"> 사진/동영상</span>
                    </div>
                </li>
                <li class="bg-lightgray border-radius-round mrg-right-5">
                    <div class="pdd-vertical-5 pdd-horizon-10 pointer" id="friends-tag-btn" data-toggle="modal" data-target='#friend-tag-modal' data-keyboard="false" data-backdrop="static">
                        <div class="icons tag-friend"></div>
                        <span class="icon-name font-size-13 text-bold"> 친구 태그하기</span>
                    </div>
                </li>
                <li class="bg-lightgray border-radius-round mrg-right-5">
                    <a class="pdd-vertical-5 pdd-horizon-10 pointer">
                        <div class="icons feeling-activity"></div>
                        <span class="icon-name font-size-13 text-bold"> 기분/활동</span>
                    </a>
                </li>
            </div>
            <li class="bg-lightgray border-radius-round mrg-right-5">
                <a id="write-btn" class="pdd-vertical-5 pdd-horizon-10 pointer">
                    <i class="icons ti-save"></i>
                    <span class="icon-name font-size-13 text-bold">게시</span>
                </a>
            </li>
        </ul>
    </div>
</div>
`

const getSuggestMessage = (receiver) => {
    if (receiver.id == localStorage.loginUserId) {
        return `${localStorage.loginUserName}님, 무슨 생각을 하고 계신가요?`
    }
    return `${receiver.name}님에게 글을 남겨보세요`
}