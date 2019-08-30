const feedTemplatesCreator = () => {
    const templates = {
        introduce: (user, introduction) => {
            const item_map = {
                'company' : '회사',
                'currentCity' : '거주지', 
                'education' : '학교', 
                'hometown' : '출신지'
            }

            for(let key in item_map) {
                if (!introduction[key]) {
                    introduction[key] = item_map[key] + '를 입력해주세요.'
                }
            }

            return `
            <div id="display-card" class="card-contents display-card">
                <div>
                    <i class="education-icon"></i><span class="introduction-card" id="education-card"> ${introduction.education}</span>
                </div>
                <div>
                    <i class="company-icon"></i><span class="introduction-card" id="company-card"> ${introduction.company}</span>
                </div>
                <div>
                    <i class="current-city-icon"></i><span class="introduction-card" id="current-city-card"> ${introduction.currentCity}</span>
                </div>
                <div>
                    <i class="hometown-icon"></i><span class="introduction-card" id="hometown-card"> ${introduction.hometown}</span>
                </div>
            </div>
            `
        },
        pictures: (user) => `
            <div class="card-images">
                ${user.images.length === 0 ? imageTemplate(DEFAULT_PERSON_IMAGE_URL(generateRandomNumber(4))) : multipleImageParser(user.images)}
            </div>
        `,
        friends: (user) => {
            const images = user.friends.map(friend => friend.profile)
            return `
                <div class="card-images">
                    ${multipleImageParser(images)}
                </div>
            `
        }
    }

    const imageTemplate = (imageSrc) => `
        <img src=${imageSrc} class="image">
    `
    const multipleImageParser = (images) => images.map(image => imageTemplate(
        image === null ? DEFAULT_PERSON_IMAGE_URL(generateRandomNumber(4)) : image.path)).join('')
    return templates
}

const generateRandomNumber = (bound) => Math.floor(Math.random() * bound) + 1

const wrapperTemplate = (template) => {
    const div = document.createElement('div');
    div.innerHTML = template
    return div.firstElementChild
}

const feedTemplates = feedTemplatesCreator()

const updateForm = `
<form id="up">
    <div id="update-card" class="card-contents update-card">
        <div>
            <i class="education-icon"></i>
            <input class="update-introduction-input" id="education-input" type="text" name="education" value="a"/>
        </div>
        <div>
            <i class="company-icon"></i>
            <input class="update-introduction-input" id="company-input" type="text" name="company" value="a"/>
        </div>
        <div>
            <i class="current-city-icon"></i>
            <input class="update-introduction-input" id="current-city-input" type="text" name="currentCity" value="a"/>
        </div>
        <div>
            <i class="hometown-icon"></i>
            <input class="update-introduction-input" id="hometown-input" type="text" name="hometown" value="a"/>
        </div>
        <input id="userId" name="userId" type="hidden" value="a"/>
    </div>
</form>
`

const updateIntroductionBtn = (introduction) => `
<div>
    <button id="introduction-update-btn"class="introduction-update-btn" data-id=${introduction.id}>수정하기</button>
    <button id="introduction-display-btn"class="introduction-display-btn" data-id=${introduction.id}>수정완료</button>  
</div>
`
