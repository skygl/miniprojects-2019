const searchResult = (function () {
    const resultContainer = document.getElementById('result-container')

    const SearchController = function () {
        const searchService = new SearchService()

        const listAll = function () {
            const searchAllBtn = document.getElementById('search-all')
            searchAllBtn.addEventListener('click', searchService.searchAll)
        }

        const listUsers = function () {
            const searchUsersBtn = document.getElementById('search-users')
            searchUsersBtn.addEventListener('click', searchService.searchUsers)
        }

        const listPosts = function () {
            const searchPostsBtn = document.getElementById('search-posts')
            searchPostsBtn.addEventListener('click', searchService.searchPosts)
        }

        const listImages = function () {
            const searchImagesBtn = document.getElementById('search-images')
            searchImagesBtn.addEventListener('click', searchService.searchImages)
        }

        const init = function () {
            searchService.searchAll()
            listAll()
            listUsers()
            listImages()
            listPosts()
        }

        return {
            init: init,
        }
    }

    const SearchService = function () {

        const searchAll = function () {
            searchUsers('', 5)
            searchPosts()
            searchImages()
        }

        const searchUsers = function (event, size = 10) {
            clearResultContainer()
            api.GET(`/api/users/search?name=${keyword}&size=${size}`)
                .then(res => res.json())
                .then(pageable => {

                    const users = pageable.content;
                    const ul = document.createElement('ul')
                    ul.classList.add('user-result-card')

                    users.forEach(user => {
                        const li = document.createElement('li')
                        li.classList.add('user-result-li')
                        li.innerHTML = resultUserTemplate(user)
                        ul.appendChild(li)
                    })
                    resultContainer.appendChild(ul)
                })
                .catch(error => console.error(error))
        }

        const searchPosts = function () {

        }

        const searchImages = function () {

        }

        const clearResultContainer = function () {
            resultContainer.innerHTML = ''
        }

        return {
            searchAll: searchAll,
            searchUsers: searchUsers,
            searchPosts: searchPosts,
            searchImages: searchImages,
        }
    }

    const init = function () {
        const searchController = new SearchController()
        searchController.init()
    };

    return {
        init: init
    }
})()

addEventListener('DOMContentLoaded', () => {
    searchResult.init()
})


