const USER_URI = `/api${location.pathname}`
const DEFAULT_PERSON_IMAGE_URL = (number) => `/images/default/person${number}.jpg`

const profile = document.getElementById('profile')
const details = document.getElementById('details')
const blockTypes = ['introduce', 'pictures', 'friends']

const feedInitLoad = () => {
    api.GET(USER_URI)
        .then(res => res.json())
        .then(user => {
            profile.style.backgroundImage = `url(${user.profile === undefined ? 
                DEFAULT_PERSON_IMAGE_URL(generateRandomNumber(4)) : user.profile})`
            loadWriteForm(user)
            return user
        })
        .then(user => {
            blockTypes.forEach(type => {
                const block = details.querySelector(`.${type} .card-block`)
                const template = feedTemplates[type](user)
                block.appendChild(wrapperTemplate(template))
            })
        })
        .catch(error => console.error(error))
}

const loadWriteForm = (receiver) => {
    const writeContainer = document.getElementById("write-post")
    writeContainer.innerHTML = writeFormTemplate(receiver)
}

const generateRandomNumber = (bound) => Math.floor(Math.random() * bound) + 1

feedInitLoad()
