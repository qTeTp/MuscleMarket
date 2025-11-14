// /js/alan-chat.js 파일
document.addEventListener('DOMContentLoaded', () => {
    // 최초에 입력 필드에 포커스
    document.getElementById('user-input').focus();
});

/**
 * 챗봇에게 메시지를 전송하고 응답을 받아옵니다.
 */
function sendChat() {
    const userInput = document.getElementById('user-input');
    const content = userInput.value.trim();

    if (content === "") {
        alert("질문을 입력해주세요.");
        return;
    }

    // 1. 사용자 메시지를 화면에 표시
    appendMessage(content, 'user');
    userInput.value = ''; // 입력창 비우기

    // 로딩 메시지 표시
    const loadingMessageId = 'loading-' + Date.now();
    appendLoadingMessage(loadingMessageId);

    // 2. /api/alan/chat API 호출
    fetch(`/api/alan/chat?content=${encodeURIComponent(content)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            // 로딩 메시지 제거
            removeLoadingMessage(loadingMessageId);

            if (!response.ok) {
                throw new Error('API 호출 실패: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            // 3. 앨런 답변 화면에 표시
            if (data.alanAnswer) {
                appendMessage(data.alanAnswer, 'alan');
            } else {
                appendMessage("죄송합니다. 답변을 받아오지 못했습니다.", 'alan');
            }

            // 4. 추천 상품 목록 갱신
            updateRecommendationList(data.recommendProducts);
        })
        .catch(error => {
            // 로딩 메시지 제거
            removeLoadingMessage(loadingMessageId);
            console.error('채팅 중 오류 발생:', error);
            appendMessage("통신 중 오류가 발생했습니다.", 'alan');
        });
}

/**
 * 앨런 상태 초기화 API를 호출합니다.
 */
function resetAlanState() {
    if (!confirm("앨런과의 대화를 초기화하시겠습니까?")) {
        return;
    }

    fetch('/api/alan/reset', {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('초기화 API 호출 실패');
            }
            return response.text();
        })
        .then(message => {
            alert(message);
            // 채팅창 비우고 초기 환영 메시지 표시
            const chatBox = document.getElementById('chat-box');
            chatBox.innerHTML = `
            <div class="message alan-message">
                <span class="sender">앨런</span>
                <div class="content">무엇을 도와드릴까요? 스포츠 용품 추천을 요청해보세요!</div>
            </div>`;

            // 추천 상품 목록 비우기
            const productList = document.getElementById('product-list');
            productList.innerHTML = `<div class="no-recommendation">아직 추천 상품이 없습니다.</div>`;

        })
        .catch(error => {
            console.error('상태 초기화 중 오류 발생:', error);
            alert("상태 초기화에 실패했습니다.");
        });
}

/**
 * 채팅 박스에 메시지를 추가합니다.
 * @param {string} text - 메시지 내용
 * @param {string} sender - 'user' 또는 'alan'
 */
function appendMessage(text, sender) {
    const chatBox = document.getElementById('chat-box');

    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message');
    messageDiv.classList.add(sender + '-message');

    const senderSpan = document.createElement('span');
    senderSpan.classList.add('sender');
    senderSpan.textContent = sender === 'alan' ? '앨런' : '나';

    const contentDiv = document.createElement('div');
    contentDiv.classList.add('content');

    // **...** 강조된 부분 찾아서 굵게 표시 (마크다운 스타일)
    // Spring Service 코드에서 백슬래시 2개(\\**...\\**)를 사용하여 이스케이프했으므로,
    // 여기서도 해당 패턴을 처리할 수 있도록 정규식 수정이 필요할 수 있습니다.
    // 일단 일반 마크다운 **...** 처리로 가정하고 UI를 구성합니다.
    let formattedText = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

    contentDiv.innerHTML = formattedText;

    messageDiv.appendChild(senderSpan);
    messageDiv.appendChild(contentDiv);
    chatBox.appendChild(messageDiv);

    // 스크롤 최하단으로 이동
    chatBox.scrollTop = chatBox.scrollHeight;
}

/**
 * 로딩 중 메시지를 표시합니다.
 * @param {string} id - 로딩 메시지의 고유 ID
 */
function appendLoadingMessage(id) {
    const chatBox = document.getElementById('chat-box');
    const loadingDiv = document.createElement('div');
    loadingDiv.classList.add('message', 'alan-message', 'loading-message');
    loadingDiv.id = id;
    loadingDiv.innerHTML = '<span class="sender">앨런</span><div class="content">답변을 생각하는 중...</div>';
    chatBox.appendChild(loadingDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

/**
 * 로딩 메시지를 제거합니다.
 * @param {string} id - 로딩 메시지의 고유 ID
 */
function removeLoadingMessage(id) {
    const loadingMessage = document.getElementById(id);
    if (loadingMessage) {
        loadingMessage.remove();
    }
}


/**
 * 추천 상품 리스트를 업데이트합니다.
 * @param {Array<Object>} products - 추천 상품 객체 리스트 (Product DTO 구조)
 */
function updateRecommendationList(products) {
    const productList = document.getElementById('product-list');
    productList.innerHTML = ''; // 기존 목록 초기화

    if (!products || products.length === 0) {
        productList.innerHTML = `<div class="no-recommendation">추천 상품을 찾지 못했습니다.</div>`;
        return;
    }

    products.forEach(product => {
        const card = document.createElement('div');
        card.classList.add('product-card');

        // Product DTO 필드에 맞게 구성
        card.innerHTML = `
            <div class="product-card-image">
                
            </div>
            <div class="product-card-info">
                <div class="product-card-title">${product.title || '상품 제목 없음'}</div>
                <div class="product-card-price">${(product.price || 0).toLocaleString()}원</div>
                <a href="/products/${product.id}" style="display: block; margin-top: 10px; text-align: right; color: #007bff;">자세히 보기</a>
            </div>
        `;
        productList.appendChild(card);
    });
}