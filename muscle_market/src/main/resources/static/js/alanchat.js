document.addEventListener('DOMContentLoaded', () => {
    const userInput = document.getElementById('user-input');
    userInput.focus();

    // Enter 키 이벤트 - 한글 IME 처리 포함
    userInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter' && !event.isComposing) {
            event.preventDefault();
            sendChat();
        }
    });
});

/* -------------------- 채팅 전송 -------------------- */
function sendChat() {
    const userInput = document.getElementById('user-input');
    const content = userInput.value.trim();

    if (content === "") {
        alert("질문을 입력해주세요.");
        return;
    }

    appendMessage(content, 'user');
    userInput.value = '';

    const loadingMessageId = 'loading-' + Date.now();
    appendLoadingMessage(loadingMessageId);

    // GET 방식으로 전송 (CSRF 토큰 불필요)
    fetch(`/api/alan/chat?content=${encodeURIComponent(content)}`)
        .then(response => {
            removeLoadingMessage(loadingMessageId);
            if (!response.ok) throw new Error('API 호출 실패');
            return response.json();
        })
        .then(data => {
            appendMessage(data.alanAnswer || "답변을 받아오지 못했습니다.", 'alan');
            updateRecommendationList(data.recommendProducts);
        })
        .catch(error => {
            removeLoadingMessage(loadingMessageId);
            console.error(error);
            appendMessage("통신 오류가 발생했습니다.", 'alan');
        });
}

/* -------------------- 앨런 상태 초기화 -------------------- */
function resetAlanState() {
    if (!confirm("대화를 초기화하시겠습니까?")) return;

    // DELETE 요청은 CSRF 토큰 필요
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/api/alan/reset', {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
        .then(res => {
            if (!res.ok) throw new Error('초기화 실패');
            return res.text();
        })
        .then(msg => {
            alert(msg);

            document.getElementById('chat-box').innerHTML = `
                <div class="message alan-message">
                    <span class="sender">앨런</span>
                    <div class="content">무엇을 도와드릴까요? 스포츠 용품 추천을 요청해보세요!</div>
                </div>`;

            document.getElementById('product-list').innerHTML =
                `<div class="no-recommendation">아직 추천 상품이 없습니다.</div>`;
        })
        .catch(error => {
            console.error('Error:', error);
            alert(`초기화 실패: ${error.message}`);
        });
}

/* -------------------- 메시지 UI -------------------- */
function appendMessage(text, sender) {
    const chatBox = document.getElementById('chat-box');

    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', sender + '-message');

    const senderSpan = document.createElement('span');
    senderSpan.classList.add('sender');
    senderSpan.textContent = sender === 'alan' ? '앨런' : '나';

    const contentDiv = document.createElement('div');
    contentDiv.classList.add('content');
    contentDiv.innerHTML = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

    messageDiv.appendChild(senderSpan);
    messageDiv.appendChild(contentDiv);
    chatBox.appendChild(messageDiv);

    chatBox.scrollTop = chatBox.scrollHeight;
}

function appendLoadingMessage(id) {
    const chatBox = document.getElementById('chat-box');
    const loadDiv = document.createElement('div');
    loadDiv.id = id;
    loadDiv.classList.add('message', 'alan-message', 'loading-message');
    loadDiv.innerHTML = `<span class='sender'>앨런</span><div class='content'>답변을 생각하는 중...</div>`;
    chatBox.appendChild(loadDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function removeLoadingMessage(id) {
    const target = document.getElementById(id);
    if (target) target.remove();
}

/* -------------------- 추천 상품 UI -------------------- */
function updateRecommendationList(products) {
    const productList = document.getElementById('product-list');
    productList.innerHTML = '';

    if (!products || products.length === 0) {
        productList.innerHTML = `<div class="no-recommendation">추천 상품을 찾지 못했습니다.</div>`;
        return;
    }

    products.forEach(product => {
        const card = document.createElement('div');
        card.classList.add('product-card');

        const firstImage = product.imageUrls?.[0] || '/img/no-image.png';

        card.innerHTML = `
            <div class="product-card-image">
                <img src="${firstImage}" alt="상품 이미지" />
            </div>

            <div class="product-card-info">
                <div class="product-card-title">
                    ${product.title || '상품 제목 없음'}
                </div>

                <div class="product-card-price">
                    ${(product.price || 0).toLocaleString()}원
                </div>

                <a href="/products/${product.id}" class="detail-link">
                    자세히 보기
                </a>
            </div>
        `;

        productList.appendChild(card);
    });
}