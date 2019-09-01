const roomId = document.getElementById('chatWindow').dataset.id;

const initLoad = async () => {
    await api.GET("/api/messenger/" + roomId)
        .then(res => res.json())
        .then(messages => {
            messages.forEach(message => {
                showMessage(message)
            })
        })
        .catch(error => console.error(error))
}

document.addEventListener("DOMContentLoaded", function() {
    initLoad();
    WebSocket.init();
});

const WebSocket = (function() {
    const SERVER_SOCKET_API = "/websocket";
    let stompClient;

    const connect = () => {
        let socket = new SockJS(SERVER_SOCKET_API);
        stompClient = Stomp.over(socket);
        // SockJS와 stomp client를 통해 연결을 시도.
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/messenger/'+roomId, function (chat) {
                showMessage(JSON.parse(chat.body));
            })
        });
    }

    function clear(input) {
        input.value = "";
    }

    const sendMessage = () => {
        const message = document.getElementById('messageInput').value
        stompClient.send("/app/messenger/"+roomId, {}, JSON.stringify({'message': message}));
        clear(document.getElementById('messageInput'))
    }

    const init = function () {
        const sendButton = document.getElementById('sendMessage');
        sendButton.addEventListener('click', sendMessage);

        connect()
    }

    return {
        init: init,
    }
})();

const showMessage = (message) => {
    const createMessageDom = (message) => {
        const direction = isMyMessage(message.sender) ? 'right' : 'left'
        const div = document.createElement('div')
        div.innerHTML = messageTemplate
        div.querySelector('.sender').innerText = message.sender.name
        div.querySelector('.text').innerText = message.content
        div.querySelector('li').setAttribute('class', 'message appeared '+direction)
        return div.firstElementChild
    }

    const isMyMessage = (sender) => sender.id == localStorage.loginUserId

    const messages = document.getElementById('messages')
    messages.appendChild(createMessageDom(message))
}
