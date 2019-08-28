const POST_URL = '/api/posts'
const ENTER = 13

const posts = document.getElementById('contents')

let totalPage;
let currentPageNumber = 1;

const initLoad = async () => {
    await api.GET(POST_URL)
        .then(res => res.json())
        .then(postPage => {
            totalPage = postPage.totalPages;
            postPage.content.forEach(post => {
                posts.appendChild(createPostDOM(post))
            })
        })
        .catch(error => console.error(error))

    const writeBtn = document.getElementById('write-btn')
    const writeArea = document.getElementById('write-area')
    writeBtn.addEventListener('click', (event) => {
        const formData = new FormData();

        const contents = writeArea.value
        formData.append('contents', contents)

        const receiver = writeArea.dataset.id
        if (receiver !== undefined && receiver !== localStorage.loginUserId) {
            formData.append('receiver', receiver)
        }

        const fileForm = event.target.closest('ul').querySelector('input[name="filename[]"]')
        const files = fileForm.files

        const len = files.length;
        for (let i = 0; i < len; i++) {
            formData.append('files', files[i]);
        }

        formDataApi.POST(POST_URL, formData)
            .then(res => {
                if (res.status == 201) {
                    return res.json();
                }
                throw res
            })
            .then(post => {
                posts.prepend(createPostDOM(post))

                const postWriteContainer = writeArea.closest('.card');
                const filesPreviewContainer = postWriteContainer.querySelector('.files-preview');
                filesPreviewContainer.innerHTML = ''
                fileForm.value = ''
                writeArea.value = ''
            })
            .catch(error => {
                error.json().then(errorMessage =>
                    alert(errorMessage.message))
            })
    })
}

const infinityScroll = (event) => {
    if (totalPage === currentPageNumber) {
        return false
    }

    const currentScrollPercentage = (window.scrollY + window.innerHeight) / document.body.scrollHeight * 100

    if (currentScrollPercentage > 90) {
        api.GET(POST_URL + `?page=${currentPageNumber++}`)
            .then(res => res.json())
            .then(postPage => {
                totalPage = postPage.totalPages;
                postPage.content.forEach(post => {
                    posts.appendChild(createPostDOM(post))
                })
            })
            .catch(error => console.error(error))
    }
}

const createPostDOM = (post) => {
    const div = document.createElement('div');
    div.innerHTML = postTemplate(post, localStorage.loginUserId)
    return div
}

document.addEventListener('scroll', infinityScroll);

