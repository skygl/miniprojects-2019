const POST_URL = '/api/posts'

const posts = document.getElementById('contents')

const writeBtn = document.getElementById('write-btn')
const writeArea = document.getElementById('write-area')

let totalPage;
let currentPageNumber = 1;
writeBtn.addEventListener('click', (event) => {
    const contents = writeArea.value

    api.POST(POST_URL, contents)
        .then(res => {
            if (res.ok) {
                return res.json();
            }
            throw res
        })
        .then(post => posts.prepend(createPostDOM(post)))
        .catch(error => {
            error.json()
                .then(errorMessage =>
                    console.log(errorMessage.message)
                )
        })


    writeArea.value = ''
})

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
    div.innerHTML = postTemplate(post)
    return div
}

window.onload = initLoad
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

        const init = function () {
            modifyPost();
            deletePost();
        };

        return {
            init: init
        }
    };

    const PostService = function () {
        const toggleUpdate = function (event) {
            const buttonContainer = event.target.closest("a")
            if (buttonContainer.classList.contains('toggle-post-update')) {
                const postCard = buttonContainer.closest('.card');
                postCard.classList.toggle('editing');
            }
        };

        const update = function (event) {
            const buttonContainer = event.target.closest("li")
            if (buttonContainer.classList.contains('post-update')) {
                const postCard = buttonContainer.closest('.card');
                const postId = postCard.dataset.id
                const updatePostContainer = buttonContainer.closest("div");
                const contents = updatePostContainer.querySelector("textArea").value;

                api.PUT(getTargetPostUrl(postId), contents)
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
                                console.log(errorMessage.message)
                            )
                    )
            }
        }

        const remove = (event) => {
            const buttonContainer = event.target.closest("a")
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

        return {
            toggleUpdate: toggleUpdate,
            update: update,
            remove: remove
        }
    };

    const init = function () {
        const buttonController = new PostController();
        buttonController.init();
    };

    return {
        init: init
    }
})();

postOperateButton.init();