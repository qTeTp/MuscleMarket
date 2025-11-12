const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');

// 검색 실행 함수
function performSearch() {
    const keyword = searchInput.value.trim();
    if (keyword) {
        // ✅ /products/search 경로로 키워드를 쿼리 파라미터로 추가하여 이동
        window.location.href = `/products/search?keyword=${encodeURIComponent(keyword)}`;
    }
}
// 버튼에 검색
searchButton.addEventListener('click', performSearch);

// 엔터로 검색
searchInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault(); // 기본 폼 제출 방지
        performSearch();
    }
});


// 로그아웃 버튼 구현
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.querySelector('.logout-btn');
    if (!logoutBtn) {
        console.error("logout-btn 버튼을 찾지 못했습니다.");
        return;
    }

    logoutBtn.addEventListener('click', async () => {
        if (!confirm('정말 로그아웃 하시겠습니까?')) return;

        try {
            const res = await fetch('/api/logout', {
                method: 'POST',
                credentials: 'include' // 쿠키 포함
            });

            if (res.ok) {
                alert('로그아웃 완료!');
                window.location.href = '/login'; // 로그인페이지로 리디렉션
            } else {
                const text = await res.text();
                alert('로그아웃 실패: ' + text);
            }
        } catch (err) {
            console.error('로그아웃 요청 실패:', err);
            alert('서버 오류가 발생했습니다.');
        }
    });
});