const postOperateButton = (function () {
        const postList = document.getElementById('contents');

        const PostController = function () {
            const postService = new PostService();

            const modifyPost = function () {
                postList.addEventListener('click', postService.toggleUpdate);
                postList.addEventListener('click', postService.update);
            };

            const deletePost = function () {
                postList.addEventListener('click', postService.remove);
            }

            const getComments = function () {
                postList.addEventListener('click', postService.getComments);
            }

            const toggleCommentUpdate = function () {
                postList.addEventListener('click', postService.toggleCommentUpdate);
            }

            const deleteComment = function () {
                postList.addEventListener('click', postService.deleteComment)
            }

            const toggleChildCommentForm = function () {
                postList.addEventListener('click', postService.toggleChildCommentForm)
            }

            const getChildren = function () {
                postList.addEventListener('click', postService.getChildren)
            }

            const init = function () {
                modifyPost();
                deletePost();
                getComments();
                toggleCommentUpdate();
                deleteComment();
                toggleChildCommentForm();
                getChildren();
            };

            return {
                init: init
            }
        };

        const PostService = function () {
            const toggleUpdate = function (event) {
                const buttonContainer = event.target.closest("a")
                if (buttonContainer == null) {
                    return
                }
                if (buttonContainer.classList.contains('toggle-post-update')) {
                    const postCard = buttonContainer.closest('.card');
                    postCard.classList.toggle('editing');
                }
            };

            const update = function (event) {
                const buttonContainer = event.target.closest("li")

                if (buttonContainer == null) {
                    return
                }

                if (buttonContainer.classList.contains('post-update')) {
                    const postCard = buttonContainer.closest('.card');
                    const postId = postCard.dataset.id
                    const updatePostContainer = buttonContainer.closest("div");
                    const contents = updatePostContainer.querySelector("textArea").value;

                    const postRequest = {
                        contents: contents
                    }

                    api.PUT(getTargetPostUrl(postId), postRequest)
                        .then(response => {
                            if (!response.ok) {
                                throw response;
                            }
                            return response.json();
                        })
                        .then(post => {
                            const updatePostContainer = createPostDOM(post);

                            postCard.parentNode.innerHTML = updatePostContainer.innerHTML;
                            postCard.classList.toggle('editing');
                        })
                        .catch(errorResponse =>
                            errorResponse.json()
                                .then(errorMessage =>
                                    alert(errorMessage.message)
                                )
                        )
                }
            }

            const remove = (event) => {
                const buttonContainer = event.target.closest("a")
                if (buttonContainer == null) {
                    return
                }
                if (buttonContainer.classList.contains('post-delete')) {
                    const postCard = buttonContainer.closest('.card');
                    const postId = postCard.dataset.id

                    api.DELETE(getTargetPostUrl(postId))
                        .then(response => {
                            if (response.status !== 204) {
                                throw response;
                            }
                            postList.removeChild(postCard.parentNode);
                        })
                        .catch(errorResponse =>
                            errorResponse.json()
                                .then(errorMessage =>
                                    console.log(errorMessage.message)
                                )
                        )
                }
            }

            const getTargetPostUrl = (postId) => {
                return POST_URL + "/" + postId;
            }

            const getComments = (event) => {
                const target = event.target

                const commentBtn = target.closest('button');

                if (target.classList.contains('more-comments') || (commentBtn !== null && commentBtn.classList.contains('comment'))) {
                    const postCard = target.closest('.card');
                    const postId = postCard.dataset.id
                    const commentsContainer = postCard.querySelector('.comment-list')
                    const page = event.target.dataset.id

                    if (page == null) {
                        commentsContainer.innerHTML = commentFormTemplate
                        commentsContainer.querySelector('.add-comment').addEventListener('keyup', postComment)
                    }
                    api.GET(`/api/posts/${postId}/comments?page=${page}`)
                        .then(res => res.json())
                        .then(comments => {
                            comments.content.forEach(comment => {
                                const div = document.createElement('div')
                                if (comments.content.parentId == null) {
                                    div.innerHTML = commentTemplate(comment)
                                    if (comment.countOfChildren > 0) {
                                        div.innerHTML += countOfChildren(comment.countOfChildren)
                                    }
                                } else {
                                    div.innerHTML = childCommentTemplate(comment)
                                }
                                div.addEventListener('keyup', updateComment)
                                commentsContainer.appendChild(div)
                            })

                            if (target.classList.contains('more-comments')) {
                                target.parentNode.removeChild(target)
                            }
                            const pageable = comments.pageable
                            if (!comments.last) {
                                const div = document.createElement('div')
                                div.innerHTML = moreCommentsTemplate(pageable.pageNumber)
                                commentsContainer.appendChild(div)
                            }
                        })
                        .catch(error => console.error(error))
                }
            }

            const postComment = (event) => {
                const contentsArea = event.target
                const contents = contentsArea.value

                if ((event.shiftKey && event.keyCode === ENTER) || event.keyCode !== ENTER || contents.length === 0) {
                    return
                }

                const postCard = contentsArea.closest(".card");
                const postId = postCard.dataset.id
                const commentsContainer = postCard.querySelector('.comment-list')
                const parentComment = event.target.closest('.parent-comment');
                const parentId = parentComment === null ? null : parentComment.dataset.id

                const commentCreate = {
                    "contents": contents,
                    "parentId": parentId
                }

                api.POST(`/api/posts/${postId}/comments`, commentCreate)
                    .then(res => res.json())
                    .then(comment => {
                        const div = document.createElement('div')
                        if (comment.parentId == null) {
                            div.innerHTML = commentTemplate(comment)
                            commentsContainer.appendChild(div)
                            contentsArea.value = ''
                        } else {
                            const form = parentComment.querySelector('.add-comment').parentNode
                            form.parentNode.removeChild(form)
                            div.innerHTML = childCommentTemplate(comment)
                            parentComment.appendChild(div)
                        }

                        const totalCountContainer = postCard.querySelector('.totalComment')
                        const totalCount = parseInt(totalCountContainer.innerHTML)+1
                        totalCountContainer.innerHTML = totalCount

                    })
                    .catch(error => console.error(error))
            }

            const toggleCommentUpdate = (event) => {
                const buttonContainer = event.target.closest("a")

                if (buttonContainer === null) {
                    return
                }

                if (buttonContainer.classList.contains('toggle-comment-update')) {
                    const commentItem = buttonContainer.closest('.comment-item')
                    commentItem.querySelector('.info').classList.toggle('editing')
                }
            }

            const updateComment = (event) => {
                const contentsArea = event.target
                const contents = contentsArea.value

                if ((event.shiftKey && event.keyCode === ENTER) || event.keyCode !== ENTER || contents.length === 0) {
                    return
                }

                if (!event.target.classList.contains('edit')) {
                    return
                }

                const postId = contentsArea.closest(".card").dataset.id
                const commentItem = contentsArea.closest('.comment-item')
                const commentId = commentItem.dataset.id
                const commentUpdate = {
                    "contents": contents,
                }

                api.PUT(`/api/posts/${postId}/comments/${commentId}`, commentUpdate)
                    .then(res => res.json())
                    .then(comment => {
                        const contentsNode = commentItem.querySelector('span');
                        const editContentsNode = commentItem.querySelector('textarea.bg-lightgray');
                        contentsNode.innerText = comment.contents
                        editContentsNode.value = comment.contents
                        commentItem.querySelector('.info').classList.toggle('editing')
                    })
                    .catch(error => console.error(error))
            }

            const deleteComment = (event) => {
                const commentDeleteBtn = event.target.closest('a');

                if (commentDeleteBtn === null) {
                    return
                }

                if (commentDeleteBtn.classList.contains('comment-delete')) {
                    const buttonContainer = event.target.closest("ul").closest("li")
                    const postCard = buttonContainer.closest('.card');
                    const postId = postCard.dataset.id;
                    const commentId = buttonContainer.dataset.id;

                    api.DELETE(`/api/posts/${postId}/comments/${commentId}`)
                        .then(res => {
                            if (res.status !== 204) {
                                throw res;
                            }
                            const commentCard = buttonContainer.parentNode
                            commentCard.removeChild(buttonContainer);

                            const totalCountContainer = postCard.querySelector('.totalComment')
                            const totalCount = parseInt(totalCountContainer.innerHTML)-1
                            totalCountContainer.innerHTML = totalCount
                        })
                        .catch(error => console.error(error))
                }
            }

            const toggleChildCommentForm = (event) => {
                const commentsContainer = event.target.closest('li')
                const commentItem = event.target.closest('.comment-item');
                if (commentItem === null) return
                const form = commentItem.querySelector('.add-comment')

                if (event.target.classList.contains('toggle-child') && form === null) {
                    const div = document.createElement('div')
                    div.innerHTML = commentFormTemplate
                    commentsContainer.appendChild(div)
                    commentsContainer.querySelector('.add-comment').addEventListener('keyup', postComment)
                }
            }

            const getChildren = (event) => {
                if (event.target.classList.contains('get-child')) {
                    const parentContainer = event.target.closest('div').querySelector('li');
                    const postId = event.target.closest('.card').dataset.id;
                    const parentId = parentContainer.dataset.id;
                    const size = event.target.dataset.id;

                    api.GET(`/api/posts/${postId}/comments/${parentId}/children?size=${size}`)
                        .then(res => res.json())
                        .then(comments => {
                            comments.content.forEach(comment => {
                                const div = document.createElement('div')
                                div.innerHTML = childCommentTemplate(comment)
                                parentContainer.appendChild(div)
                            })
                            event.target.parentNode.removeChild(event.target)
                        })
                        .catch(error => console.error(error))
                }
            }

            return {
                toggleUpdate: toggleUpdate,
                update: update,
                remove: remove,
                getComments: getComments,
                toggleCommentUpdate: toggleCommentUpdate,
                deleteComment: deleteComment,
                toggleChildCommentForm: toggleChildCommentForm,
                getChildren: getChildren,
            }
        };

        const init = function () {
            const buttonController = new PostController();
            buttonController.init();
        };

        return {
            init: init
        }
    }
)();

