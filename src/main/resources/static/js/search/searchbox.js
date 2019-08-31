const searchboxContainer = document.getElementById('searchbox')
const searchDropdown = document.getElementById('search-dropdown')
const dropdownMenu = document.getElementById('dropdown-menu')

const searchbox = function (event) {
    const keyword = event.target.value

    if (keyword.length === 0) {
        searchDropdown.classList.remove('show')
        return
    }

    api.GET(`/api/search/${keyword}`)
        .then(res => res.json())
        .then(users => {
            console.log(users)
            dropdownMenu.innerHTML = ''
            searchDropdown.classList.add('show')

            const items = document.createElement('div')
            items.id = 'dropdown-items'

            users.forEach(user => {
                console.log(user)
                const aTag = getATag(`/users/${user.id}`, searchboxResultTemplate(user))
                items.appendChild(aTag)
            })
            items.appendChild(getATag(`/search/${keyword}`, searchboxAllTemplate(keyword)))
            dropdownMenu.appendChild(items)
        })
        .catch(error => console.error(error))
}

const searchEnter = function (event) {
    const keyword = event.target.value
    const enter = 13;
    if (event.keyCode !== enter) return

    location.href = `/search/${keyword}`

}

const getATag = function (location, html) {
    const aTag = document.createElement('a')
    aTag.classList.add('dropdown-item')
    aTag.href = location
    aTag.innerHTML = html
    return aTag
}

searchboxContainer.addEventListener('keyup', searchbox)
searchboxContainer.addEventListener('keyup', searchEnter)
