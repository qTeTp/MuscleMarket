const SocketManager = {
    stompClient: null,
    currentUser: null,
    subscriptions: new Map(),

    init: function(user) {
        if (!user || !user.userId) {
            console.log('사용자가 없어 socket 연결을 하지 않습니다');
            return;
        }
        this.currentUser = user;
        this.connect();
    },

    connect: function() {
        const socket = new SockJS('/ws-stomp');
        this.stompClient = Stomp.over(socket);

        // 개발자 모드 해제
        this.stompClient.debug = null;

        this.stompClient.connect({},
            () => this.onConnected(),
            (error) => this.onError(error)
        );
    },

    onConnected: function() {
        console.log("Connected!");

        // 유저별 메시지 구독 채널
        const userSubUrl = `/sub/users/${this.currentUser.userId}`;
        this.subscribeTo(userSubUrl, (payload) => this.onNotificationReceived(payload));
    },

    subscribeTo: function(url, callback) {
        if (this.stompClient && this.stompClient.connected) {
            // 이미 연결이 되어있으면 바로 구독
            const subscription = this.stompClient.subscribe(url, callback);
            this.subscriptions.set(url, subscription);
            console.log(`${url} subscribing`);
        } else {
            // 아직 연결이 되지 않았으면 연결하고 구독
            // 잠시 대기를 걸고 구독 시도
            setTimeout(() => this.subscribeTo(url, callback), 500);
            console.log(`${url} subscribing`);
        }
    },

    unsubscribeFrom: function(url) {
        const subscription = this.subscriptions.get(url);
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(url);
            console.log(`${url} unsubscribed`);
        }
    },

    onError: function(error) {
        console.error('Error: ', error);
        setTimeout(() => {
            console.log('Reconnecting...');
            this.connect();
        }, 5000);
    },

    onNotificationReceived: function(payload) {
        console.log('New notification received: ', payload.body);
        const updateInfo = JSON.parse(payload.body);

        const event = new CustomEvent('chatNotification', {
            detail: updateInfo
        });

        // document 전체에 이벤트 발생
        document.dispatchEvent(event);
    },

    sendMessage: function(chatId, content) {
        if (this.stompClient && this.stompClient.connected && content && content.trim() !== '') {
            this.stompClient.send("/pub/chats/messages",
                {},
                JSON.stringify({
                    chatId: chatId,
                    userId: this.currentUser.userId,
                    content: content
                })
            );
            console.log(`메시지 전송 to ${chatId}: ${content}`);
        } else {
            console.error('소켓이 연결되지 않았거나 메시지 내용이 없습니다');
        }
    },

    disconnect: function() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            console.log('Disconnected!');
        }
    }
};