const fileAttach = (function () {
    const postsContainer = document.getElementById('posts');

    const FileAttachController = function () {
        const fileAttachService = new FileAttachService();

        const showFileAttach = function () {
            postsContainer.addEventListener('click', fileAttachService.showFileAttachForm)
            postsContainer.addEventListener('change', fileAttachService.previewFileAttach)
        }

        const init = function () {
            showFileAttach();
        };

        return {
            init: init
        }
    }

    const FileAttachService = function () {
        const showFileAttachForm = function (event) {
            const fileAttachContainer = event.target.closest('li');
            if (fileAttachContainer && fileAttachContainer.classList.contains('file-attach')) {
                const fileInput = fileAttachContainer.firstElementChild;
                fileInput.click();
            }
        };

        const previewFileAttach = function (event) {
            const fileAttachContainer = event.target.closest('li');
            if (fileAttachContainer && fileAttachContainer.classList.contains('file-attach')) {
                const postWriteContainer = fileAttachContainer.closest('.card');
                const filesPreviewContainer = postWriteContainer.querySelector('.files-preview');
                filesPreviewContainer.innerHTML = ''

                const files = fileAttachContainer.firstElementChild.files;
                const fileInfos = [];
                const len = files.length;
                for (let i = 0; i < len; i++) {
                    const fileInfo = new FileInfo(files[i].type, URL.createObjectURL(files[i]))
                    fileInfos.push(fileInfo);
                }

                try {
                    filesPreviewContainer.innerHTML = previewTemplate(fileInfos)
                } catch (err) {
                    alert(err.message)
                }
            }
        }

        const FileInfo = function (type, src) {
            this.type = type
            this.src = src
        }

        return {
            showFileAttachForm: showFileAttachForm,
            previewFileAttach: previewFileAttach
        }
    }

    const init = function () {
        const fileAttachController = new FileAttachController();
        fileAttachController.init();
    };

    return {
        init: init
    }
})();

postOperateButton.init();
fileAttach.init();
initLoad();