const searchboxContainer = document.getElementById('searchbox')
const searchDropdown = document.getElementById('search-dropdown')
const dropdownMenu = document.getElementById('dropdown-menu')

const searchbox = function (event) {
    const keyword = changeSlash(event.target.value)

    if (keyword.length === 0) {
        searchDropdown.classList.remove('show')
        return
    }

    api.GET(`/api/users/search?name=${keyword}`)
        .then(res => res.json())
        .then(users => {
            dropdownMenu.innerHTML = ''
            searchDropdown.classList.add('show')

            const items = document.createElement('div')
            items.id = 'dropdown-items'

            users.content.forEach(user => {
                const aTag = getATag(`/users/${user.id}`, searchboxResultTemplate(user))
                items.appendChild(aTag)
            })
            items.appendChild(getATag(`/search/${keyword}`, searchboxAllTemplate(keyword)))
            dropdownMenu.appendChild(items)
        })
        .catch(error => console.error(error))
}

const searchEnter = function (event) {
    const keyword = changeSlash(event.target.value)
    const enter = 13;
    if (event.keyCode !== enter) return

    location.href = `/search?keyword=${keyword}`

}

const getATag = function (location, html) {
    const aTag = document.createElement('a')
    aTag.classList.add('dropdown-item')
    aTag.href = location
    aTag.innerHTML = html
    return aTag
}

const changeSlash = function(text){
    return text.replace(/\//g, "-").replace(/\\/g, "_")
}

searchboxContainer.addEventListener('keyup', searchbox)
searchboxContainer.addEventListener('keyup', searchEnter)
