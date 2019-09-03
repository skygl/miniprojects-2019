const locationArray = location.pathname.split("/");

const MYPAGE_URI = `/api${location.pathname}/mypage`
const MYPAGE_POST_URI = `/api/posts/${locationArray[locationArray.length - 1]}/mypage`
const INTRODUCTION_URI = `/api${location.pathname}/introduction`
const DEFAULT_PERSON_IMAGE_URL = (number) => `/images/default/person${number}.jpg`
const DEFAULT_COVER_IMAGE_URL = '/images/default/chelsea.png'

const profile = document.getElementById('profile')
const cover = document.getElementById('cover')
const details = document.getElementById('details')
const blockTypes = ['introduce', 'pictures', 'friends']

const renderProfile = (uploadedProfile) => {
    profile.style.backgroundImage = `url(${(uploadedProfile === undefined) || (uploadedProfile === null) ?
        DEFAULT_PERSON_IMAGE_URL(generateRandomNumber(4)) : uploadedProfile.path})`
}

const renderCover = (uploadedCover) => {
    cover.style.backgroundImage = `url(${(uploadedCover === undefined) || (uploadedCover === null) ?
        DEFAULT_COVER_IMAGE_URL : uploadedCover.path})`
}

const feedInitLoad = async () => {
    const introduction = await api.GET(INTRODUCTION_URI)
        .then(res => res.json())
        .catch(error => console.error(error))

    api.GET(MYPAGE_URI)
        .then(res => res.json())
        .then(dto => {
            renderProfile(dto.user.profile)
            renderCover(dto.cover)
            loadWriteForm(dto.user)
            return dto
        })
        .then(user => {
            blockTypes.forEach(type => {
                const block = details.querySelector(`.${type} .card-block`)
                let template
                if (type === 'introduce') {
                    template = feedTemplates[type](user, introduction)
                    block.appendChild(wrapperTemplate(template))
                    if (localStorage.loginUserId == introduction.userId) {
                        block.appendChild(wrapperTemplate(updateForm))
                        block.appendChild(wrapperTemplate(updateIntroductionBtn))
                        addUpdateListener()
                    }
                } else {
                    template = feedTemplates[type](user)
                    block.appendChild(wrapperTemplate(template))
                }
            })
        })
        .catch(error => console.error(error))
}

const loadWriteForm = (receiver) => {
    const writeContainer = document.getElementById("write-post")
    writeContainer.innerHTML = writeFormTemplate(receiver)
}

const addUpdateListener = () => {
    const item_map = {
        'company': '회사',
        'currentCity': '거주지',
        'education': '학교',
        'hometown': '출신지'
    }

    const update_btn = document.getElementById("introduction-update-btn")
    const display_btn = document.getElementById("introduction-display-btn")
    const display_card = document.getElementById("display-card")
    const update_card = document.getElementById("update-card")

    update_btn.addEventListener('click', function (event) {
        api.GET(INTRODUCTION_URI)
            .then(res => res.json())
            .then(introduction => {
                document.getElementById("userId").value = introduction.userId
                document.getElementById("education-input").value = checkNull(introduction.education)
                document.getElementById("current-city-input").value = checkNull(introduction.currentCity)
                document.getElementById("hometown-input").value = checkNull(introduction.hometown)
                document.getElementById("company-input").value = checkNull(introduction.company)

                display_card.classList.remove('display-block')
                update_btn.classList.remove('display-block')

                update_card.classList.add('display-block')
                display_btn.classList.add('display-block')
            })
    })
    display_btn.addEventListener('click', function () {
        const updateIntroductionRequest = serializeObject($('#up'))
        api.PUT(INTRODUCTION_URI, updateIntroductionRequest)
            .then(res => res.json())
            .then(introduction => {
                for (let key in item_map) {
                    if (introduction[key] == null || introduction[key] == "") {
                        introduction[key] = item_map[key] + '를 입력해주세요.'
                    }
                }
                document.getElementById("education-card").innerHTML = checkNull(introduction.education)
                document.getElementById("current-city-card").innerHTML = checkNull(introduction.currentCity)
                document.getElementById("hometown-card").innerHTML = checkNull(introduction.hometown)
                document.getElementById("company-card").innerHTML = checkNull(introduction.company)

                display_card.classList.add('display-block')
                update_btn.classList.add('display-block')

                update_card.classList.remove('display-block')
                display_btn.classList.remove('display-block')
            })
    })
}

feedInitLoad()

const checkNull = (string) => {
    if (!string) {
        return ""
    }
    return string
}