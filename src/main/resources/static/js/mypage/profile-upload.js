const UPLOAD_URL = (userId) => `/api/users/${userId}/upload`

// profile
const myProfile = document.querySelector('#profile')
const profileForm = document.querySelector('#profile-modal input[type="file"]')
const profileModal = document.getElementById('profile-modal')
const profileUploadBtn = document.querySelector('#profile-modal .modal-body button')

// cover
const coverModalBtn = document.querySelector('.mypage-header .cover-modal-btn')
const coverForm = document.querySelector('#cover-modal input[type="file"]')
const coverModal = document.getElementById('cover-modal')
const coverUploadBtn = document.querySelector('#cover-modal .modal-body button')


const closeModal = (modalBackground) => {
    profileModal.style.display = 'none'
    coverModal.style.display = 'none'
    modalBackground.remove()
}

const profileUpload = (event) => {
    const userId = localStorage.loginUserId

    const image = profileForm.files[0]
    const profileUploadRequest = new FormData();
    profileUploadRequest.append('type', 'profile')
    profileUploadRequest.append('image', image)

    formDataApi.PUT(UPLOAD_URL(userId), profileUploadRequest)
        .then(res => res.json())
        .then(profile => {
            renderProfile(profile)
            localStorage.loginUserProfile = profile.path
            setupNavBarUserProfile()
        })
        .catch(error => console.error(error))

    const modalBackground = document.querySelector('.modal-backdrop')
    closeModal(modalBackground)
}

const coverUpload = (event) => {
    const userId = localStorage.loginUserId

    const image = coverForm.files[0]
    const coverUploadRequest = new FormData();
    coverUploadRequest.append('type', 'cover')
    coverUploadRequest.append('image', image)

    formDataApi.PUT(UPLOAD_URL(userId), coverUploadRequest)
        .then(res => res.json())
        .then(cover => renderCover(cover))
        .catch(error => console.error(error))

    const modalBackground = document.querySelector('.modal-backdrop')
    closeModal(modalBackground)
}

profileUploadBtn.addEventListener('click', profileUpload)
coverUploadBtn.addEventListener('click', coverUpload)

const showModal = (modal) => {
    const validateLoginedUserAndMyPageUser = () => {
        let path = location.pathname.split('/');
        const userId = localStorage.loginUserId
        return path[path.length - 1] !== userId
    }

    if (validateLoginedUserAndMyPageUser()) {
        alert('해당 유저가 아닙니다')
        return false;
    }

    modal.style.display = 'block'
    const modalFade = wrapperTemplate('<div class="modal-backdrop fade show"></div>')
    document.body.append(modalFade)
}

myProfile.addEventListener('click', () => showModal(profileModal))
coverModalBtn.addEventListener('click', () => showModal(coverModal))

document.body.addEventListener('click', (event) => {
    if (event.target.classList.contains('modal-backdrop')) {
        closeModal(event.target)
    }
})